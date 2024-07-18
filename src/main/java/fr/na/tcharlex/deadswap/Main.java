package fr.na.tcharlex.deadswap;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("parfait !!");
        getCommand("DSstart").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }
}
