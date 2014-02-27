package de.craftlancer.speedroads;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.craftlancer.groups.Plot;
import de.craftlancer.groups.managers.PlotManager;
import de.craftlancer.speedapi.SpeedModifier;

public class RoadSpeedModifier extends SpeedModifier
{
    private final SpeedRoads plugin;
    
    public RoadSpeedModifier(int priority, SpeedRoads plugin)
    {
        super(priority);
        this.plugin = plugin;
    }
    
    @Override
    public float getSpeedChange(Player p, float speed)
    {
        Plot plot = PlotManager.getPlot(p.getLocation());
        if(plot.isTownPlot())
            return 0;
        
        Block block = p.getLocation().getBlock();
        float speedMod = 0;
        
        for (Road road : plugin.getRoads())
            if (road.isRoadBlock(block) && road.getSpeedMod() > speedMod)
                speedMod = road.getSpeedMod();
        
        if(speedMod != 0)
            p.setExhaustion(0);
        
        return speed * speedMod;
    }
    
    @Override
    public boolean isApplicable(Player p)
    {
        return true;
    }
}
