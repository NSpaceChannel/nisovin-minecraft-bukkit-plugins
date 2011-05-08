package com.nisovin.MagicSpells;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public abstract class InstantSpell extends Spell {
	
	protected int range;
	private boolean castWithItem;
	private boolean castByCommand;
	
	public InstantSpell(Configuration config, String spellName) {
		super(config, spellName);
		
		range = config.getInt("spells." + spellName + ".range", -1);
		castWithItem = config.getBoolean("spells." + spellName + ".can-cast-with-item", true);
		castByCommand = config.getBoolean("spells." + spellName + ".can-cast-by-command", true);
	}
	
	public boolean canCastWithItem() {
		return castWithItem;
	}
	
	public boolean canCastByCommand() {
		return castByCommand;
	}

	protected boolean inRange(Location loc1, Location loc2, int range) {
		return sq(loc1.getX()-loc2.getX()) + sq(loc1.getY()-loc2.getY()) + sq(loc1.getZ()-loc2.getZ()) < sq(range);
	}
	
	private double sq(double n) {
		return n*n;
	}
	
	protected LivingEntity getTargetedEntity(Player player, int range, int variance, boolean targetPlayers) {
		List<LivingEntity> entities = player.getWorld().getLivingEntities();
		
		double px = player.getLocation().getX();
		double py = player.getLocation().getY();
		double pz = player.getLocation().getZ();
				
		LivingEntity target = null;
		double distance = 0;
		for (LivingEntity entity : entities) {
			double dx = entity.getLocation().getX() - px;
			double dy = entity.getLocation().getY() - py;
			double dz = entity.getLocation().getZ() - pz;
			if (Math.abs(dx) < range && Math.abs(dy) < range && Math.abs(dz) < range && (targetPlayers || !(entity instanceof Player))) {
				double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
				double xzAngle = Math.atan2(entity.getLocation().getZ() - player.getLocation().getZ(), entity.getLocation().getX() - player.getLocation().getX()) * 57.295F - 90;
				double yAngle = Math.asin(dy / dist) * -57.295F;
				
				if (angleDiff(xzAngle, player.getLocation().getYaw()) < variance && Math.abs(yAngle - player.getLocation().getPitch()) < variance && (target == null || dist < distance)) {
					target = (LivingEntity)entity;
					distance = dist;
				}			
			}	
		}
		
		return target;
	}

	private double angleDiff(double angle1, double angle2) {
		double a = Math.abs(angle1-angle2) % 360;
		if (a <= 180) {
			return a;
		} else {
			return 360-a;
		}
	}
}
