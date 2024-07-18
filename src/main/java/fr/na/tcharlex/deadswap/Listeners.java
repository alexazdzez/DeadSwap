package fr.na.tcharlex.deadswap;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
                Entity damager = damageByEntityEvent.getDamager();

                if (damager instanceof Player) {
                    event.setCancelled(true);
                    return;
                }

                if (damager instanceof Monster) {
                    event.setCancelled(true);
                    return;
                }

                if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Monster) {
                    event.setCancelled(true);
                    return;
                }

                if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
                    event.setCancelled(true);
                }
            }
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

    //jour infine(boucle)
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        player.setInvulnerable(false);
        world.setTime(0);
    }
}