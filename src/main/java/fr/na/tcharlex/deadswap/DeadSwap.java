package fr.na.tcharlex.deadswap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeadSwap {

    private final Main plugin;
    private final List<Player> dead_players = new ArrayList<>();
    private final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    private final int min_players = 2;
    private final Map<Player, Integer> playerAirLevels = new HashMap<>();

    public DeadSwap(Player player, Main main) {
        this.plugin = main;

        if (players.size() < min_players) {
            player.sendMessage("Désolé, tu es tout seul.");
            Bukkit.broadcastMessage("La partie est annulée, il n'y a pas assez de joueurs.");
            return;
        }

        player.sendMessage("Tu es le Chef de ce DeadSwap");

        for (Player p : players) {
            readyPlayer(p);
        }

        prepareForSwap(player);
    }

    private void readyPlayer(Player player) {
        player.sendMessage("Un nouveau DeadSwap va commencer, soyez prêts !");
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(10);
    }

    private void prepareForSwap(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                startSwapTask(player);
            }
        }.runTaskLater(plugin, 60 * 20L); // 1 minute en ticks (20 ticks = 1 seconde)
    }

    private void startSwapTask(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                startCountdown(player, this);
            }
        };
        task.runTaskTimer(plugin, 0L, 2 * 60 * 20L); // 2 minutes en ticks (20 ticks = 1 seconde)
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
            player.sendMessage("Désolé, tu es tout seul.");
            Bukkit.broadcastMessage("La partie est annulée, il n'y a pas assez de joueurs.");
            task.cancel();
            return;
        }

        for (Player p : players) {
            playerAirLevels.put(p, p.getRemainingAir());
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

            if (playerAirLevels.containsKey(players.get(i))) {
                players.get(i).setRemainingAir(playerAirLevels.get(players.get(i)));
            }
        }
    }

    public void checkWin(Player player, Main main) {
        player.kickPlayer("Tu es éliminé.");
        dead_players.add(player);
        players.remove(player);

        if (players.size() == 1) {
            Player winner = players.get(0);
            Bukkit.broadcastMessage("Félicitation à " + winner.getName() + " qui a gagné la partie de DeadSwap !");
            plugin.onGame = false;
            main.expChange = true;
            winner.giveExpLevels(1);
        }
    }
}
