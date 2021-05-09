package de.craftlancer.speedroads;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedRoadsTask extends BukkitRunnable {
    private static final int UPDATE_ON_ROAD_DIVIDER = 20;
    private static final int UPDATE_ENTITIY_CACHE = 100;
    private static final UUID MODIFIER_UUID = UUID.fromString("0d2d4303-c228-4075-9f94-00fa3036f40c");
    private static final String MODIFIER_NAME = "SpeedRoads";
    private static final AttributeModifier EMPTY_MODIFIER = new AttributeModifier(MODIFIER_UUID, MODIFIER_NAME, 0, Operation.ADD_SCALAR);
    private final SpeedRoads plugin;
    
    private Map<UUID, Double> currentSpeedMap = new HashMap<>();
    private Map<UUID, Double> targetSpeedMap = new HashMap<>();
    
    private Map<World, Collection<Entity>> affectedEntitiesMap = new HashMap<>();
    private long tickCounter = 0;
    
    public SpeedRoadsTask(SpeedRoads plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this::applyAttribute);
        affectedEntitiesMap.forEach((w, a) -> a.forEach(this::applyAttribute));
        
        if(tickCounter++ % UPDATE_ENTITIY_CACHE == 0 && !plugin.getAffectedEntities().isEmpty())
            Bukkit.getWorlds().forEach(a -> affectedEntitiesMap.put(a, a.getEntitiesByClasses(plugin.getAffectedEntities().toArray(new Class[0]))));
    }
    
    private void applyAttribute(Entity a) {
        if (a.isValid() && a instanceof LivingEntity)
            applyAttribute((LivingEntity) a);
    }
    
    private void applyAttribute(LivingEntity a) {
        double currentSpeedMod = currentSpeedMap.getOrDefault(a.getUniqueId(), 0D);
        double targetSpeedMod = getTargetSpeed(a);
        
        // no need to update attribute if we're at the target already
        if(currentSpeedMod == targetSpeedMod)
            return;
        
        AttributeInstance attrib = a.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        attrib.removeModifier(EMPTY_MODIFIER);
        
        if (targetSpeedMod >= currentSpeedMod)
            currentSpeedMod = Math.min(currentSpeedMod + plugin.getStepSize(), targetSpeedMod);
        else
            currentSpeedMod = Math.max(currentSpeedMod - plugin.getStepSize(), 0);
        
        attrib.addModifier(new AttributeModifier(MODIFIER_UUID, MODIFIER_NAME, currentSpeedMod, Operation.ADD_SCALAR));
        
        currentSpeedMap.put(a.getUniqueId(), currentSpeedMod);
    }
    
    private double getTargetSpeed(LivingEntity a) {
        // distribute updated entities roughly evenly across the ticks
        if(tickCounter % UPDATE_ON_ROAD_DIVIDER == a.getEntityId() % UPDATE_ON_ROAD_DIVIDER) {
            double targetSpeedMod = Double.NEGATIVE_INFINITY;
            for (Road r : plugin.getRoads())
                if (r.getSpeedMod() > targetSpeedMod && r.isRoadBlock(a.getLocation().getBlock()))
                    targetSpeedMod = r.getSpeedMod();
            
            if (targetSpeedMod == Double.NEGATIVE_INFINITY)
                targetSpeedMod = 0.0;
            
            targetSpeedMap.put(a.getUniqueId(), targetSpeedMod);
        }
        
        return targetSpeedMap.getOrDefault(a.getUniqueId(), 0D);
    }
}
