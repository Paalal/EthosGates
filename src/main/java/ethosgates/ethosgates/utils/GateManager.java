package ethosgates.ethosgates.utils;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;

import ethosgates.ethosgates.EthosGates;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.sqrt;

public class GateManager {

    private BlockType[] legalBlockList = EthosGates.getLegalBlockList();

    public boolean toggleGate(final int gateID, final org.bukkit.World world, final BlockVector3 pos, final Player p) {
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^" + gateID + " .*$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            return false;
        }
        dir = Objects.requireNonNull(files)[0];
        File gatePropertiesFile = new File(dir, "/gateProperties.yml");
        YamlConfiguration gateProperties = YamlConfiguration.loadConfiguration(gatePropertiesFile);

        double height = 1.0 * (int) gateProperties.get("Dimensions.Height");
        double width =  1.0 * (int) gateProperties.get("Dimensions.Height");

        Object[] BlockVectorMaxArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMax").toArray();
        BlockVector3 max = BlockVector3.at(Integer.parseInt(BlockVectorMaxArray[0].toString()), Integer.parseInt(BlockVectorMaxArray[1].toString()), Integer.parseInt(BlockVectorMaxArray[2].toString()));
        Object[] BlockVectorMinArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMin").toArray();
        BlockVector3 min = BlockVector3.at(Integer.parseInt(BlockVectorMinArray[0].toString()), Integer.parseInt(BlockVectorMinArray[1].toString()), Integer.parseInt(BlockVectorMinArray[2].toString()));
        int[] m;
        if (min.getX() == max.getX()) {
            m = new int[]{min.getX(), (int) (min.getY() + (height / 2 + width / 2) / 2), (int) (min.getZ() + (height / 2 + width / 2) / 2)};
        } else {
            m = new int[]{(int) (min.getX() + (height / 2 + width / 2) / 2), (int) (min.getY() + (height / 2 + width / 2) / 2), min.getZ()};
        }
        double distance = sqrt(((pos.getX() - m[0]) * (pos.getX() - m[0])) + ((pos.getY() - m[1]) * (pos.getY() - m[1])) + ((pos.getZ() - m[2]) * (pos.getZ() - m[2])));
        if (distance > sqrt(height * width) + 7) {
            p.sendMessage(ChatColor.YELLOW + "Das Tor ist zu weit entfernt!");
            return true;
        }
        if (Objects.equals(Objects.requireNonNull(gateProperties.get("State")).toString(), "closed")) {
            p.sendMessage("§7öffnet");
            return openGate(gateID, world);
        } else if (Objects.equals(Objects.requireNonNull(gateProperties.get("State")).toString(), "open")) {
            p.sendMessage("§7schließt");
            return closeGate(gateID, world);
        } else {
            p.sendMessage("§eDas Tor §6schließt §eoder §6öffnet §eschon.");
            return true;
        }
    }

    public boolean redstoneOpenGate(final int gateID, final org.bukkit.World world, final BlockVector3 pos) {
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^" + gateID + " .*$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            return false;
        }
        dir = Objects.requireNonNull(files)[0];
        File gatePropertiesFile = new File(dir, "/gateProperties.yml");
        YamlConfiguration gateProperties = YamlConfiguration.loadConfiguration(gatePropertiesFile);

        double height = 1.0 * (int) gateProperties.get("Dimensions.Height");
        double width =  1.0 * (int) gateProperties.get("Dimensions.Height");

        Object[] BlockVectorMaxArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMax").toArray();
        BlockVector3 max = BlockVector3.at(Integer.parseInt(BlockVectorMaxArray[0].toString()), Integer.parseInt(BlockVectorMaxArray[1].toString()), Integer.parseInt(BlockVectorMaxArray[2].toString()));
        Object[] BlockVectorMinArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMin").toArray();
        BlockVector3 min = BlockVector3.at(Integer.parseInt(BlockVectorMinArray[0].toString()), Integer.parseInt(BlockVectorMinArray[1].toString()), Integer.parseInt(BlockVectorMinArray[2].toString()));
        int[] m;
        if (min.getX() == max.getX()) {
            m = new int[]{min.getX(), (int) (min.getY() + (height / 2 + width / 2) / 2), (int) (min.getZ() + (height / 2 + width / 2) / 2)};
        } else {
            m = new int[]{(int) (min.getX() + (height / 2 + width / 2) / 2), (int) (min.getY() + (height / 2 + width / 2) / 2), min.getZ()};
        }
        double distance = sqrt(((pos.getX() - m[0]) * (pos.getX() - m[0])) + ((pos.getY() - m[1]) * (pos.getY() - m[1])) + ((pos.getZ() - m[2]) * (pos.getZ() - m[2])));
        if (distance > sqrt(height * width) + 7) {
            return false;
        }
        return openGate(gateID, world);
    }

    public void redstoneCloseGate(final int gateID, final org.bukkit.World world, final BlockVector3 pos) {
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^" + gateID + " .*$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            return;
        }
        dir = Objects.requireNonNull(files)[0];
        File gatePropertiesFile = new File(dir, "/gateProperties.yml");
        YamlConfiguration gateProperties = YamlConfiguration.loadConfiguration(gatePropertiesFile);

        double height = 1.0 * (int) gateProperties.get("Dimensions.Height");
        double width =  1.0 * (int) gateProperties.get("Dimensions.Width");

        Object[] BlockVectorMaxArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMax").toArray();
        BlockVector3 max = BlockVector3.at(Integer.parseInt(BlockVectorMaxArray[0].toString()), Integer.parseInt(BlockVectorMaxArray[1].toString()), Integer.parseInt(BlockVectorMaxArray[2].toString()));
        Object[] BlockVectorMinArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMin").toArray();
        BlockVector3 min = BlockVector3.at(Integer.parseInt(BlockVectorMinArray[0].toString()), Integer.parseInt(BlockVectorMinArray[1].toString()), Integer.parseInt(BlockVectorMinArray[2].toString()));
        int[] m;
        if (min.getX() == max.getX()) {
            m = new int[]{min.getX(), (int) (min.getY() + (height / 2 + width / 2) / 2), (int) (min.getZ() + (height / 2 + width / 2) / 2)};
        } else {
            m = new int[]{(int) (min.getX() + (height / 2 + width / 2) / 2), (int) (min.getY() + (height / 2 + width / 2) / 2), min.getZ()};
        }
        double distance = sqrt(((pos.getX() - m[0]) * (pos.getX() - m[0])) + ((pos.getY() - m[1]) * (pos.getY() - m[1])) + ((pos.getZ() - m[2]) * (pos.getZ() - m[2])));
        if (distance > sqrt(height * width) + 7) {
            return;
        }
        closeGate(gateID, world);
    }

    public boolean openGate(final int gateID, final org.bukkit.World world) {
        //get gate properties
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^" + gateID + " .*$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            return false;
        }
        dir = Objects.requireNonNull(files)[0];
        File gatePropertiesFile = new File(dir, "/gateProperties.yml");
        YamlConfiguration gateProperties = YamlConfiguration.loadConfiguration(gatePropertiesFile);
        if (!Objects.equals(Objects.requireNonNull(gateProperties.get("State")).toString(), "closed")) {
            return false;
        }
        gateProperties.set("State", "opening");
        try {
            gateProperties.save(gatePropertiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object[] BlockVectorMaxArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMax").toArray();
        BlockVector3 max = BlockVector3.at(Integer.parseInt(BlockVectorMaxArray[0].toString()), Integer.parseInt(BlockVectorMaxArray[1].toString()), Integer.parseInt(BlockVectorMaxArray[2].toString()));
        Object[] BlockVectorMinArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMin").toArray();
        BlockVector3 min = BlockVector3.at(Integer.parseInt(BlockVectorMinArray[0].toString()), Integer.parseInt(BlockVectorMinArray[1].toString()), Integer.parseInt(BlockVectorMinArray[2].toString()));
        int height = gateProperties.getInt("Dimensions.Height");
        int width = gateProperties.getInt("Dimensions.Width");
        int overhang = gateProperties.getInt("Overhang");
        int[] m;
        if (min.getX() == max.getX()) {
            m = new int[]{min.getX(), (int) (min.getY() + (height / 2 + width / 2) / 2), (int) (min.getZ() + (height / 2 + width / 2) / 2)};
        } else {
            m = new int[]{(int) (min.getX() + (height / 2 + width / 2) / 2), (int) (min.getY() + (height / 2 + width / 2) / 2), min.getZ()};
        }
        Location location = new Location(world , 1.0 * m[0],1.0 * m[1], 1.0 * m[2]);

        dir = new File(dir, "/schematics/");
        //open-gate-logic
        final Clipboard[] clipboard = {null};
        File finalDir = dir;
        new BukkitRunnable() {
            int step = 0;
            @Override
            public void run() {
                for (int row = 0; row < height; row++) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.5F, 1F);
                        player.playSound(location, Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1F, 1F);
                    }
                    if (row - step >= 0) {
                        if (step - row == 0) {
                            clipboard[0] = loadSchematic(finalDir, "air");
                        } else {
                            clipboard[0] = loadSchematic(finalDir, Integer.toString(row - step));
                        }
                        BlockVector3 pos1 = BlockVector3.at(min.getX(), min.getY() + row, min.getZ());
                        BlockVector3 pos2 = BlockVector3.at(max.getX(), min.getY() + row, max.getZ());
                        boolean success = saveRowSchematic( BukkitAdapter.adapt(world), pos1, pos2, finalDir, String.valueOf(row - step + 1));
                        pasteSchematic(clipboard[0], pos1, BukkitAdapter.adapt(world));
                        if (!success) {
                            deleteGate(new File (finalDir.getPath().replace("schematics", ""), ""));
                            cancel();
                        }
                    }

                }
                step++;
                if (step + overhang == height) {
                    gateProperties.set("State", "open");
                    try {
                        gateProperties.save(gatePropertiesFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cancel();
                }
            }
        }.runTaskTimer(EthosGates.getInstance(), 0, 20);
        return true;
    }

    public boolean closeGate(final int gateID, final org.bukkit.World world) {
        //get gate properties
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^" + gateID + " .*$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            return false;
        }
        dir = Objects.requireNonNull(files)[0];
        File gatePropertiesFile = new File(dir, "/gateProperties.yml");
        YamlConfiguration gateProperties = YamlConfiguration.loadConfiguration(gatePropertiesFile);
        if (!Objects.equals(Objects.requireNonNull(gateProperties.get("State")).toString(), "open")) {
            return false;
        }
        gateProperties.set("State", "closing");
        try {
            gateProperties.save(gatePropertiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object[] BlockVectorMaxArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMax").toArray();
        BlockVector3 max = BlockVector3.at(Integer.parseInt(BlockVectorMaxArray[0].toString()), Integer.parseInt(BlockVectorMaxArray[1].toString()), Integer.parseInt(BlockVectorMaxArray[2].toString()));
        Object[] BlockVectorMinArray = gateProperties.getIntegerList("BlockVectors.BlockVectorMin").toArray();
        BlockVector3 min = BlockVector3.at(Integer.parseInt(BlockVectorMinArray[0].toString()), Integer.parseInt(BlockVectorMinArray[1].toString()), Integer.parseInt(BlockVectorMinArray[2].toString()));
        int height = gateProperties.getInt("Dimensions.Height");
        int width = gateProperties.getInt("Dimensions.Width");
        int overhang = gateProperties.getInt("Overhang");
        int[] m;
        if (min.getX() == max.getX()) {
            m = new int[]{min.getX(), (int) (min.getY() + (height / 2 + width / 2) / 2), (int) (min.getZ() + (height / 2 + width / 2) / 2)};
        } else {
            m = new int[]{(int) (min.getX() + (height / 2 + width / 2) / 2), (int) (min.getY() + (height / 2 + width / 2) / 2), min.getZ()};
        }
        Location location = new Location(world , 1.0 * m[0],1.0 * m[1], 1.0 * m[2]);

        dir = new File(dir, "/schematics/");
        //close-gate-logic
        final Clipboard[] clipboard = {null};
        File finalDir = dir;
        new BukkitRunnable() {
            int step = 1 + overhang;
            @Override
            public void run() {
                for (int row = 0; row < height; row++) {
                    BlockVector3 pos1 = BlockVector3.at(min.getX(), max.getY() - row, min.getZ());
                    BlockVector3 pos2 = BlockVector3.at(max.getX(), max.getY() - row, max.getZ());
                    if (step - row > 1) {
                        if (!saveRowSchematic(BukkitAdapter.adapt(world), pos1, pos2, finalDir, String.valueOf(step - row - 1))) {
                            deleteGate(new File(finalDir.getPath().replace("schematics", ""), ""));
                            cancel();
                        }
                    }
                    if (step - row > 0) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.5F, 1F);
                            player.playSound(location, Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1F, 1F);
                        }
                        clipboard[0] = loadSchematic(finalDir, Integer.toString(step - row));
                        pasteSchematic(clipboard[0], pos1, BukkitAdapter.adapt(world));
                    }
                }
                step++;
                if (step > height) {
                    gateProperties.set("State", "closed");
                    try {
                        gateProperties.save(gatePropertiesFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(location, Sound.BLOCK_ANVIL_PLACE, 0.3F, 1F);
                        player.playSound(location, Sound.BLOCK_LANTERN_PLACE, 1F, 0.5F);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(EthosGates.getInstance(), 0, 5);
        return true;
    }

    public boolean createGate(final Player p, final BlockVector3 maximum, final BlockVector3 minimum, int overhang, final File dir) {
        World world = BukkitAdapter.adapt(p.getWorld());
        if (!saveGateSchematics(world, maximum, minimum, dir)) {
            return false;
        }
        //save gate data
        File file = new File(dir.getPath().replace("schematics", ""), "gateProperties.yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("Owner.Name", p.getName());

        String[] BlockVectorMax = {Integer.toString(maximum.getX()), Integer.toString(maximum.getY()), Integer.toString(maximum.getZ())};
        String[] BlockVectorMin = {Integer.toString(minimum.getX()), Integer.toString(minimum.getY()), Integer.toString(minimum.getZ())};
        config.set("BlockVectors.BlockVectorMax", Arrays.asList(BlockVectorMax));
        config.set("BlockVectors.BlockVectorMin", Arrays.asList(BlockVectorMin));

        int height = maximum.getY() - minimum.getY() + 1;
        int width = (maximum.getX() - minimum.getX()) + (maximum.getZ() - minimum.getZ()) + 1;
        config.set("Dimensions.Height", height);
        config.set("Dimensions.Width", width);
        config.set("Overhang", overhang);
        config.set("State", "closed");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteGate(final File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteGate(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    public boolean saveRowSchematic(final World world, final BlockVector3 min, final BlockVector3 max, final File dir, final String schemName) {
        //test for illegal blocks
        boolean allLegal = true;
        if (max.getX() - min.getX() == 0) {
            for (int i = min.getZ(); i <= max.getZ(); i++) {
                boolean legal = false;
                for (BlockType block : legalBlockList) {
                    if (block == world.getBlock(BlockVector3.at(max.getX(), max.getY(), i)).getBlockType()) {
                        legal = true;
                    }
                }
                if (!legal) {
                    allLegal = false;
                }
            }
        } else {
            for (int i = min.getX(); i <= max.getX(); i++) {
                boolean legal = false;
                for (BlockType block : legalBlockList) {
                    if (block == world.getBlock(BlockVector3.at(i, max.getY(), max.getZ())).getBlockType()) {
                        legal = true;
                    }
                }
                if (!legal) {
                    allLegal = false;
                }
            }
        }
        if(!allLegal) {
            return false;
        }
        //copy schematic
        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld());
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(false);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        //save schematic
        File file = new File(dir, schemName + ".schematic");
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean saveGateSchematics(final World world, final BlockVector3 maximum, final BlockVector3 minimum, final File dir) {
        //save gate schematics row by row
        for (int i = 0; i < maximum.getY() - minimum.getY() + 1; i++) {

            BlockVector3 max = BlockVector3.at(maximum.getX(), minimum.getY() + i, maximum.getZ());
            BlockVector3 min = BlockVector3.at(minimum.getX(), minimum.getY() + i, minimum.getZ());

            String schemName = String.valueOf(i + 1);
            if (!saveRowSchematic(world, min, max, dir, schemName)) {
                return false;
            }
        }
        //save air-schematic
        BlockVector3 max = BlockVector3.at(maximum.getX(), 320, maximum.getZ());
        BlockVector3 min = BlockVector3.at(minimum.getX(), 320, minimum.getZ());
        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld());
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(false);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        File file = new File(dir,"air.schematic");
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Clipboard loadSchematic(final File dir, final String schematicName) {
        File file = new File(dir,  schematicName + ".schematic");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void pasteSchematic(final Clipboard clipboard, final BlockVector3 pos, final World world) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(pos)
                    .ignoreAirBlocks(false)
                    .build();
            try {
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
    }
}
