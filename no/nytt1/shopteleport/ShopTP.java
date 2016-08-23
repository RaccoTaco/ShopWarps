package no.nytt1.shopteleport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.nytt1.shopteleport.commands.DeleteShops;
import no.nytt1.shopteleport.commands.Setshop;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopTP extends JavaPlugin
{
    public static ShopTP plugin;
    public Object[] playershops;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    File shopfile = new File(getDataFolder(), "shops.yml");
    FileConfiguration shopconfig = YamlConfiguration.loadConfiguration(shopfile);
    
    @Override
    public void onDisable()
    {
	Bukkit.getScheduler().cancelAllTasks();
    }

    @Override
    public void onEnable()
    {
	plugin = this;
	
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();
	saveConfig();

        if(!shopfile.exists())
        {
            try 
            {
                shopfile.createNewFile();
                shopconfig.save(shopfile);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(ShopTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        playershops = shopconfig.getConfigurationSection("shops").getKeys(false).toArray();
        
        if (playershops != null)
        {
            for (int r = 0; r < playershops.length; r++)
            {
                try 
                {
                    long time = (df.parse(shopconfig.getString("shops." + playershops[r].toString() + ".exp")).getTime() - new Date().getTime())/1000;
                    Bukkit.getScheduler().runTaskLater(plugin, new DeleteShops(plugin, shopconfig, shopfile, playershops[r].toString()), time * 20);
                } 
                catch (ParseException ex) 
                {
                    Logger.getLogger(ShopTP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        this.getCommand("setshop").setExecutor(new Setshop(this, Messages.colours(getConfig().getString("messages.default-shop-msg")), shopconfig, shopfile));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
    {	
	if (sender instanceof Player && (cmd.getName().equalsIgnoreCase("shop") || cmd.getName().equalsIgnoreCase("delshop")))
        {
            Player player = (Player) sender;
            
            String NoPermission = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.no-permission"));
            String TpedToYourShop = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.tped-to-your-shop"));
            String NoShop = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.no-shop"));
            String NoTp = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.no-tp"));
            String Tped = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.tped"));
            String DoesntExsist = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.doesnt-exsist"));
            String ShopDeleted = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.shop-deleted"));
            String NoShopDeleted = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.no-shop-deleted"));
            String MessageSet = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.message-set"));
            String NoShops = Messages.colours(getConfig().getString("prefix") + " " + getConfig().getString("messages.no-shops"));
            
            if (cmd.getName().equalsIgnoreCase("shop") && player.hasPermission("shopteleport.shop"))
            {
                if (args.length == 0) 
                {
                    String playername1 = player.getName().toLowerCase();
                    if (StringUtils.isNotBlank(shopconfig.getString("shops." + playername1 + ".world"))) 
                    {
                        double x = shopconfig.getDouble("shops." + playername1 + ".x");
                        double y = shopconfig.getDouble("shops." + playername1 + ".y");
                        double z = shopconfig.getDouble("shops." + playername1 + ".z");
                        float yaw = (float) shopconfig.getDouble("shops." + playername1 + ".yaw");
                        float pitch = (float) shopconfig.getDouble("shops." + playername1 + ".pitch");
                        Location loc = new Location(getServer().getWorld(shopconfig.getString("shops." + playername1 + ".world")), x, y, z); //defines new location
                        loc.setPitch(pitch);
                        loc.setYaw(yaw);

                        if(isSafe(loc)==true)
                        {
                            player.teleport(loc);
                            player.sendMessage(TpedToYourShop);
                        }
                        else
                            player.sendMessage(NoTp);       
                    }
                    else
                        player.sendMessage(NoShop);
                    return true;
                }
                else
                {
                    if (args[0].equalsIgnoreCase("help") || args[0].equals("?"))
                    {
                        if(player.hasPermission("shopteleport.admin") || player.isOp()) 
                            player.sendMessage(ChatColor.WHITE + "----" + ChatColor.GREEN + " ShopTeleport Help " + ChatColor.RED + "(Admin) " + ChatColor.WHITE + "----");
                        else
                            player.sendMessage(ChatColor.WHITE + "----" + ChatColor.GREEN + " ShopTeleport Help " + ChatColor.WHITE + "----");
							
                        player.sendMessage(ChatColor.AQUA + "/shop help" + ChatColor.WHITE + "|" + ChatColor.AQUA + "?" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Shows this help page!");
                        player.sendMessage(ChatColor.AQUA + "/setshop" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Set your shop's warp position.");
                        player.sendMessage(ChatColor.AQUA + "/shop <name>" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Teleports you to your/someone's shop.");
                        player.sendMessage(ChatColor.AQUA + "/delshop" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Deletes your shop.");
                        player.sendMessage(ChatColor.AQUA + "/shop message|msg" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Set a shop message.");
                        player.sendMessage(ChatColor.AQUA + "/shop list" + ChatColor.WHITE + " - " + ChatColor.GREEN + "List all shops.");
				
                        if(player.hasPermission("shopteleport.admin") || player.isOp()) 
                        {
                            player.sendMessage(ChatColor.BLUE + "/shop reload" + ChatColor.WHITE + " - " + ChatColor.DARK_GREEN + "Reloads Config Files!");
                            player.sendMessage(ChatColor.BLUE + "/delshop <name>" + ChatColor.WHITE + " - " + ChatColor.GREEN + "Deletes others shop.");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("reload"))
                    {
                        if(player.hasPermission("shopteleport.admin") || player.isOp()) 
                        {
                            reloadConfig();
                            PluginDescriptionFile pdfFile = this.getDescription();
                            player.sendMessage(ChatColor.GREEN + "Reloaded " + pdfFile.getName() + "!");
                        }
                        else
                            player.sendMessage(NoPermission);
                    }
                    else if (args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("message"))
                    {
                        if (shopconfig.contains("shops."+player.getName().toLowerCase()))
                        {
                            int x = 1;
                            String message = "";
                                
                            for (int i = 1; i < args.length; i++)
                            {
                                message = message+" "+args[i];
                                x++;
                            }
                            if (x == 1)
                                message = Messages.colours(getConfig().getString("messages.default-shop-msg"));
                                
                            int range = message.length();
                            int confrange = getConfig().getInt("config.msg-length");
                            if (message.length() > confrange)
                                range = confrange;
                            message = message.substring(1, range);
                            shopconfig.set("shops." + player.getName().toLowerCase() + ".message", message);
                            saveFile();
                            player.sendMessage(MessageSet+" " + ChatColor.WHITE + Messages.colours(message));
                        } 
                        else 
                            player.sendMessage(DoesntExsist);
                    }
                    else if (args[0].equalsIgnoreCase("list"))
                    {
                        int shopnum = 0;
                        playershops = shopconfig.getConfigurationSection("shops").getKeys(false).toArray();
                        
                        shopnum = playershops.length;
                        if (playershops != null)
                            shopnum = playershops.length;
                        
                        int pagenum = 1, pages = shopnum / 6, nextpage = 0;
                            
                        if (shopnum % 6 != 0)
                            pages++;
                            
                        if (args.length == 1 || !args[1].matches("\\d+"))
                            pagenum = 1;
                        else
                        {
                            pagenum = Integer.parseInt(args[1]);
                            if (pagenum > pages)
                                pagenum = pages;
                        }
                            
                        nextpage = pagenum + 1;
                            
                        if (shopnum == 0)
                            player.sendMessage(NoShops);
                        else
                        {
                            player.sendMessage(ChatColor.DARK_AQUA + "-------- " + ChatColor.WHITE + "Shop List" + ChatColor.DARK_AQUA + " --- " 
                                + ChatColor.WHITE + "Page " + pagenum + "/" + pages + ChatColor.DARK_AQUA + " --------");
                            
                            for (int n = (6 * (pagenum - 1)); (n < 6 * pagenum) && (n < shopnum); n++ )
                            {
                                String msg = "tellraw " + player.getName() + 
                                        " [\"\", {\"text\":\"" + shopconfig.getString("shops." + playershops[n].toString() + ".name") + ": " 
                                        + "\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + "/shop " + shopconfig.getString("shops." + playershops[n].toString() + ".name")
                                        + "\"}},{\"text\":\"" + shopconfig.getString("shops." + playershops[n].toString() + ".message") + "\",\"color\":\"white\"}]";
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),msg);
                            }
                            
                            if (pagenum == pages)
                                player.sendMessage(ChatColor.DARK_AQUA + "-----------" + ChatColor.RED + " End of Shop List " + ChatColor.DARK_AQUA + "-----------");
                            else
                                player.sendMessage(ChatColor.DARK_AQUA + "------ " + ChatColor.WHITE + "Next Page " + ChatColor.DARK_AQUA 
                                    + "---- " + ChatColor.WHITE + "/shop list " + nextpage + ChatColor.DARK_AQUA + " ------");
                        }
                    }
                    else
                    {
                        String playername = args[0].toLowerCase();
                        String playername1 = player.getName();
                        if (StringUtils.isNotBlank(shopconfig.getString("shops." + playername + ".world"))) 
                        {
                            double x = shopconfig.getDouble("shops." + playername + ".x");
                            double y = shopconfig.getDouble("shops." + playername + ".y");
                            double z = shopconfig.getDouble("shops." + playername + ".z");
                            float yaw = (float) shopconfig.getDouble("shops." + playername + ".yaw");
                            float pitch = (float) shopconfig.getDouble("shops." + playername + ".pitch");
                            Location loc = new Location(getServer().getWorld(shopconfig.getString("shops." + playername + ".world")), x, y, z); //defines new location
                            loc.setPitch(pitch);
                            loc.setYaw(yaw);

                            if(isSafe(loc)==true)
                            {
                                player.teleport(loc);
                                if (playername.matches(playername1.toLowerCase()))
                                    player.sendMessage(TpedToYourShop);
                                else
                                    player.sendMessage(Tped.replace("{shop}", shopconfig.getString("shops." + playername + ".name")));
                            }
                            else
                                player.sendMessage(NoTp);     
                        }
                        else
                            player.sendMessage(NoShop);
                    }
                    return true;
                } 
            }
            else if (cmd.getName().equalsIgnoreCase("delshop"))
            {
                String playername = player.getName().toLowerCase();
                if (args.length == 0 && player.hasPermission("shopteleport.delshop"))
                {
                    if (StringUtils.isNotBlank(shopconfig.getString("shops." + playername + ".world"))) 
                    {
                        shopconfig.set("shops." + playername, null);
                        saveFile();
                        player.sendMessage(ShopDeleted);
                    }
                    else
                        player.sendMessage(NoShopDeleted);
                    return true;
                }	
                else if (args.length == 1 && (player.hasPermission("shopteleport.delshop-others") || player.isOp()))
                {
                    playername = args[0].toLowerCase();
                    if (StringUtils.isNotBlank(shopconfig.getString("shops." + playername + ".world"))) 
                    {
                        shopconfig.set("shops." + playername, null);
                        saveFile();
                        player.sendMessage(ShopDeleted);
                    }
                    else
                        player.sendMessage(NoShopDeleted);
                    return true;
                }
            }		
        }
        sender.sendMessage(Messages.colours(plugin.getConfig().getString("messages.no-permission")));
        return true;
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
    
    private void saveFile()
    {
        try 
        {
            shopconfig.save(shopfile);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Setshop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}