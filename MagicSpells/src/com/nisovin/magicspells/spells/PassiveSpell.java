package com.nisovin.magicspells.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellCastedEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.util.CastItem;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellReagents;

public class PassiveSpell extends Spell {

	private PassiveSpell thisSpell = this;
	private Random random = new Random();
	
	private String trigger;
	private float chance;
	
	private List<String> spellNames;
	private List<Spell> spells;
	
	public PassiveSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		trigger = getConfigString("trigger", "");
		chance = getConfigFloat("chance", 100F) / 100F;
		
		spellNames = getConfigStringList("spells", null);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		// create spell list
		spells = new ArrayList<Spell>();
		if (spellNames != null) {
			for (String spellName : spellNames) {
				Spell spell = MagicSpells.getSpellByInternalName(spellName);
				if (spell != null) {
					spells.add(spell);
				}
			}
		}
		if (spells.size() == 0) {
			MagicSpells.error("Passive spell '" + name + "' has no spells defined!");
			return;
		}
		
		// get trigger
		String type = trigger;
		String var = null;
		if (trigger.contains(" ")) {
			String[] data = trigger.split(" ", 2);
			type = data[0];
			var = data[1];
		}
		type = type.toLowerCase();
		
		// process trigger
		if (type.equalsIgnoreCase("takedamage")) {
			registerEvents(new TakeDamageListener(var));
		} else if (type.equalsIgnoreCase("givedamage")) {
			registerEvents(new GiveDamageListener(var));
		} else if (type.equalsIgnoreCase("blockbreak")) {
			registerEvents(new BlockBreakListener(var));
		} else if (type.equalsIgnoreCase("blockplace")) {
			registerEvents(new BlockPlaceListener(var));
		} else if (type.equalsIgnoreCase("rightclick")) {
			registerEvents(new RightClickListener(var));
		} else if (type.equalsIgnoreCase("spelltargeted")) {
			registerEvents(new SpellTargetedListener(var));
		} else if (type.equalsIgnoreCase("spellcast")) {
			registerEvents(new SpellCastListener(var));
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		return PostCastAction.ALREADY_HANDLED;
	}
	
	private void activate(Player caster) {
		activate(caster, null, null);
	}
	
	private void activate(Player caster, LivingEntity target) {
		activate(caster, target, null);
	}
	
	private void activate(Player caster, Location location) {
		activate(caster, null, location);
	}
	
