package fr.na.tcharlex.deadswap;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public boolean onGame = false;
    public DeadSwap deadSwap;
    @Override
    public void onEnable() {
        System.out.println("parfait !!");
        getCommand("DSstart").setExecutor(new Commands(this));
        getCommand("DSongame").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }
}
