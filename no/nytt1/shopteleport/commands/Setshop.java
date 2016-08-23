package no.nytt1.shopteleport.commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.nytt1.shopteleport.Messages;
import no.nytt1.shopteleport.ShopTP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Setshop implements CommandExecutor 
{
    private final ShopTP plugin; // The global variable.
    String defaultMsg = " ";
    FileConfiguration shops;
    File shopfile;
    
    public Setshop(ShopTP plugin, String defaultMsg, FileConfiguration shops, File shopfile) // A method without a return type and the classes name is a constructor
    {
        this.plugin = plugin; //Save the given reference in our global variable.
        this.defaultMsg = defaultMsg;
        this.shops = shops;
        this.shopfile = shopfile;
    }

    public boolean isSafe(Location loc)
    {
	Location loc2 = loc;
	Location loc3 = loc;
	loc2.setY(loc.getY()+1);	
	loc3.setY(loc.getY()-1);
        
	if (!loc.getBlock().isEmpty() && !loc2.getBlock().isEmpty() && loc3.getBlock().isLiquid())
            return false;																			
	return true;
    }
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if (args.length == 0 && (player.hasPermission("shopteleport.setshop") || player.isOp()))
            {
		String playername1 = player.getName().toLowerCase();
		Location loc = player.getLocation();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		double yaw = loc.getYaw();
		double pitch = loc.getPitch();
                String exp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() + plugin.getConfig().getInt("config.shop-time")*1000);
		World world = loc.getWorld();
		        
		if (isSafe(loc) == true)
                {
                    shops.set("shops." + playername1 + ".name", player.getName());
                    shops.set("shops." + playername1 + ".x", x);
                    shops.set("shops." + playername1 + ".y", y);
                    shops.set("shops." + playername1 + ".z", z);
                    shops.set("shops." + playername1 + ".yaw", yaw);
		    shops.set("shops." + playername1 + ".pitch", pitch);
		    shops.set("shops." + playername1 + ".world", world.getName());
                    shops.set("shops." + playername1 + ".exp", exp);
		    shops.set("shops." + playername1 + ".message", defaultMsg);
                    
                    try 
                    {
                        shops.save(shopfile);
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(Setshop.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
                    
                    try 
                    {
                        long time = (df.parse(exp).getTime() - new Date().getTime())/1000;
                        Bukkit.getScheduler().runTaskLater(plugin, new DeleteShops(plugin, shops, shopfile, playername1), time * 20);
                        player.sendMessage(Messages.colours(plugin.getConfig().getString("prefix") + " " + plugin.getConfig().getString("messages.shop-set")));
                    } 
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(Setshop.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                else 
		    player.sendMessage(Messages.colours(plugin.getConfig().getString("prefix") + " " + plugin.getConfig().getString("messages.location-not-safe")));
		return true;
            } 
            else 
            {
		player.sendMessage(Messages.colours(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
	}		
	return true;
    }
}
