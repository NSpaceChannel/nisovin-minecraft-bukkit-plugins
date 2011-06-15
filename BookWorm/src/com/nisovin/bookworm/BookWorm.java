package com.nisovin.bookworm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class BookWorm extends JavaPlugin {

	protected static ChatColor TEXT_COLOR = ChatColor.GREEN;
	protected static int CLEAN_INTERVAL = 600;
	protected static int REMOVE_DELAY = 300;
	protected static boolean CONSUME_BOOK = true;
	protected static boolean CHECK_WORLDGUARD = true;
	
	protected static int LINE_LENGTH = 55;
	protected static int PAGE_LENGTH = 6;
	protected static String INDENT = "    ";
	protected static String NEW_PARA = "::";
	
	protected static BookWorm plugin;
	protected HashMap<String,Book> books;
	protected HashMap<String,Bookmark> bookmarks;
	protected HashMap<String,NewBook> newBooks;
	protected BookUnloader unloader;
	protected WorldGuardPlugin worldGuard;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		books = new HashMap<String,Book>();
		bookmarks = new HashMap<String,Bookmark>();
		newBooks = new HashMap<String,NewBook>();
		
		loadBooks();
		loadConfig();
		
		new BookWormPlayerListener(this);
		new BookWormBlockListener(this);
		
		if (CLEAN_INTERVAL > 0) {
			unloader = new BookUnloader(this);
		}
		
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin != null) {
			worldGuard = (WorldGuardPlugin)plugin;
		}
		
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
		if (sender.isOp() && args.length == 1 && args[0].equals("-reload")) {
			books.clear();
			bookmarks.clear();
			loadBooks();
			sender.sendMessage("BookWorm data reloaded.");
		} else if (sender instanceof Player) {
			Player player = (Player)sender;
			
			if (player.getItemInHand().getType() != Material.BOOK) {
				player.sendMessage(TEXT_COLOR + "You must be holding a book to write.");
			} else if (args.length == 0) {
				// must have args
				if (!newBooks.containsKey(player.getName())) {
					player.sendMessage(TEXT_COLOR + "Use /" + label + " <title> to start your book.");
				} else {
					player.sendMessage(TEXT_COLOR + "Use /" + label + " <text> to add text to your book.");
					player.sendMessage(TEXT_COLOR + "You can use a double-colon :: to create a new paragraph.");
					player.sendMessage(TEXT_COLOR + "Right click on a bookcase to save your book.");
					player.sendMessage(TEXT_COLOR + "Type /" + label + " -help to see special commands.");
				}
			} else if (!newBooks.containsKey(player.getName())) {
				if (args[0].startsWith("-")) {
					if (args[0].equalsIgnoreCase("-page") && args.length == 2 && args[1].matches("[0-9]+") && bookmarks.containsKey(player.getName())) {
						int page = Integer.parseInt(args[1]) - 1;
						if (page >= 0) {
							bookmarks.get(player.getName()).page = page;
							player.sendMessage(TEXT_COLOR + "Turned to page " + page + ".");
						}
					} else {
						player.sendMessage(TEXT_COLOR + "Invalid command.");
					}
				} else {
					NewBook newBook = new NewBook(player, args);
					newBooks.put(player.getName(), newBook);
					player.sendMessage(TEXT_COLOR + "New book created: " + ChatColor.WHITE + newBook.getTitle());
				}
			} else {
				NewBook newBook = newBooks.get(player.getName());
				if (args[0].startsWith("-")) {
					// special command
					if (args[0].equalsIgnoreCase("-help")) {
						player.sendMessage(TEXT_COLOR + "Special commands:");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -read <page>" + ChatColor.WHITE + " -- read the specified page");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -title <title>" + ChatColor.WHITE + " -- change the title");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -erase <text>" + ChatColor.WHITE + " -- erase the given text");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -replace <old text> -> <new text>" + ChatColor.WHITE + " -- replace text");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -startover" + ChatColor.WHITE + " -- erase the entire book");
						player.sendMessage(TEXT_COLOR + "   /" + label + " -cancel" + ChatColor.WHITE + " -- cancel book creation");
					} else if (args[0].equalsIgnoreCase("-read")) { 
						if (args.length == 2 && args[1].matches("[0-9]+")) {
							newBook.read(player, Integer.parseInt(args[1])-1);
						} else {
							newBook.read(player, 0);
						}
					} else if (args[0].equalsIgnoreCase("-title") && args.length > 1) {
						String title = "";
						for (int i = 1; i < args.length; i++) {
							title += args[i] + " ";
						}
						newBook.setTitle(title.trim());
						player.sendMessage(TEXT_COLOR + "Title changed: " + ChatColor.WHITE + title);
					} else if (args[0].equalsIgnoreCase("-erase") && args.length > 1) {
						String s = "";
						for (int i = 1; i < args.length; i++) {
							s += args[i] + " ";
						}
						newBook.delete(s.trim());
						player.sendMessage(TEXT_COLOR + "String " + ChatColor.WHITE + s + TEXT_COLOR + " erased from book.");
					} else if (args[0].equalsIgnoreCase("-replace") && args.length > 1) {
						String s = "";
						for (int i = 1; i < args.length; i++) {
							s += args[i] + " ";
						}
						s = s.trim();
						newBook.replace(s.trim());
						player.sendMessage(TEXT_COLOR + "String replaced.");
					} else if (args[0].equalsIgnoreCase("-startover")) {
						newBook.erase();
						player.sendMessage(TEXT_COLOR + "Book contents erased.");
					} else if (args[0].equalsIgnoreCase("-cancel")) {
						newBooks.remove(player.getName());
						player.sendMessage(TEXT_COLOR + "Book cancelled.");
					} else {
						player.sendMessage(TEXT_COLOR + "Invalid command.");
					}
				} else {
					// just writing
					String line = newBook.write(args);
					player.sendMessage(TEXT_COLOR + "Wrote: " +ChatColor.WHITE + line);
				}
			}
		}
		return true;
	}
	
	private void loadBooks() {		
		try {
			Scanner scanner = new Scanner(new File(getDataFolder(), "books.txt"));
			while (scanner.hasNext()) {
				String[] line = scanner.nextLine().split(":");
				books.put(line[0], new Book(line[1]));
			}
			scanner.close();
		} catch (FileNotFoundException e) {			
		}
	}
	
	private void loadConfig() {
		Configuration config = getConfiguration();
		config.load();
		TEXT_COLOR = ChatColor.getByCode(config.getInt("general.text-color", ChatColor.GREEN.getCode()));
		LINE_LENGTH = config.getInt("formatting.line-length", LINE_LENGTH);
		PAGE_LENGTH = config.getInt("formatting.page-length", PAGE_LENGTH);
		int indent = config.getInt("formatting.indent-size", INDENT.length());
		INDENT = "";
		for (int i = 0; i < indent; i++) {
			INDENT += " ";
		}
		CLEAN_INTERVAL = config.getInt("general.clean-interval", CLEAN_INTERVAL);
		REMOVE_DELAY = config.getInt("general.remove-delay", REMOVE_DELAY);
		config.save();
	}
	
	protected void saveAll() {
		PrintWriter writer = null;
		try {
			// append entry to book list
			writer = new PrintWriter(new FileWriter(new File(BookWorm.plugin.getDataFolder(), "books.txt"), false));
			for (String s : books.keySet()) {
				writer.println(s + ":" + books.get(s).getId());
			}
		} catch (IOException e) {
			getServer().getLogger().severe("BookWorm: Error writing book list");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	@Override
	public void onDisable() {
		unloader.stop();
		books = null;
		bookmarks = null;
		newBooks = null;
		unloader = null;
	}

}
