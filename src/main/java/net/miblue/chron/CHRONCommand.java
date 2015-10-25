package net.miblue.chron;

import org.bukkit.Bukkit;

import java.io.IOException;

/**
 * Created by Main on 10/25/15.
 */
public class CHRONCommand {
    private String command;
    private Execution.Type type;
    public CHRONCommand(String command, Execution.Type type){
        this.command = command;
        this.type = type;
    }

    public void execute(){
        if(type == Execution.Type.GAME){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }else{
            try {
                Runtime.getRuntime().exec(command);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public String parseVariables(String s){
        s = s.replaceAll("{epoch}", System.currentTimeMillis() + "");
        return s;
    }
}
