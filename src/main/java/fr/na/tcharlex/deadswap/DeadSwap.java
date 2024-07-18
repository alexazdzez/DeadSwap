package fr.na.tcharlex.deadswap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeadSwap {
    private BukkitRunnable task;
    private Main plugin;
    public List<Player> dead_players = new ArrayList<>();
    private List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    private int min_players = 1;
    public DeadSwap(Player player, Main main) {
        plugin = main;
        if(main.onGame){
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (players.size() < min_players) {
            player.sendMessage("Désolé tu es tout seul.");
            Bukkit.broadcastMessage("La partie est annulée, il n'y a qu'un joueur.");
            return;
        }

        player.sendMessage("Tu es le Chef de ce DeadSwap");

        for (Player p : players) {
            p.sendMessage("Un nouveau DeadSwap va commencer, soyez prêts !");
        }

        startSwapTask(player);
        }

    }

    private void startSwapTask(Player player) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                startCountdown(player, this);
            }
        };
        task.runTaskTimer(plugin, 0L, 1 * 30 * 20L); // 2 minutes en ticks (20 ticks = 1 seconde)
    }

    private void startCountdown(Player player, BukkitRunnable task) {
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("Téléportation dans " + countdown + "...");
                    countdown--;
                } else {
                    this.cancel();
                    swapPlayers(player, task);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 seconde
    }


    private void swapPlayers(Player player, BukkitRunnable task) {
        if (players.size() < min_players) {
            player.sendMessage("Désolé tu es tout seul.");
            Bukkit.broadcastMessage("La partie est annulée, il n'y a qu'un joueur.");
            task.cancel();
            return;
        }
        List<Location> locations = new ArrayList<>();
        for (Player p : players) {
            locations.add(p.getLocation());
        }

        boolean positionsSwapped = false;
        while (!positionsSwapped) {
            Collections.shuffle(locations);
            positionsSwapped = true;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getLocation().equals(locations.get(i))) {
                    positionsSwapped = false;
                    break;
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {
            players.get(i).teleport(locations.get(i));
            players.get(i).sendMessage("Vos positions ont été échangées !");
        }
    }
    public void checkWin(Player player){
        player.kickPlayer("Tu es éliminé.");

        dead_players.add(player);
        players.remove(player);
    }
}