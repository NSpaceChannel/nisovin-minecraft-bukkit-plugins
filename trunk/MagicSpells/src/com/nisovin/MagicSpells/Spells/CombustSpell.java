package com.nisovin.MagicSpells.Spells;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.nisovin.MagicSpells.MagicSpells;
import com.nisovin.MagicSpells.InstantSpell;

public class CombustSpell extends InstantSpell {

	private static final String SPELL_NAME = "combust";
	
	private boolean targetPlayers;
	private int fireTicks;
	private int precision;
	private String strNoTarget;
	
	public static void load(Configuration config) {
		load(config, SPELL_NAME);
	}
	
	public static void load(Configuration config, String spellName) {
		if (config.getBoolean("spells." + spellName + ".enabled", true)) {
			MagicSpells.spells.put(spellName, new CombustSpell(config, spellName));
		}
	}
	
	public CombustSpell(Configuration config, String spellName) {
		super(config, spellName);
		
		targetPlayers = config.getBoolean("spells." + spellName + ".target-players", false);
		fireTicks = config.getInt("spells." + spellName + ".fire-ticks", 100);
		precision = config.getInt("spells." + spellName + ".precision", 20);
		strNoTarget = config.getString("spells." + spellName + ".str-no-target", "");
	}
	
	@Override
	protected boolean castSpell(Player player, SpellCastState state, String[] args) {
		if (state == SpellCastState.NORMAL) {
			LivingEntity target = getTargetedEntity(player, range>0?range:100, precision, targetPlayers);
			if (target == null) {
				sendMessage(player, strNoTarget);
				return true;
			} else {
				target.setFireTicks(fireTicks);
				// TODO: manually send messages with replacements
			}
		}
		return false;
	}
}