package net.miblue.chron;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Main on 10/25/15.
 */
public class Chron extends JavaPlugin{
    ArrayList<Job> jobs = new ArrayList<Job>();

    @Override
    public void onEnable(){
        saveDefaultConfig();
        for(String key : getConfig().getConfigurationSection("jobs").getKeys(false)){
            List<String> commands = getConfig().getStringList("jobs." + key + ".commands");
            long start = getConfig().getLong("jobs." + key + ".interval.start");
            long repeat = getConfig().getLong("jobs." + key + ".interval.every");
            ArrayList<String> cmds = new ArrayList<String>();
            for(String s : commands){
                cmds.add(s);
            }
            Execution e = new Execution(cmds);
            Job j = new Job(e, start, repeat, key);
            jobs.add(j);
            if(!getConfig().isSet("data." + key)){
                getConfig().set("data." + key, 0);
            }
        }
        saveConfig();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Process(), 20, 20);
    }

    @Override
    public void onDisable(){
        saveConfig();
    }

    public class Process implements Runnable {
        public void run(){
            reloadConfig();
            long time = System.currentTimeMillis();
            for(Job j : jobs){
                if(j.getStart() <= time){
                    long lastRun = getConfig().getLong("data." + j.getKey());
                    long repeat = j.getRepeat();

                    if(lastRun + repeat <= time){
                        getLogger().info("Running CHRON job: " + j.getKey());
                        getConfig().set("data." + j.getKey(), time);
                        j.getExecution().execute();
                    }
                }
            }
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chron.runjobs")){
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        if(args.length != 1){
            sender.sendMessage(ChatColor.DARK_PURPLE +"Available Jobs -----------------------------");
            for(Job j : jobs){
                sender.sendMessage(ChatColor.DARK_PURPLE + "  • " +ChatColor.LIGHT_PURPLE+ j.getKey() + " (in " + until(j) + ")");
            }
            sender.sendMessage(ChatColor.DARK_PURPLE + "Usage: /chron <job> // run <job>");
            return true;
        }
        String job = args[0];
        for(Job j : jobs){
            if(j.getKey().equals(job)){
                if(sender.hasPermission("chron.job." + j.getKey()) || sender.hasPermission("chron.runall")){
                    j.getExecution().execute();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + job + ChatColor.DARK_PURPLE + " successfully executed.");
                }else{
                    sender.sendMessage(ChatColor.RED + "You do not have access to this job. That would require: " + ChatColor.DARK_RED + "chron.runall or " + "chron.job." + j.getKey());
                }
                return true;
            }
        }
        return true;
    }

    public String until(Job j){
        long lastRun = getConfig().getLong("data." + j.getKey());
        long repeat = j.getRepeat();

        getLogger().info("lastrun/repeat " + lastRun + "/" + repeat);

        long until = (lastRun + repeat) - System.currentTimeMillis();

        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;
        if(until <= 999){
            return "now";
        }
        while(until > 999){
            seconds++;
            if(seconds == 60){
                minutes++;
                seconds = 0;
            }
            if(minutes == 60){
                hours++;
                minutes = 0;
            }
            if(hours == 24){
                days++;
                hours = 0;
            }
            if(days == 7){
                weeks++;
                days = 0;
            }
            until -= 1000;
        }
        StringBuilder out = new StringBuilder();
        if(weeks != 0){
            out.append(weeks + " weeks, ");
        }
        if(days != 0){
            out.append(days + " days, ");
        }
        if(hours != 0){
            out.append(hours + " hours, ");
        }
        if(minutes != 0){
            out.append(minutes + " minutes, ");
        }
        if(seconds != 0){
            out.append(seconds + " seconds");
        }
        return out.toString().trim();
    }
}
