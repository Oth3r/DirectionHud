package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;
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
        Player player = new Player(plr);
        assert player != null;
        Destination.commandExecutor.logic(player, Helper.Command.quoteHandler(args));
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // if not player no tab complete
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return new ArrayList<>();
        }
        Player player = new Player(plr);
        // fix args
        args = Helper.Command.quoteHandler(args);
        return Destination.commandSuggester.logic(player,args.length,args);
    }
}