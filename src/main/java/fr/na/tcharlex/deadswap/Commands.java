package fr.na.tcharlex.deadswap;

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
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if(cmd.getName().equalsIgnoreCase("DSstart")){
            if(sender instanceof Player){
            Player player = (Player) sender;
            main.deadSwap = new DeadSwap(player, main);
            main.onGame = true;
            }

            else {
                sender.sendMessage("Il faut être un joueur pour démarrer une partie.");
            }
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("DSongame")) {
            if (main.onGame){
                main.onGame = false;
            }
            else{
                main.onGame = true;
            }
            return true;
        }

        return false;
    }
}
