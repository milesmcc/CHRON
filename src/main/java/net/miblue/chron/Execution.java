package net.miblue.chron;

import java.util.ArrayList;

/**
 * Created by Main on 10/25/15.
 */
public class Execution {
    public enum Type {
        GAME, SHELL;
    }

    public Execution(ArrayList<String> cmds){
        for(String s : cmds){
            Type type = Type.SHELL;
            if(s.startsWith("g:")){
                type = Type.GAME;

            }else if(s.startsWith("s:")){
                // nothing, type is already shell
            }
            String command = s.substring(2);
            commands.add(new CHRONCommand(command, type));
        }
    }


    private ArrayList<CHRONCommand> commands = new ArrayList<CHRONCommand>();

    public void execute(){
        for(CHRONCommand c : commands){
            c.execute();
        }
    }
}
