package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class BetSign implements MinigameSign{
	
	private static Minigames plugin = Minigames.plugin;
	private FileConfiguration lang = plugin.getLang();

	@Override
	public String getName() {
		return "Bet";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.bet";
	}

	@Override
	public String getCreatePermissionMessage() {
		return lang.getString("sign.bet.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.bet";
	}

	@Override
	public String getUsePermissionMessage() {
		return lang.getString("sign.bet.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(1, ChatColor.GREEN + "Bet");
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName());
			if(event.getLine(3).matches("[0-9]+")){
				event.setLine(3, "$" + event.getLine(3));
			}
			return true;
		}
		event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
		if(mgm != null && (player.getPlayer().getItemInHand().getType() != Material.AIR || (sign.getLine(3).startsWith("$") && player.getPlayer().getItemInHand().getType() == Material.AIR))){
			if(mgm.isEnabled() && (!mgm.getUsePermissions() || player.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
				if(mgm.isSpectator(player)){
					return false;
				}
				
				if(!sign.getLine(3).startsWith("$")){
					plugin.pdata.joinWithBet(player, plugin.mdata.getMinigame(sign.getLine(2)), 0d);
				}
				else{
					if(plugin.hasEconomy()){
						Double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
						plugin.pdata.joinWithBet(player, plugin.mdata.getMinigame(sign.getLine(2)), bet);
						return true;
					}
					else{
						player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + lang.getString("minigame.error.noVault"));
					}
				}
			}
			else if(!mgm.isEnabled()){
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + lang.getString("minigame.error.notEnabled"));
			}
			else if(mgm.getUsePermissions()){
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName().toLowerCase()));
			}
		}
		else if(mgm != null && player.getPlayer().getItemInHand().getType() == Material.AIR && !sign.getLine(3).startsWith("$")){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + lang.getString("sign.bet.noBet"));
		}
		else if(mgm != null && player.getPlayer().getItemInHand().getType() != Material.AIR && sign.getLine(3).startsWith("$")){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + lang.getString("sign.emptyHand"));
		}
		else{
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + lang.getString("minigame.error.noMinigame"));
		}
		return false;
	}

}
