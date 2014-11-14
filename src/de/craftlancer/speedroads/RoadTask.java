package de.craftlancer.speedroads;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Deprecated
public class RoadTask implements Runnable
{
    private SpeedRoads instance;
    
    public RoadTask(SpeedRoads plugin)
    {
        instance = plugin;
    }
    
    @Override
    public void run()
    {
        for (Player player : instance.getServer().getOnlinePlayers())
        {
            Block block = player.getLocation().getBlock();
            boolean onRoad = false;
            
            for (Road road : instance.roads)
                if (!onRoad && (road.block0 == null || block.getType() == road.block0) && (road.block1 == null || block.getRelative(0, -1, 0).getType() == road.block1) && (road.block2 == null || block.getRelative(0, -2, 0).getType() == road.block2) && (road.block3 == null || block.getRelative(0, -3, 0).getType() == road.block3))
                {
                    onRoad = true;
                    player.setWalkSpeed(road.speed);
                    player.setExhaustion(0);
                }
            
            //if (!onRoad)
            //    player.setWalkSpeed(SpeedRoads.getDefaultSpeed());
        }
    }
}