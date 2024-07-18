package fr.na.tcharlex.deadswap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if(cmd.getName().equalsIgnoreCase("DSstart")){
            if(sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage("[serveur](privé): " + player.getName() + " le jeu va démarrer");
            Bukkit.broadcastMessage(player.getName() + " va démarrer un DeadSwap!");
            }

            else {
                sender.sendMessage("il faut être un joueur pour démarrer une partie");
            }
            return true;
        }

        return false;
    }
}
