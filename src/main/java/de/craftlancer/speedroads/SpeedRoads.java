package de.craftlancer.speedroads;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedRoads extends JavaPlugin {
    private Set<Road> roads;
    private double stepSize = 0.01D;
    private Set<Class<? extends LivingEntity>> affectedEntities = new HashSet<>();
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        new SpeedRoadsTask(this).runTaskTimer(this, 1L, 1L);
    }
    
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
    
    private void loadConfig() {
        affectedEntities.clear();
        stepSize = getConfig().getDouble("stepSize", 0.01D);
        
        for (String s : getConfig().getStringList("affectedEntities")) {
            EntityType type = EntityType.valueOf(s);
            
            if (type == EntityType.PLAYER)
                continue;
            
            Class<? extends Entity> clazz = type.getEntityClass();
            
            if (LivingEntity.class.isAssignableFrom(clazz))
                affectedEntities.add(clazz.asSubclass(LivingEntity.class));
        }
        
        ConfigurationSection roadSection = getConfig().getConfigurationSection("roads");
        roads = roadSection.getKeys(false).stream().map(key -> new Road(roadSection.getConfigurationSection(key))).collect(Collectors.toSet());
        getLogger().info(() -> roads.size() + " Road(s) loaded");
    }
    
    public Set<Road> getRoads() {
        return roads;
    }
    
    public double getStepSize() {
        return stepSize;
    }
    
    public Set<Class<? extends LivingEntity>> getAffectedEntities() {
        return affectedEntities;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!sender.hasPermission("speedroads.admin"))
            return true;
        
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("/roads - this page");
            sender.sendMessage("/roads reload - reloads the config");
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadConfig();
            sender.sendMessage(getRoads().size() + " road(s) loaded.");
        }
        else
            return false;
        
        return true;
    }
}
