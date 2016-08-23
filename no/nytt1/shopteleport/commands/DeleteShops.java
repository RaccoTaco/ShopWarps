package no.nytt1.shopteleport.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.nytt1.shopteleport.ShopTP;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.configuration.file.FileConfiguration;

public class DeleteShops implements Runnable 
{
    ShopTP plugin;
    FileConfiguration shopconfig;
    File shopfile;
    String username;
    
    public DeleteShops(ShopTP plugin, FileConfiguration shopconfig, File shopfile, String username)
    {
        this.plugin = plugin;
        this.shopfile = shopfile;
        this.shopconfig = shopconfig;
        this.username = username;
    }

    @Override
    public void run() 
    {
        shopconfig.set("shops." + username, null);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail " + username + " " + plugin.getConfig().getString("messages.shop-exp"));
        getLogger().info(username + "'s was shop deleted.");

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
