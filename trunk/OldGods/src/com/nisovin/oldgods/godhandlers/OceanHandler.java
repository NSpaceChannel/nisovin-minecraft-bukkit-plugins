package com.nisovin.oldgods.godhandlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class OceanHandler {

	public static void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.DROWNING) {
			event.setCancelled(true);
		}
	}
	
	public static void pray(Player player, Block block, int amount) {
		
	}
	
}
