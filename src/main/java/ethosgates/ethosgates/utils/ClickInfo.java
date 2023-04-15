package ethosgates.ethosgates.utils;

import org.bukkit.entity.Player;

public class ClickInfo {
    private final Player player;
    private final int x;
    private final int y;
    private final int z;

    public ClickInfo(Player player, int x, int y, int z) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
