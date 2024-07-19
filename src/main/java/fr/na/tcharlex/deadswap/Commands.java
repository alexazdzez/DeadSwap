package fr.na.tcharlex.deadswap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final Main main;

    public Commands(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("DSs")) {
            if (sender instanceof Player player) {
                if (main.onGame) {
                    player.sendMessage("Une partie est déjà en cours.");
                } else {
                    main.players.clear(); // Clear the player list to start fresh
                    main.players.addAll(Bukkit.getOnlinePlayers()); // Initialize players with online players
                    main.onGame = true;
                    main.deadSwap = new DeadSwap(player, main);
                }
            } else {
                sender.sendMessage("Il faut être un joueur pour démarrer une partie.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("DSog")) {
            main.onGame = !main.onGame;
            sender.sendMessage("État du jeu : " + (main.onGame ? "En cours" : "Arrêté"));
            return true;
        } else if (cmd.getName().equalsIgnoreCase("DSready")) {
            if (sender instanceof Player player) {
                if (main.onGame && !main.ready_players.contains(player)) {
                    main.ready_players.add(player);
                    main.deadSwap.checkReady(main);
                }
            }
            return true;
        }
        return false;
    }
}
