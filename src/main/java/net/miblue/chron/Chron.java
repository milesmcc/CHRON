package net.miblue.chron;

import org.bukkit.Bukkit;
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
            long time = System.currentTimeMillis();
            for(Job j : jobs){
                if(!(j.getStart() > time)){
                    long lastRun = getConfig().getLong("data." + j.getKey());
                    long repeat = j.getRepeat();

                    if(lastRun + repeat <= time){
                        getLogger().info("Running CHRON job: " + j.getKey());
                        getConfig().set("data." + j.getKey(), time);
                        j.getExecution().execute();
                    }
                }
            }
        }
    }
}
