package fr.na.mpxp.deadswap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Listeners implements Listener {

    private final Main main;

    public Listeners(Main plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (main.onGame) {
            player.kickPlayer("Il y a une partie en cours.");
        } else {
            event.setJoinMessage("[" + ChatColor.GREEN + "+" + ChatColor.RESET + "] " + player.getName());
            player.sendMessage("Bienvenue sur ce serveur DeadSwap");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("[" + ChatColor.RED + "-" + ChatColor.RESET + "] " + player.getName());

        if (main.players.contains(player)) {
            main.players.remove(player);

            if (main.onGame && main.players.size() < main.deadSwap.min_players) {
                main.deadSwap.forceFinish(main);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true); // Empêcher les dégâts
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (main.onGame) {
            event.setKeepInventory(true);
            Player player = event.getEntity();
            main.deadSwap.checkWin(player, main);
        }
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        if (main.onGame || !main.expChange) {
            event.setAmount(0);
        } else {
            main.expChange = false;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.setInvulnerable(false);
        player.getWorld().setTime(6000);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event){
        if(main.onGame){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerGameModeChangeEvent event){
        if (main.onGame){
            event.setCancelled(true);
        }
    }
}
