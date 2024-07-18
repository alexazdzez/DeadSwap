package fr.na.tcharlex.deadswap;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class Main extends JavaPlugin {
    public boolean onGame = false;
    public DeadSwap deadSwap;
    public boolean expChange = false;

    @Override
    public void onEnable() {
        System.out.println("Plugin DeadSwap activé !");
        Objects.requireNonNull(getCommand("DSs")).setExecutor(new Commands(this));
        Objects.requireNonNull(getCommand("DSog")).setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }
}
