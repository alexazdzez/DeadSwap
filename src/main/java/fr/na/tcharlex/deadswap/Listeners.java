package fr.na.tcharlex.deadswap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {

    private final Main main;

    public Listeners(Main plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(main.onGame){
            player.kickPlayer("Il y a une partie.");
        }
        player.sendMessage("Bienvenue sur ce serveur DeadSwap");

    }
    public void OnDead(PlayerDeathEvent event){
        if(main.onGame){
            Player player = event.getEntity();
            main.deadSwap.checkWin(player);
        }
    }

}
