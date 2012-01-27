package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.InstantSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class PurgeSpell extends InstantSpell {
	
	public PurgeSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Override
	protected PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		// TODO: make this spell more customizable, also don't charge if there was nothing nearby
		if (state == SpellCastState.NORMAL) {
			int range = Math.round(this.range*power);
			List<Entity> entities = player.getNearbyEntities(range, range, range);
			for (Entity entity : entities) {
				if (entity instanceof LivingEntity && !(entity instanceof Player)) {
					((LivingEntity)entity).setHealth(0);
				}
			}
		}
		return PostCastAction.HANDLE_NORMALLY;
	}	
	
}