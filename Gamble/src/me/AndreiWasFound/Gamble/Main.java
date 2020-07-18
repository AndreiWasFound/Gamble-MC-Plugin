package me.AndreiWasFound.Gamble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.util.internal.ThreadLocalRandom;


public class Main extends JavaPlugin implements Listener {
	
	List<Inventory> invs = new ArrayList<Inventory>();
	public static ItemStack[] contents;
	private int ItemIndex = 0;

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("gamble")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Sorry, console can't gamble.");
				return true;
			}
			Player player = (Player) sender;
			ItemStack fee = new ItemStack(Material.DIAMOND);
			fee.setAmount(5);
			if (player.getInventory().getItemInMainHand().isSimilar(fee)) {
				player.getInventory().removeItem(fee);
				// spin the GUI
				spin(player);
				return true;
			}
			player.sendMessage(ChatColor.DARK_RED + "You need to hold 5 diamonds in your hand to gamble!");
			return true;
			
		}
		return false;
	}
	
	public void shuffle(Inventory inv) {
		if (contents == null) {
			ItemStack[] items = new ItemStack[10];
			
			items[0] = new ItemStack(Material.COARSE_DIRT, 64);
			items[1] = new ItemStack(Material.DIAMOND, 3);
			items[2] = new ItemStack(Material.IRON_INGOT, 16);
			items[3] = new ItemStack(Material.NETHER_STAR, 1);
			items[4] = new ItemStack(Material.ELYTRA, 1);
			items[5] = new ItemStack(Material.DRAGON_HEAD, 4);
			items[6] = new ItemStack(Material.EMERALD, 8);
			items[7] = new ItemStack(Material.DIAMOND_BLOCK, 1);
			items[8] = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
			items[9] = new ItemStack(Material.DRAGON_BREATH, 8);
			
			contents = items;
			
			
		}
		
		
		int startingIndex = ThreadLocalRandom.current().nextInt(contents.length); 
		
		for (int index = 0; index < startingIndex; index++) {
			for (int itemstacks = 9; itemstacks < 18; itemstacks++) {
				inv.setItem(itemstacks, contents[(itemstacks + ItemIndex) % contents.length]);
			}
			ItemIndex++;
			
			
		}
		
		// Customize
		ItemStack hopperpointer = new ItemStack(Material.HOPPER);
		ItemMeta hopperpointermeta = hopperpointer.getItemMeta();
		hopperpointermeta.setDisplayName(ChatColor.DARK_GRAY + "|");
		hopperpointer.setItemMeta(hopperpointermeta);
		inv.setItem(4, hopperpointer);
	}
	
	
	public void spin(final Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "" + ChatColor.BOLD + "Good luck!");
		shuffle(inv);
		invs.add(inv);
		player.openInventory(inv);
		
		Random r = new Random();
		double seconds = 7.0 + (12.0 - 7.0) * r.nextDouble();
		
		new BukkitRunnable() {
			double delay = 0;
			int ticks = 0;
			boolean done = false;
			
			public void run() {
				if (done)
					return;
				ticks++;
				delay += 1 / (20 *seconds);
				if (ticks > delay * 10) {
					ticks = 0;
					
					for (int itemstacks = 9; itemstacks	< 18; itemstacks++)
						inv.setItem(itemstacks, contents[(itemstacks + ItemIndex) % contents.length]);
					
					
					ItemIndex++;
					
					if (delay >= .5) {
						done = true;
						new BukkitRunnable() {
							public void run() {
								ItemStack item = inv.getItem(13);
								player.getInventory().addItem(item);
								player.updateInventory();
								player.closeInventory();
								cancel();
								
							}
							
						}.runTaskLater(Main.getPlugin(Main.class), 50);
						cancel();
						
						
					}
				}
				
				
			}
			
			
			
		}.runTaskTimer(this, 0, 1);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!invs.contains(event.getInventory()))
			return;
		
		event.setCancelled(true);
		return;
	}
	
	}