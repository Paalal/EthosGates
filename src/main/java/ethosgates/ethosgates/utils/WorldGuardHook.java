package ethosgates.ethosgates.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    public WorldGuardHook(){}

    public boolean testState(Player player, World world, Sign sign) {
        player.sendMessage("Testing");
        Location location = new Location(BukkitAdapter.adapt(world), sign.getX(), sign.getY(), sign.getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return set.testState(localPlayer, Flags.USE);
    }
}
