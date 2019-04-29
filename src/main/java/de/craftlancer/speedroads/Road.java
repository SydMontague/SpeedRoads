package de.craftlancer.speedroads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

public class Road {
    private double speed;
    private List<BlockData> blockData = new ArrayList<>();
    
    public Road(ConfigurationSection config) {
        this.speed = config.getDouble("speed", 0.2D);
        
        config.getStringList("blocks").forEach(a -> {
            if (a.equalsIgnoreCase("EMPTY") || a.equalsIgnoreCase("NULL") || a.equalsIgnoreCase("ANY")) {
                blockData.add(null);
                return;
            }
            
            int index = a.indexOf('[');
            
            if (index == -1)
                index = a.length();
            
            String material = a.substring(0, index).trim();
            String data = a.substring(index);
            Material mat = Material.matchMaterial(material);
            blockData.add(Bukkit.createBlockData(mat, data));
        });
    }
    
    public boolean isRoadBlock(Block block) {
        if (blockData.isEmpty())
            return false;
        
        for (int i = 0; i < blockData.size(); i++) {
            if (blockData.get(i) == null)
                continue;
            
            if (!block.getRelative(0, -i, 0).getBlockData().matches(blockData.get(i)))
                return false;
        }
        
        return true;
    }
    
    public double getSpeedMod() {
        return speed;
    }
}
