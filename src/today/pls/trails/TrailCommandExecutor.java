package today.pls.trails;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrailCommandExecutor implements CommandExecutor {

    private PLSTrails pl;

    public TrailCommandExecutor(PLSTrails _pl) {
        pl = _pl;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            ((Player) commandSender).openInventory(pl.getTrailsInventory(((Player) commandSender)));
            return true;
        }
        return false;
    }
}
