package sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {
    private Map<String, Integer> startCommands = new HashMap<>();
    private Map<String, Integer> endCommands = new HashMap<>();
    private int distance;
    private int speed;

    public Command(int distance, int speed){
        this.distance = distance;
        this.speed = speed;
    }

    public void addStartCommand(String command, int value){
        startCommands.put(command, value);
    }

    public void addEndCommand(String command, int value){
        endCommands.put(command, value);
    }

    public Map getStartCommands(){
        return this.startCommands;
    }

    public Map getEndCommands(){
        return this.endCommands;
    }

    public int getDistance(){
        return this.distance;
    }

    public int getSpeed(){
        return this.speed;
    }
}
