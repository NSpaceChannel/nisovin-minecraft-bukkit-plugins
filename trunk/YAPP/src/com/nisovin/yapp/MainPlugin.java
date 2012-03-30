package com.nisovin.yapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.nisovin.yapp.menu.Menu;

public class MainPlugin extends JavaPlugin {
	
	public static ChatColor TEXT_COLOR = ChatColor.GOLD;
	public static ChatColor HIGHLIGHT_COLOR = ChatColor.YELLOW;
	public static ChatColor ERROR_COLOR = ChatColor.DARK_RED;
	
	public static MainPlugin yapp;
	
	private static boolean debug = true;
	private boolean updatePlayerList = true;
	private boolean setPlayerGroupPerm = false;
	private boolean setPlayerMetadata = false;

	private Map<String, Group> groups;
	private Map<String, User> players;
	private Group defaultGroup;
	private Map<String, List<Group>> ladders;
	
	private Map<String, PermissionAttachment> attachments;
	
	@Override
	public void onEnable() {
		yapp = this;
		
		load();
		
		// register commands
		getCommand("yapp").setExecutor(new CommandMain());
		CommandPromoteDemote cpd = new CommandPromoteDemote(this);
		getCommand("yapppromote").setExecutor(cpd);
		getCommand("yappdemote").setExecutor(cpd);
		
		// register vault hook
		if (getServer().getPluginManager().isPluginEnabled("Vault")) {
			getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, new VaultService(), this, ServicePriority.Highest);
		}
	}
	
	private void load() {
		// get data folder
		File folder = getDataFolder();
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		// get config
		File configFile = new File(folder, "config.txt");
		if (!configFile.exists()) {
			this.saveResource("config.txt", false);
		}
		SimpleConfig config = new SimpleConfig(configFile);
		debug = config.getboolean("general.debug");
		updatePlayerList = config.getboolean("general.update player list");
		setPlayerGroupPerm = config.getboolean("general.set group perm");
		setPlayerMetadata = config.getboolean("general.set player metadata");
		boolean modalMenu = config.getboolean("general.modal menu");
		String defGroupName = config.getString("general.default group");
				
		// load all group data
		loadGroups();
		
		// get default group
		if (defGroupName != null && !defGroupName.isEmpty()) {
			defaultGroup = getGroup(defGroupName);
			if (defaultGroup == null) {
				// create default group
				defaultGroup = newGroup(defGroupName);
				defaultGroup.addPermission(null, "yapp.build");
				defaultGroup.save();
				log("Created default group '" + defGroupName + "'");
			}
		}
		
		// get promotion ladders
		ladders = new HashMap<String, List<Group>>();
		Set<String> keys = config.getKeys("ladders");
		if (keys != null) {
			for (String key : keys) {
				List<String> groupList = config.getStringList("ladders." + key);
				List<Group> ladderGroups = new ArrayList<Group>();
				for (String s : groupList) {
					Group g = getGroup(s);
					if (g != null) {
						ladderGroups.add(g);
					} else {
						error("Group '" + s + "' in ladder '" + key + "' does not exist, ignoring this ladder");
						ladderGroups = null;
						break;
					}
				}
				if (ladderGroups != null) {
					ladders.put(key, ladderGroups);
				}
			}
		}
		
		// load logged in players
		players = new HashMap<String, User>();
		attachments = new HashMap<String, PermissionAttachment>();
		for (Player player : getServer().getOnlinePlayers()) {
			loadPlayerPermissions(player);
		}
		
		// register listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PermListener(this), this);
		if (config.getboolean("general.use build perm")) {
			pm.registerEvents(new BuildListener(), this);
		}
		if (config.getboolean("general.use chat formatting")) {
			pm.registerEvents(new ChatListener(), this);
		}
		
		// create converation factory
		Menu.initializeFactory(this, modalMenu);
	}
	
	private void unload() {
		saveAll();
		
		groups.clear();
		groups = null;
		players.clear();
		players = null;
		
		for (PermissionAttachment attachment : attachments.values()) {
			attachment.remove();
		}
		attachments.clear();
		attachments = null;
		
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public void onDisable() {
		unload();
		
		getServer().getServicesManager().unregisterAll(this);
		Menu.closeAllMenus();
		
		yapp = null;
	}
	
	public void reload() {
		unload();
		load();
	}
	
	public void cleanup() {
		Iterator<Map.Entry<String,User>> iter = players.entrySet().iterator();
		Map.Entry<String,User> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.getValue().getPlayer() == null) {
				entry.getValue().save();
				iter.remove();
			}
		}
		
		Iterator<Map.Entry<String,PermissionAttachment>> iter2 = attachments.entrySet().iterator();
		Map.Entry<String,PermissionAttachment> entry2;
		while (iter2.hasNext()) {
			entry2 = iter2.next();
			if (Bukkit.getPlayerExact(entry2.getKey()) == null) {
				entry2.getValue().remove();
				iter2.remove();
			}
		}
	}
	
	public void loadGroups() {
		debug("Loading groups...");
		groups = new TreeMap<String, Group>();
		
		// get groups from group folder
		File groupsFolder = new File(getDataFolder(), "groups");
		if (groupsFolder.exists() && groupsFolder.isDirectory()) {
			File[] groupFiles = groupsFolder.listFiles();
			for (File f : groupFiles) {
				if (f.getName().endsWith(".txt")) {
					String name = f.getName().replace(".txt", "");
					if (!groups.containsKey(name)) {
						Group group = new Group(name);
						groups.put(name, group);
						debug("  Found group: " + name);
					}
				}
			}
		}
		
		// get groups from world group folders
		File worldsFolder = new File(getDataFolder(), "worlds");
		if (worldsFolder.exists() && worldsFolder.isDirectory()) {
			File[] worldFolders = worldsFolder.listFiles();
			for (File wf : worldFolders) {
				if (wf.isDirectory()) {
					File worldGroupsFolder = new File(wf, "groups");
					if (worldGroupsFolder.exists() && worldGroupsFolder.isDirectory()) {
						File[] groupFiles = worldGroupsFolder.listFiles();
						for (File f : groupFiles) {
							if (f.getName().endsWith(".txt")) {
								String name = f.getName().replace(".txt", "");
								if (!groups.containsKey(name)) {
									Group group = new Group(name);
									groups.put(name, group);
									debug("  Found group: " + name);
								}
							}
						}
					}
				}
			}
		}
		
		// load group data
		for (Group group : groups.values()) {
			group.loadFromFiles();
		}
	}
	
	public static User getPlayerUser(String playerName) {
		User user = yapp.players.get(playerName);
		if (user == null) {
			user = new User(playerName);
			yapp.players.put(playerName, user);
			user.loadFromFiles();
			if (yapp.defaultGroup != null && user.getGroups(null).size() == 0) {
				user.addGroup(null, yapp.defaultGroup);
				user.save();
				debug("Added default group '" + yapp.defaultGroup.getName() + "' to player '" + playerName + "'");
			}
		}
		return user;
	}
	
	public User loadPlayerPermissions(Player player) {
		String playerName = player.getName().toLowerCase();
		String worldName = player.getWorld().getName();
		debug("Loading player permissions for " + playerName + "...");
		
		// prepare user
		User user = getPlayerUser(playerName);
		user.resetCachedInfo();
		user.getColor(worldName);
		user.getPrefix(worldName);
		Group primaryGroup = user.getPrimaryGroup(player.getWorld().getName());
		
		// prepare attachment
		PermissionAttachment attachment = attachments.get(playerName);
		if (attachment == null) {
			attachment = player.addAttachment(this);
			attachments.put(playerName, attachment);
		}
		attachment.getPermissions().clear();
		
		// load permissions
		debug("  Adding permissions");
		if (setPlayerGroupPerm && primaryGroup != null) {
			attachment.setPermission("group." + primaryGroup.getName(), true);
		}
		List<PermissionNode> nodes = user.getAllPermissions(worldName);
		for (PermissionNode node : nodes) {
			node.addTo(attachment);
			debug("    Added: " + node);
		}
		player.recalculatePermissions();
		
		// set player list color
		setPlayerListName(player, user);
		
		// set metadata
		if (setPlayerMetadata) {
			if (primaryGroup != null) {
				player.removeMetadata("group", this);
				player.setMetadata("group", new FixedMetadataValue(this, primaryGroup.getName()));
			}
		}
		
		return user;
	}
	
	private void loadAllUsers() {
		File playersFolder = new File(getDataFolder(), "players");
		String fileName, playerName;
		for (File file : playersFolder.listFiles()) {
			fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".txt")) {
				playerName = fileName.replace(".txt", "");
				User user = getPlayerUser(playerName);
				players.put(playerName, user);
			}
		}
	}
	
	public void renameOrDeleteGroup(Group group, String newName) {
		// create new group as copy of old
		Group newGroup = null;
		if (newName != null && !newName.isEmpty()) {
			newGroup = new Group(group, newName);
			newGroup.save();
		}
		
		// replace group in groups
		for (Group g : groups.values()) {
			g.replaceGroup(group, newGroup);
		}
		
		// replace group in players
		loadAllUsers();
		for (User u : players.values()) {
			u.replaceGroup(group, newGroup);
		}
		
		// save and clean up
		saveAll();
		cleanup();
		
		// remove old group
		String oldName = group.getName();
		groups.remove(oldName.toLowerCase());
		File file = new File(getDataFolder(), "groups" + File.separator + oldName + ".txt");
		if (file.exists()) {
			file.delete();
		}
		File worldsFolder = new File(getDataFolder(), "worlds");
		if (worldsFolder.exists()) {
			for (File f : worldsFolder.listFiles()) {
				if (f.isDirectory()) {
					file = new File(f, oldName + ".txt");
					if (file.exists()) {
						file.delete();
					}
				}
			}
		}
		
		// finally add new group
		if (newGroup != null) {
			groups.put(newName.toLowerCase(), newGroup);
		}
		
		reload();
	}
	
	public boolean promote(User user, String world, CommandSender sender) {
		List<Group> groups;
		if (world == null) {
			groups = user.getActualGroupList();
		} else {
			groups = user.getActualGroupList(world);
		}
		if (groups == null || groups.size() == 0) {
			return false;
		} else {
			Group group = groups.get(0);
			for (String ladderName : ladders.keySet()) {
				if (sender.hasPermission("yapp.promote.*") || sender.hasPermission("yapp.promote." + ladderName)) {
					List<Group> ladder = ladders.get(ladderName);
					int index = ladder.indexOf(group) + 1;
					if (index > 0 && index < ladder.size()) {
						user.replaceGroup(group, ladder.get(index));
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public boolean demote(User user, String world, CommandSender sender) {
		List<Group> groups;
		if (world == null) {
			groups = user.getActualGroupList();
		} else {
			groups = user.getActualGroupList(world);
		}
		if (groups == null || groups.size() == 0) {
			return false;
		} else {
			Group group = groups.get(0);
			for (String ladderName : ladders.keySet()) {
				if (sender.hasPermission("yapp.demote.*") || sender.hasPermission("yapp.demote." + ladderName)) {
					List<Group> ladder = ladders.get(ladderName);
					int index = ladder.indexOf(group) - 1;
					if (index >= 0) {
						user.replaceGroup(group, ladder.get(index));
						return true;
					}
				}
			}
			return false;
		}
	}
		
	public void setPlayerListName(Player player, User user) {
		if (updatePlayerList) {
			player.setPlayerListName(user.getColor() + player.getName());
		}		
	}
	
	public void unloadPlayer(Player player) {
		String playerName = player.getName().toLowerCase();
		players.remove(playerName).save();
		attachments.remove(playerName).remove();
	}
	
	public void saveAll() {
		for (User user : players.values()) {
			user.save();
		}
		for (Group group : groups.values()) {
			group.save();
		}
	}
	
	public static Group newGroup(String name) {
		Group group = new Group(name);
		yapp.groups.put(name.toLowerCase(), group);
		return group;
	}
	
	public static Group getGroup(String name) {
		return yapp.groups.get(name.toLowerCase());
	}
	
	public static Set<String> getGroupNames() {
		return yapp.groups.keySet();
	}
	
	public static void log(String message) {
		yapp.getLogger().info(message);
	}
	
	public static void warning(String message) {
		yapp.getLogger().warning(message);
	}
	
	public static void error(String message) {
		yapp.getLogger().severe(message);
	}
	
	public static void debug(String message) {
		if (debug) {
			yapp.getLogger().info(message);
		}
	}
	
}