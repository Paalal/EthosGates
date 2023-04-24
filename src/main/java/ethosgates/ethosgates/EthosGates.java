package ethosgates.ethosgates;

import ethosgates.ethosgates.Commands.TabCompleter;
import ethosgates.ethosgates.Commands.Commands;
import ethosgates.ethosgates.Listener.ClickListener;
import ethosgates.ethosgates.Listener.SignListener;
import ethosgates.ethosgates.utils.GateManager;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EthosGates extends JavaPlugin {

    private static int currentGateID;
    private FileConfiguration config;

    private static BlockType[] legalBlockList;

    private static EthosGates instance;
    private static GateManager gateManager;
    @Override
    public void onLoad() {
        instance = this;
        config = this.getConfig();
        if (config.contains("GateID.ID")) {
            currentGateID = config.getInt("GateID.ID");
        } else {
            currentGateID = 0;
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClickListener(), this);
        Objects.requireNonNull(getCommand("tor")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("tor")).setTabCompleter(new TabCompleter());
        if (config.contains("LegalBlockList")) {
           Object[] l = config.getStringList("LegalBlockList").toArray();
           legalBlockList = new BlockType[l.length];
            for (int i = 0; i < l.length; i++) {
                legalBlockList[i] = BlockTypes.get(l[i].toString());
            }
        } else {
            legalBlockList = new BlockType[]{
                    BlockTypes.IRON_BARS,
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
                    "minecraft:iron_bars",
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
                    "minecraft:mossy_cobblestone_wall",
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
            config.set("LegalBlockList", legalBlockListString);
            saveConfig();
        }
        gateManager = new GateManager();
    }

    public static EthosGates getInstance() {
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
        config.set("GateID.ID", currentGateID);
        saveConfig();
    }

    public static BlockType[] getLegalBlockList() {
        return legalBlockList;
    }
}
