package server;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// server startup form client
import server.*;

public class ServerProgram
{
    private final static int port = 8888;
    
    // Thread-safe collection used as Market-state
    public static final List<Trader> Market = 
            Collections.synchronizedList(new ArrayList<Trader>()); 
    
    public static ServerGUI sgui;
    
    public static void main(String[] args)
    {
        RunServer();
    }

    // Listens for new clients and connects them.
    private static void RunServer() {
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(port);
            // initialize server GUI
            sgui = new ServerGUI();
            String msg1 = "Waiting for incoming connections...\n";
        	System.out.println(msg1);
        	sgui.message.append(msg1);
        	
            while (true) {
            	// run new ClientHandler per incoming Trader (Client)
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();   
            }
        } catch (IOException e) {
        	// close server if another server already exists
        	System.exit(0);
            e.printStackTrace();
        }
    }
}
