package de.craftlancer.speedroads;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class Road
{
    protected Material block0;
    protected Material block1;
    protected Material block2;
    protected Material block3;
    protected float speed;
    
    public Road(String key, FileConfiguration config)
    {
        block0 = getMaterial(config.getString("roads." + key + ".block0", "-1"));
        block1 = getMaterial(config.getString("roads." + key + ".block1", "-1"));
        block2 = getMaterial(config.getString("roads." + key + ".block2", "-1"));
        block3 = getMaterial(config.getString("roads." + key + ".block3", "-1"));
        speed = (float) config.getDouble("roads." + key + ".speed", 0.2);
    }
    
    private static Material getMaterial(String string)
    {
        if (string.equals("-1"))
            return null;
        
        return Material.getMaterial(string);
    }
    
    public boolean isRoadBlock(Block block)
    {
        if (block0 == null || block.getType() == block0)
            if (block1 == null || block.getRelative(0, -1, 0).getType() == block1)
                if (block2 == null || block.getRelative(0, -2, 0).getType() == block2)
                    if (block3 == null || block.getRelative(0, -3, 0).getType() == block3)
                        return true;
        
        return false;
    }
    
    public float getSpeedMod()
    {
        return speed;
    }
}
