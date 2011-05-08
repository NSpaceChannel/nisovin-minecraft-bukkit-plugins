package com.nisovin.MagicSpells.Spells;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.nisovin.MagicSpells.CommandSpell;
import com.nisovin.MagicSpells.MagicSpells;

public class RecallSpell extends CommandSpell {
	
	private static final String SPELL_NAME = "recall";
	
	private boolean allowCrossWorld;
	private int maxRange;
	private String strNoMark;
	private String strOtherWorld;
	private String strTooFar;
	
	public static void load(Configuration config) {
		load(config, SPELL_NAME);
	}
	
	public static void load(Configuration config, String spellName) {
		if (config.getBoolean("spells." + spellName + ".enabled", true)) {
			MagicSpells.spells.put(spellName, new RecallSpell(config, spellName));
		}		
	}

	public RecallSpell(Configuration config, String spellName) {
		super(config, spellName);
		
		allowCrossWorld = config.getBoolean("spells." + spellName + ".allow-cross-world", true);
		maxRange = config.getInt("spells." + spellName + ".max-range", 0);
		strNoMark = config.getString("spells." + spellName + ".str-no-mark", "You have no mark to recall to.");
		strOtherWorld = config.getString("spells." + spellName + ".str-other-world", "Your mark is in another world.");
		strTooFar = config.getString("spells." + spellName + ".str-too-far", "You mark is too far away.");
	}

	@Override
	protected boolean castSpell(Player player, SpellCastState state, String[] args) {
		if (state == SpellCastState.NORMAL) {
			if (MarkSpell.marks == null || !MarkSpell.marks.containsKey(player.getName())) {
				// no mark
				sendMessage(player, strNoMark);
				return true;
			} else {
				Location mark = MarkSpell.marks.get(player.getName());
				if (!allowCrossWorld && mark.getWorld() != player.getLocation().getWorld()) {
					// can't cross worlds
					sendMessage(player, strOtherWorld);
					return true;
				} else if (maxRange > 0 && mark.toVector().distanceSquared(player.getLocation().toVector()) > maxRange) {
					// too far
					sendMessage(player, strTooFar);
					return true;
				} else {
					// all good!
					player.teleport(mark);
				}
			}
		}
		return false;
	}

}