	private void activate(Player caster, LivingEntity target, Location location) {
		MagicSpells.debug(3, "Activating passive spell '" + name + "' for player " + caster.getName());
		if (!onCooldown(caster) && (chance >= .999 || random.nextFloat() <= chance) && hasReagents(caster)) {
			SpellCastEvent event = new SpellCastEvent(this, caster, SpellCastState.NORMAL, 1.0F, null, cooldown, new SpellReagents());
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				float power = event.getPower();
				for (Spell spell : spells) {
					MagicSpells.debug(3, "    Casting spell effect '" + spell.getName() + "'");
					if (spell instanceof TargetedEntitySpell && target != null) {
						((TargetedEntitySpell)spell).castAtEntity(caster, target, power);
					} else if (spell instanceof TargetedLocationSpell && (location != null || target != null)) {
						if (location != null) {
							((TargetedLocationSpell)spell).castAtLocation(caster, location, power);
						} else if (target != null) {
							((TargetedLocationSpell)spell).castAtLocation(caster, target.getLocation(), power);
						}
					} else {
						spell.castSpell(caster, SpellCastState.NORMAL, power, null);
					}
				}
				setCooldown(caster, event.getCooldown());
				removeReagents(caster);
				sendMessage(caster, strCastSelf);
			}
		}
	}
	
	public class TakeDamageListener implements Listener {
		
		int itemId = -1;
		DamageCause damageCause = null;
		
		public TakeDamageListener(String var) {
			if (var != null) {
				if (var.matches("[0-9]+")) {
					itemId = Integer.parseInt(var);
				} else {
					damageCause = DamageCause.valueOf(var.toUpperCase());
				}
			}
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
		public void onDamage(EntityDamageEvent event) {
			if (event.getEntityType() == EntityType.PLAYER) {
				Player player = (Player)event.getEntity();
				Spellbook spellbook = MagicSpells.getSpellbook(player);
				if (spellbook.hasSpell(thisSpell)) {
					DamageCause cause = event.getCause();
					if (event instanceof EntityDamageByEntityEvent) {
						Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();
						if (attacker instanceof Projectile) {
							attacker = ((Projectile)attacker).getShooter();
							if (check(attacker, cause)) activate(player, (LivingEntity)attacker);
						} else if (attacker instanceof LivingEntity) {
							if (check(attacker, cause)) activate(player, (LivingEntity)attacker);
						} else {
							if (check(attacker, cause)) activate(player);
						}
					} else {
						if (check(null, cause)) activate(player);
					}
				}
			}
		}
		
		private boolean check(Entity attacker, DamageCause cause) {
			if (itemId >= 0 && attacker != null) {
				if (attacker instanceof Player) {
					ItemStack inHand = ((Player)attacker).getItemInHand();
					if (inHand != null && inHand.getTypeId() == itemId) {
						return true;
					} else if (inHand == null && itemId == 0) {
						return true;
					}
				} else if (attacker instanceof Skeleton && itemId == Material.BOW.getId()) {
					return true;
				} else if (attacker instanceof Zombie && itemId == 0) {
					return true;
				}
				return false;
			} else if (damageCause != null) {
				return damageCause == cause;
			} else {
				return true;
			}
		}
	}
	
	public class GiveDamageListener implements Listener {
		
		int itemId = -1;
		
		public GiveDamageListener(String var) {
			if (var != null && var.matches("[0-9]+")) {
				itemId = Integer.parseInt(var);
			}
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
		public void onDamage(EntityDamageByEntityEvent event) {
			Player player = null;
			if (event.getDamager().getType() == EntityType.PLAYER) {
				player = (Player)event.getDamager();
			} else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter().getType() == EntityType.PLAYER) {
				player = (Player)((Projectile)event.getDamager()).getShooter();
			}
			if (player != null) {
				if (itemId >= 0 && player.getItemInHand().getTypeId() != itemId) {
					return;
				}
				Spellbook spellbook = MagicSpells.getSpellbook(player);
				if (spellbook.hasSpell(thisSpell)) {
					if (event.getEntity() instanceof LivingEntity && ((LivingEntity)event.getEntity()).getNoDamageTicks() <= 0) {
						activate(player, (LivingEntity)event.getEntity());
					}
				}
			}
		}
	}
	
	public class BlockBreakListener implements Listener {
		
		int typeId = -1;
		
		public BlockBreakListener(String var) {
			if (var != null && var.matches("[0-9]+")) {
				typeId = Integer.parseInt(var);
			}
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
		public void onBlockBreak(BlockBreakEvent event) {
			if (typeId < 0 || event.getBlock().getTypeId() == typeId) {
				Player player = event.getPlayer();
				Spellbook spellbook = MagicSpells.getSpellbook(player);
				if (spellbook.hasSpell(thisSpell)) {
					activate(player, event.getBlock().getLocation().add(.5, 0, .5));
				}
			}
		}
	}
	
	public class BlockPlaceListener implements Listener {
		
		int typeId = -1;
		
		public BlockPlaceListener(String var) {
			if (var != null && var.matches("[0-9]+")) {
				typeId = Integer.parseInt(var);
			}
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
		public void onBlockPlace(BlockPlaceEvent event) {
			if (typeId < 0 || event.getBlock().getTypeId() == typeId) {
				Player player = event.getPlayer();
				Spellbook spellbook = MagicSpells.getSpellbook(player);
				if (spellbook.hasSpell(thisSpell)) {
					activate(player, event.getBlock().getLocation().add(.5, 0, .5));
				}
			}
		}
	}
	
	public class RightClickListener implements Listener {
		int typeId = -1;
		int data = 0;
		boolean checkData = false;
		
		public RightClickListener(String var) {
			if (var != null && var.matches("[0-9]+(:[0-9]+)?")) {
				if (var.contains(":")) {
					String[] s = var.split(":");
					typeId = Integer.parseInt(s[0]);
					data = Integer.parseInt(s[1]);
					checkData = true;
				} else {
					typeId = Integer.parseInt(var);
				}
			}
		}
		
		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerInteract(PlayerInteractEvent event) {
			if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.useItemInHand() != Result.DENY) {
				if (event.getItem().getTypeId() == typeId && (!checkData || event.getItem().getDurability() == data)) {
					Player player = event.getPlayer();
					Spellbook spellbook = MagicSpells.getSpellbook(player);
					if (spellbook.hasSpell(thisSpell)) {
						activate(player);
					}
				}
			}
		}
	}
	
	public class SpellTargetedListener implements Listener {
		
		String spellName;
		
		public SpellTargetedListener(String var) {
			spellName = var;
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
		public void onSpellTarget(SpellTargetEvent event) {
			if (event.getTarget() instanceof Player) {
				if (spellName == null || event.getSpell().getName().equals(spellName)) {
					Player player = (Player)event.getTarget();
					Spellbook spellbook = MagicSpells.getSpellbook(player);
					if (spellbook.hasSpell(thisSpell)) {
						activate(player, event.getCaster());
					}
				}
			}
		}
	}
	
	public class SpellCastListener implements Listener {
		
		String spellName;
		
		public SpellCastListener(String var) {
			spellName = var;
		}
		
		@EventHandler(priority=EventPriority.MONITOR)
		public void onSpellCasted(SpellCastedEvent event) {
			if (event.getSpell().getInternalName().equals(spellName) && event.getPostCastAction() != PostCastAction.ALREADY_HANDLED) {
				Spellbook spellbook = MagicSpells.getSpellbook(event.getCaster());
				if (spellbook.hasSpell(thisSpell)) {
					activate(event.getCaster());
				}
			}
		}
	}

	@Override
	public boolean canBind(CastItem item) {
		return false;
	}

	@Override
	public boolean canCastWithItem() {
		return false;
	}

	@Override
	public boolean canCastByCommand() {
		return false;
	}

}
