import java.util.Map;

public class RobotController {
    PilotTest robot = new PilotTest();


    public RobotController(){
        robot = new PilotTest();
    }

    public void runCommand(Command command){
        runCommand(command.getStartCommands());
        robot.drive(command.getDistance(), command.getSpeed());
        runCommand(command.getEndCommands());
    }

    private void runCommand(Map<String, Integer> commandMap){
        for(Map.Entry<String, Integer> entry : commandMap.entrySet()){
            switch (entry.getKey()){
                case "turn":
                    robot.turn(entry.getValue());
                    break;
                default:

            }
        }
    }
}
