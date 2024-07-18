package fr.na.tcharlex.deadswap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
            player.sendMessage("Bienvenue sur ce serveur DeadSwap");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (main.onGame) {
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
}
