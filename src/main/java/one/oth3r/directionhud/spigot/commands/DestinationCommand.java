package one.oth3r.directionhud.spigot.commands;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.spigot.utils.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class DestinationCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return true;
        }
        Player player = Player.of(plr);
        assert player != null;
        Destination.commandExecutor.logic(player,args);
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        int pos = args.length;
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return new ArrayList<>();
        }
        Player player = Player.of(plr);
        assert player != null;
        return Destination.commandSuggester.logic(player,pos,args);
    }
}