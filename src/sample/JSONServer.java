/*
Author: Isaiah Zupke
Date: 04/05/2019
 */

package sample;

import org.json.JSONArray; //Not working? It comes from the jar
import org.json.JSONObject;// not working? it comes from the jar
//import the jar into eclipse. idk how but just do it
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 */
public class JSONServer {


    //everything was stolen from: https://www.codevoila.com/post/65/java-json-tutorial-and-example-json-java-orgjson
    //https://stackoverflow.com/questions/33497118/how-to-send-jsonobject-over-tcp
    //for testing, you need to setup a tcp client, connect, and send the JSON found in
    //the JIRA board "Translate input trip id into a list of waypoints"
    //https://msoese.atlassian.net/secure/RapidBoard.jspa?rapidView=363&projectKey=SE2800JCM5&modal=detail&selectedIssue=SE2800JCM5-22
    //theres the link for reference.

    //last thing that is needed is for me to actually issue these commands to a robot. I am going to
    //make a robot class, and I will just have it accept a string and int (command and value respectively)
    //and that will control the objects commands. Additionally, I am going to make a robot controller class which will
    //be the main class that starts the whole process.

    private ServerSocket serverSocket;
    private int port;
    public static int clients = 0;


    public void establish(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        System.out.println("JSONServer has been established on port " + port);

    }


    public void accept() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            Runnable r = new MyThreadHandler(socket);
            Thread t = new Thread(r);
            t.start();
            try{
                t.wait();
                List<Command> commands = ((MyThreadHandler) r).getCommands();

                for (Command command: commands) {

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyThreadHandler implements Runnable {
        private Socket socket;
        private List<Command> commands;

        MyThreadHandler(Socket socket) {
            this.socket = socket;
        }

        public List<Command> getCommands(){
            return this.commands;
        }

        @Override
        public void run() {
            clients++;
            System.out.println(clients + " JSONClient(s) connected on port: " + socket.getPort());
            commands = new ArrayList<>();
            try {
                // For JSON Protocol
                JSONObject jsonObject = receiveJSON();
                JSONArray commandArray = jsonObject.getJSONArray("commands"); //gets us in the object array

                for(int i=0;i<commandArray.length();i++){ //for every command in the json array
                    JSONObject _command = (JSONObject) commandArray.get(i);
                    Command command = new Command(_command.getInt("distance"), _command.getInt("speed"));
                    getStartCommands(command, _command.getJSONArray("start_command"));
                    getEndCommands(command, _command.getJSONArray("end_command"));
                    commands.add(command);
                }

                notify();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public Map getStartCommands(Command command, JSONArray _command){
            Map<String, Object> commandList = null;
            for(int i=0;i<_command.length(); i++){
                Map<String, Object> tempMap = ((JSONObject)_command.get(i)).toMap(); //right now i am trying to add each command
                commandList = tempMap;
                for(Map.Entry<String, Object> entry : tempMap.entrySet()){
                    command.addStartCommand(entry.getKey(), (Integer) entry.getValue());
                }
            }
            return commandList;
        }

        public Map getEndCommands(Command command, JSONArray _command){
            Map<String, Object> commandList = null;
            for(int i=0;i<_command.length(); i++){
                Map<String, Object> tempMap = ((JSONObject)_command.get(i)).toMap(); //right now i am trying to add each command
                commandList = tempMap;
                for(Map.Entry<String, Object> entry : tempMap.entrySet()){ //iterates through the map (end commands)
                    command.addEndCommand(entry.getKey(), (Integer) entry.getValue());
                }
            }
            return commandList;
        }



        public void closeSocket() throws IOException {
            socket.close();
        }


        /**
         * use the JSON Protocol to receive a json object as
         * String from the client and reconstructs that object
         * @return JSONObejct with the same state (data) as
         * the JSONObject the client sent as a String msg.
         * @throws IOException
         */
        public JSONObject receiveJSON() throws IOException {
            InputStream in = socket.getInputStream();
            ObjectInputStream i = new ObjectInputStream(in);
            JSONObject line = null;
            try {
                line = (JSONObject) i.readObject();

                System.out.println(line);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            JSONObject jsonObject = new JSONObject(line);
            //System.out.println("Got from client on port " + socket.getPort() + " " + jsonObject.get("key").toString());
            return jsonObject;
        }
    }

    public void start(int port) throws IOException{
        establish(port);
        accept();
    }

    public static void main(String[] args) {
        JSONServer server = new JSONServer();

        try {
            server.start(42069);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(e);
        }
    }
}