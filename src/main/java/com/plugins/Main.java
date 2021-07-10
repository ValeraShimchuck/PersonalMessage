package com.plugins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

//Hello github
public class Main extends JavaPlugin implements CommandExecutor {
    //@Override
    private static final String url = "jdbc:mysql://localhost:3306/test";
    private static final String user = "root";
    private static final String password = "root";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    private static Main instance;
    private FileConfiguration data;
    private File datafile;
    public void onEnable() {
        //String query = "select ";


        File config = new File(getDataFolder()+ File.separator+"config.yml");
        if(!config.exists()){
            getLogger().info("Creating cfg file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        getDataFolder().mkdirs();
        datafile = new File(getDataFolder()+File.separator+"data.yml");
        if(!datafile.exists()){
            try {
                datafile.createNewFile();
                data = YamlConfiguration.loadConfiguration(datafile);
                data.createSection("players");
                data.save(datafile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        data = YamlConfiguration.loadConfiguration(datafile);

        getLogger().info(String.valueOf(data.getKeys(true)));
        getLogger().info("Plugin PersonalMessage is enable");
        getCommand("pm").setExecutor(this);
        getCommand("ignore").setExecutor(this);



    }
    //@Override
    public void onDisable() {
        getLogger().info("Plugin PersonalMessage is disable");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = sender.getName();
        if(cmd.getName().equals("ignore"))
        {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(sender.equals(p)){

                sender.sendMessage(ChatColor.RED+this.getConfig().getString("messages.ysignore"));
                return true;
            }
            getLogger().info("Pass");
            if(args.length != 1) return false;
            Player pign = Bukkit.getServer().getPlayer(args[0]);
            getLogger().info("Pass");
            //data.createPath(data,name);
            //data.set(name,"");
            if(pign == null){
                sender.sendMessage(this.getConfig().getString("messages.offline"));
                getLogger().info("Passed");
                return true;
            }
            //Set<String> keys = data.getConfigurationSection("players").getKeys(false);
            //getLogger().info("Pass");
            if(data.isSet("players."+name)){
                getLogger().info("then");
                Set<String> keys = data.getConfigurationSection("players").getKeys(false);
                getLogger().info("Pass");
                for(String k: keys){
                    if(k.equals(name)){
                        if(!data.getBoolean("players."+name+"."+args[0],true)){
                            getLogger().info("Passed");
                            data.set("players."+name+"."+args[0],true);
                            try {
                                data.save(datafile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sender.sendMessage(this.getConfig().getString("messages.onignore"));
                            return true;
                        }else{
                            getLogger().info("Passed");
                            data.set("players."+name+"."+args[0],false);
                            try {
                                data.save(datafile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sender.sendMessage(this.getConfig().getString("messages.unignore"));
                            return true;
                        }
                    }
                }
                data.getConfigurationSection("players").createSection(name);
                getLogger().info("Passed");
                data.set("players."+name+"."+args[0],true);
                try {
                    data.save(datafile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(this.getConfig().getString("messages.onignore"));
                return true;
            }else{
                getLogger().info("else");
                data.getConfigurationSection("players").createSection(name);
                getLogger().info("Pass");
                data.set("players."+name+"."+args[0],true);
                try {
                    data.save(datafile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(this.getConfig().getString("messages.onignore"));
                return true;
            }


        }
        if(args.length < 2) return false;
        if(data.isSet("players." +name)){
            if(data.getBoolean("players."+name+"."+args[0],false)){
                sender.sendMessage(this.getConfig().getString("messages.ignoreys"));
                return true;
            }
        }
        if(data.isSet("players."+args[0])){
            if(data.getBoolean("players."+args[0]+"."+name,false)){
                sender.sendMessage(this.getConfig().getString("messages.ignore"));
                return true;
            }
        }
        Player p = Bukkit.getServer().getPlayer(args[0]);
        if(sender.equals(p)){

            sender.sendMessage(ChatColor.RED+this.getConfig().getString("messages.yourself"));
            return true;
        }
        if(p == null){
            sender.sendMessage(this.getConfig().getString("messages.offline"));
            return true;
        }
        String s = "";
        for (int i = 1; i < args.length; i++) {
            s = s + " "+ args[i];
        }

        p.sendMessage(ChatColor.GOLD + "["+name+"->"+"me]:"+s);
        sender.sendMessage(ChatColor.GOLD + "["+"me"+"->"+args[0]+"]:"+s);
        return true;
    }

    public static Main getInstance(){
        return instance;
    }
}
