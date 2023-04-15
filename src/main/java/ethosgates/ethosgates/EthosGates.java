package ethosgates3.ethosgates3;

import ethosgates3.ethosgates3.Commands.Commands;
import ethosgates3.ethosgates3.Commands.GateTabCompleter;
import ethosgates3.ethosgates3.Listener.SignListener;
import ethosgates3.ethosgates3.utils.Config;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private static int currentGateID;

    private Config config;

    private static BlockType[] legalBlockList;

    private static Main instance;
    private static GateManager gateManager;

    @Override
    public void onLoad() {
        instance = this;
        config = new Config();
        if (config.getConfig().contains("GateID.ID")) {
            currentGateID = config.getConfig().getInt("GateID.ID");
        } else {
            currentGateID = 0;
        }

    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Objects.requireNonNull(getCommand("tor")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("tor")).setTabCompleter(new GateTabCompleter());
        if (config.getConfig().contains("LegalBlockList")) {
            System.out.println("contains");
           Object[] l = config.getConfig().getStringList("LegalBlockList").toArray();
           legalBlockList = new BlockType[l.length];
            for (int i = 0; i < l.length; i++) {
                legalBlockList[i] = BlockTypes.get(l[i].toString());
            }
        } else {
            System.out.println("doesnt contain");
            legalBlockList = new BlockType[]{
                    BlockTypes.OAK_FENCE,
                    BlockTypes.DARK_OAK_FENCE,
                    BlockTypes.SPRUCE_FENCE,
                    BlockTypes.BIRCH_FENCE,
                    BlockTypes.JUNGLE_FENCE,
                    BlockTypes.ACACIA_FENCE,
                    BlockTypes.WARPED_FENCE,
                    BlockTypes.MANGROVE_FENCE,
                    BlockTypes.CRIMSON_FENCE,
                    BlockTypes.COBBLESTONE_WALL,
                    BlockTypes.MOSSY_COBBLESTONE_WALL,
                    BlockTypes.STONE_BRICK_WALL,
                    BlockTypes.MOSSY_STONE_BRICK_WALL,
                    BlockTypes.GRANITE_WALL,
                    BlockTypes.DIORITE_WALL,
                    BlockTypes.ANDESITE_WALL,
                    BlockTypes.COBBLED_DEEPSLATE_WALL,
                    BlockTypes.POLISHED_DEEPSLATE_WALL,
                    BlockTypes.DEEPSLATE_BRICK_WALL,
                    BlockTypes.BRICK_WALL,
                    BlockTypes.MUD_BRICK_WALL,
                    BlockTypes.SANDSTONE_WALL,
                    BlockTypes.RED_SANDSTONE_WALL,
                    BlockTypes.NETHER_BRICK_WALL,
                    BlockTypes.BLACKSTONE_WALL,
                    BlockTypes.POLISHED_BLACKSTONE_WALL,
                    BlockTypes.END_STONE_BRICK_WALL
            };
            String[] legalBlockListString = {
                    "minecraft:oak_fence",
                    "minecraft:dark_oak_fence",
                    "minecraft:spruce_fence",
                    "minecraft:birch_fence",
                    "minecraft:jungle_fence",
                    "minecraft:acacia_fence",
                    "minecraft:warped_fence",
                    "minecraft:mangrove_fence",
                    "minecraft:crimson_fence",
                    "minecraft:cobblestone_wall",
                    "minecraft:mossy_cobble_wall",
                    "minecraft:stone_brick_wall",
                    "minecraft:mossy_stone_brick_wall",
                    "minecraft:granite_wall",
                    "minecraft:diorite_wall",
                    "minecraft:andesite_wall",
                    "minecraft:cobbled_deepslate_wall",
                    "minecraft:polished_deepslate_wall",
                    "minecraft:deepslate_brick_wall",
                    "minecraft:brick_wall",
                    "minecraft:mud_brick_wall",
                    "minecraft:sandstone_wall",
                    "minecraft:red_sandstone_wall",
                    "minecraft:nether_brick_wall",
                    "minecraft:blackstone_wall",
                    "minecraft:polished_blackstone_wall",
                    "minecraft:end_stone_brick_wall"
            };
            config.getConfig().set("LegalBlockList", legalBlockListString);
            config.save();
        }
        gateManager = new GateManager();
    }

    public static Main getInstance() {
        return instance;
    }

    public static GateManager getGateManager() {
        return gateManager;
    }

    public static int getCurrentGateID() {
        return currentGateID;
    }

    public static void increaseCurrentGateID() {
        currentGateID++;
    }


    @Override
    public void onDisable() {
        //config.getConfig().set("LegalBlockList", legalBlockList);
        config.getConfig().set("GateID.ID", currentGateID);
        config.save();
    }

    public static BlockType[] getLegalBlockList() {
        return legalBlockList;
    }
}
