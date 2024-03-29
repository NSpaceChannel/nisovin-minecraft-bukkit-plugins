package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;

public class DistanceLessThan extends Condition {

	private double distanceSq;
	
	@Override
	public boolean setVar(String var) {
		try {
			distanceSq = Double.parseDouble(var);
			distanceSq = distanceSq * distanceSq;
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(Player player) {
		return false;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return player.getLocation().distanceSquared(target.getLocation()) < distanceSq;
	}

}
