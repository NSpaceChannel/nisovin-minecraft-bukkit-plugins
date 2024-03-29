package com.nisovin.magicspells.spells.targeted;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.ValidTargetList;

public class SilenceSpell extends TargetedSpell implements TargetedEntitySpell {

	private boolean preventCast;
	private boolean preventChat;
	private boolean preventCommands;
	private int duration;
	private List<String> allowedSpellNames;
	private Set<Spell> allowedSpells;
	private String strSilenced;
	
	private Map<String,Unsilencer> silenced;
	
	public SilenceSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		preventCast = getConfigBoolean("prevent-cast", true);
		preventChat = getConfigBoolean("prevent-chat", false);
		preventCommands = getConfigBoolean("prevent-commands", false);
		duration = getConfigInt("duration", 200);
		allowedSpellNames = getConfigStringList("allowed-spells", null);
		strSilenced = getConfigString("str-silenced", "You are silenced!");
		
		if (preventChat) {
			silenced = new ConcurrentHashMap<String, Unsilencer>();
		} else {
			silenced = new HashMap<String, Unsilencer>();
		}
		
		validTargetList = new ValidTargetList(true, false);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		if (allowedSpellNames != null && allowedSpellNames.size() > 0) {
			allowedSpells = new HashSet<Spell>();
			for (String spellName : allowedSpellNames) {
				Spell spell = MagicSpells.getSpellByInternalName(spellName);
				if (spell != null) {
					allowedSpells.add(spell);
				} else {
					MagicSpells.error("Invalid allowed spell specified on silence spell '" + this.internalName + "': '" + spellName + "'");
				}
			}
			allowedSpellNames.clear();
		}
		allowedSpellNames = null;
		
		if (preventCast) {
			registerEvents(new CastListener());
		}
		if (preventChat) {
			registerEvents(new ChatListener());
		}
		if (preventCommands) {
			registerEvents(new CommandListener());
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Player target = getTargetedPlayer(player, power);
			if (target == null) {
				return noTarget(player);
			}
						
			// silence player
			silence(target, power);
			playSpellEffects(player, target);
			
			sendMessages(player, target);
			return PostCastAction.NO_MESSAGES;
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private void silence(Player player, float power) {
		// handle previous silence
		Unsilencer u = silenced.get(player.getName());
		if (u != null) {
			u.cancel();
		}
		// silence now
		silenced.put(player.getName(), new Unsilencer(player, Math.round(duration * power)));
	}

	@Override
	public boolean castAtEntity(Player caster, LivingEntity target, float power) {
		if (target instanceof Player) {
			silence((Player)target, power);
			playSpellEffects(caster, target);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (target instanceof Player) {
			silence((Player)target, power);
			playSpellEffects(EffectPosition.TARGET, target);
			return true;
		} else {
			return false;
		}
	}
	
	public class CastListener implements Listener {
		@EventHandler(ignoreCancelled=true)
		public void onSpellCast(final SpellCastEvent event) {
			if (silenced.containsKey(event.getCaster().getName()) && (allowedSpells == null || !allowedSpells.contains(event.getSpell()))) {
				event.setCancelled(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(MagicSpells.plugin, new Runnable() {
					public void run() {
						sendMessage(event.getCaster(), strSilenced);
					}
				});
			}
		}
	}
	
	public class ChatListener implements Listener {
		@EventHandler(ignoreCancelled=true)
		public void onChat(AsyncPlayerChatEvent event) {
			if (silenced.containsKey(event.getPlayer().getName())) {
				event.setCancelled(true);
				sendMessage(event.getPlayer(), strSilenced);
			}
		}
	}
	
	public class CommandListener implements Listener {
		@EventHandler(ignoreCancelled=true)
		public void onCommand(PlayerCommandPreprocessEvent event) {
			if (silenced.containsKey(event.getPlayer().getName())) {
				event.setCancelled(true);
				sendMessage(event.getPlayer(), strSilenced);
			}
		}
	}
	
	public class Unsilencer implements Runnable {

		private String playerName;
		private boolean canceled = false;
		private int taskId = -1;
		
		public Unsilencer(Player player, int delay) {
			this.playerName = player.getName();
			taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MagicSpells.plugin, this, delay);
		}
		
		@Override
		public void run() {
			if (!canceled) {
				silenced.remove(playerName);
			}
		}
		
		public void cancel() {
			canceled = true;
			if (taskId > 0) {
				Bukkit.getScheduler().cancelTask(taskId);
			}
		}
		
	}

}
