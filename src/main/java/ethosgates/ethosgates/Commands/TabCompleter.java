package ethosgates.ethosgates.Commands;

import ethosgates.ethosgates.utils.RegexFileFilter;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, final String[] args) {
        if (!(sender instanceof Player)) return null;
        final Player player = (Player) sender;
        final Block targetBlock = player.getTargetBlock(null, 5);
        final List<String> arguments = new ArrayList<>();
        switch (args.length) {
            case 1:
                arguments.add("erstellen");
                arguments.add("löschen");
                arguments.add("auflisten");
                arguments.add("hilfe");
                return arguments;
            case 2:
                if (Objects.equals(args[0], "erstellen")) {
                    arguments.add(targetBlock.getX() + "");
                    arguments.add(targetBlock.getX() + " " + targetBlock.getY());
                    arguments.add(targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                    arguments.add("[Torname]");
                    return arguments;
                }
                if (Objects.equals(args[0], "löschen")) {
                    List<String> gateNames = new ArrayList<>();
                    File dir = new File("./plugins/EthosGates/");
                    FileFilter fileFilter = new RegexFileFilter("^.* " + player.getUniqueId() + ".*$");
                    File[] files = dir.listFiles(fileFilter);
                    if (Objects.requireNonNull(dir.listFiles(fileFilter)).length == 0) {
                        return Collections.singletonList(ChatColor.YELLOW + "Du hast keine Tore zum löschen.");
                    }
                    if (files == null) return Collections.singletonList("");
                    String pattern = ".*" + player.getUniqueId();
                    for (File file : files) {
                        String gateName = file.toString().replaceAll(pattern, "");
                        gateNames.add(gateName);
                    }
                    return gateNames;
                }
                return Collections.singletonList("");
            case 3:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                if (!Objects.equals(args[1], targetBlock.getX() + "")) return Collections.singletonList("[ÜberhangHöhe]");
                arguments.add(targetBlock.getY() + "");
                arguments.add(targetBlock.getY() + " " + targetBlock.getZ());
                return arguments;
            case 4:
            case 7:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                arguments.add(targetBlock.getZ() + "");
                return arguments;
            case 5:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                arguments.add(targetBlock.getX() + "");
                arguments.add(targetBlock.getX() + " " + targetBlock.getY());
                arguments.add(targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                return arguments;
            case 6:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                arguments.add(targetBlock.getY() + "");
                arguments.add(targetBlock.getY() + " " + targetBlock.getZ());
                return arguments;
            case 8:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                return Collections.singletonList("[Torname]");
            case 9:
                if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                return Collections.singletonList("[ÜberhangHöhe]");
            default: return Collections.singletonList("");
        }
    }
}
