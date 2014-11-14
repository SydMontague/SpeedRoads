package de.craftlancer.speedroads;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.speedapi.SpeedAPI;

public class SpeedRoads extends JavaPlugin
{
    private FileConfiguration config;
    protected Set<Road> roads;
    
    @Override
    public void onEnable()
    {
        config = getConfig();
        if (!new File(this.getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        roads = loadRoads();
        
        SpeedAPI.addModifier("road", new RoadSpeedModifier(1, this));
    }
    
    @Override
    public void onDisable()
    {
        roads = null;
        config = null;
        getServer().getScheduler().cancelTasks(this);
    }
    
    private Set<Road> loadRoads()
    {
        Set<Road> set = new HashSet<Road>();
        
        for (String key : config.getConfigurationSection("roads").getKeys(false))
            set.add(new Road(key, config));
        
        getLogger().info(set.size() + " Roads loaded");
        
        return set;
    }
    
    public Set<Road> getRoads()
    {
        return roads;
    }
    
    public void reload()
    {
        reloadConfig();
        config = getConfig();
        
        roads = null;
        roads = loadRoads();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("roads") && sender.hasPermission("speedroads.admin"))
        {
            if (args.length == 0 || args[0].equalsIgnoreCase("help"))
                commandHelp(sender);
            else if (args.length > 0)
                if (args[0].equalsIgnoreCase("reload"))
                    reload();
        }
        return true;
    }
    
    private static void commandHelp(CommandSender sender)
    {
        sender.sendMessage("/roads - this page");
        sender.sendMessage("/roads reload - reloads the config");
    }
}
