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
    public final int min_players = 2;
    private final Map<Player, Integer> playerAirLevels = new HashMap<>();
    private BukkitRunnable preparationTask;
    private BukkitRunnable task;

    public DeadSwap(Player player, Main main) {
        this.plugin = main;

        if (!main.onGame) {
            if (main.players.size() < min_players) {
                player.sendMessage("Désolé, il n'y a pas assez de joueurs.");
                Bukkit.broadcastMessage("La partie est annulée, il n'y a pas assez de joueurs.");
                return;
            }

            player.sendMessage("Tu es le Chef de ce DeadSwap");

            for (Player p : main.players) {
                readyPlayer(p);
            }

            preparationPhase();
        } else {
            player.sendMessage("Il y a déjà une partie en cours.");
        }
    }

    private void readyPlayer(Player player) {
        player.sendMessage("Un nouveau DeadSwap va commencer, soyez prêts !");
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(10);
    }

    private void preparationPhase() {
        preparationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (task != null) {
                    task.cancel();
                }
                if (plugin.onGame) {
                    startSwapTask();
                } else {
                    forceFinish(plugin);
                }
            }
        };
        preparationTask.runTaskLater(plugin, 60 * 20L); // 1 minute en ticks (20 ticks = 1 seconde)
    }

    private void startSwapTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.onGame) {
                    startCountdown();
                } else {
                    forceFinish(plugin);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 2 * 60 * 20L); // Toutes les 2 minutes en ticks (20 ticks = 1 seconde)
    }

    private void startCountdown() {
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("Téléportation dans " + countdown + " secondes...");
                    countdown--;
                } else {
                    this.cancel();
                    if (plugin.onGame) {
                        swapPlayers();
                    } else {
                        forceFinish(plugin);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 seconde
    }

    private void swapPlayers() {
        if (plugin.onGame) {
            for (Player p : plugin.players) {
                playerAirLevels.put(p, p.getRemainingAir());
            }

            List<Location> locations = new ArrayList<>();
            for (Player p : plugin.players) {
                locations.add(p.getLocation());
            }

            boolean positionsSwapped = false;
            while (!positionsSwapped) {
                Collections.shuffle(locations);
                positionsSwapped = true;
                for (int i = 0; i < plugin.players.size(); i++) {
                    if (plugin.players.get(i).getLocation().equals(locations.get(i))) {
                        positionsSwapped = false;
                        break;
                    }
                }
            }

            for (int i = 0; i < plugin.players.size(); i++) {
                plugin.players.get(i).teleport(locations.get(i));
                plugin.players.get(i).sendMessage("Vos positions ont été échangées !");

                if (playerAirLevels.containsKey(plugin.players.get(i))) {
                    plugin.players.get(i).setRemainingAir(playerAirLevels.get(plugin.players.get(i)));
                }
            }

            plugin.ready_players.clear(); // Réinitialiser les joueurs prêts
        } else {
            forceFinish(plugin);
        }
    }

    public void checkWin(Player player, Main main) {
        player.kickPlayer("Tu es éliminé.");
        dead_players.add(player);
        plugin.players.remove(player);

        if (plugin.players.size() == 1) {
            win();
        }
    }

    private void win() {
        Player winner = plugin.players.get(0);
        Bukkit.broadcastMessage("Félicitation à " + winner.getName() + " qui a gagné la partie de DeadSwap !");
        plugin.onGame = false;
        plugin.expChange = true;
        winner.giveExpLevels(1);
        if (task != null) {
            task.cancel();
        }
        if (preparationTask != null) {
            preparationTask.cancel();
        }
        plugin.ready_players.clear();
        dead_players.clear();
    }

    public void checkReady(Main main) {
        Bukkit.broadcastMessage(plugin.ready_players.size() + " joueur(s) est/sont prêt(s) sur : " + plugin.players.size());
        if (plugin.players.size() == plugin.ready_players.size()) {
            if (task != null) {
                task.cancel();
            }
            startCountdown(); // Démarrer le compte à rebours de 5 secondes
        }
    }

    public void forceFinish(Main main) {
        plugin.onGame = false;
        Bukkit.broadcastMessage("Une erreur nous a obligé à terminer la partie.\n Redémarrez le serveur ou faites /dss pour recommencer la partie.");
        if (task != null) {
            task.cancel();
        }
        if (preparationTask != null) {
            preparationTask.cancel();
        }
        plugin.ready_players.clear();
        dead_players.clear();
    }
}
