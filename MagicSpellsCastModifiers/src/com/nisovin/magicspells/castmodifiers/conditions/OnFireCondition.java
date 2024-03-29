package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OnFireCondition extends Condition {

	@Override
	public boolean setVar(String var) {
		return true;
	}

	@Override
	public boolean check(Player player) {
		return player.getFireTicks() > 0;
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return target.getFireTicks() > 0;
	}

}
