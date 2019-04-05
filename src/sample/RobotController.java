package sample;

import java.util.Map;

public class RobotController {
    PilotTest robot = new PilotTest();


    public RobotController(){
        robot = new PilotTest();
    }

    public void runCommand(Command command){
        Map<String, Integer> startCommands = command.getStartCommands();

    }
}
