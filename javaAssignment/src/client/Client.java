package client;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client implements AutoCloseable {
    final int port = 8888;
    private final Scanner reader;
    private final PrintWriter writer;
    
    // flag to updating client
    public String customerID;

	public Client() throws Exception {
		
		// socket closes from ClientHandler serverside
        Socket socket = new Socket("localhost", port);
        
        reader = new Scanner(socket.getInputStream());

        // Automatically flushes the stream with every command
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    // show all connected traders
    public synchronized List<String> GetTraders() {
        // Sending command
        writer.println("TRADERS");

        // Reading the number of accounts
        String line = reader.nextLine();
        int numberOfTraders = Integer.parseInt(line);

        // Reading the account numbers
        List<String> Tarr = new ArrayList<>();
        for (int i = 0; i < numberOfTraders; i++) {
            Tarr.add(reader.nextLine());
        }

        return Tarr;
    }
    
    // receive a list of stock owners (should only be one at any time)
    public synchronized List<String> GetStockOwner() {
    	// Sending command
    	writer.println("STOCKOWNER");

        // Reading the number of accounts
        String line = reader.nextLine();

        // receive number of stock-owners
        int numberOfStockOwners = Integer.parseInt(line);
        
        // Reading the account numbers
        List<String> Tarr = new ArrayList<>();
        for (int i = 0; i < numberOfStockOwners; i++) {
            Tarr.add(((String) reader.nextLine() ));
        }
        return Tarr;
    }
    
    // initialize clients TraderID from server
    public synchronized String NewClient() {
    	writer.println("NEWCLIENT");
    	
        String TraderID = reader.nextLine();
        
        // set client id
        this.customerID = TraderID;
        return TraderID;
    }
    
    // called when server is closed but client is running, client restarts and reconnects to server
    public synchronized void ReconnectClient(String TraderID, Boolean stock) {
    	this.customerID = TraderID;
    	writer.println("RECONNECTCLIENT "+TraderID+ " " +stock);
    	
    }
    
    // check if current client has stock
    public synchronized Boolean HasStock() {
    	writer.println("HASSTOCK");
    	Boolean ans =  Boolean.parseBoolean(reader.nextLine());
    	return ans;
    }
    
    // Trade stock to other clients if available
    public synchronized String TradeStock(String traderID) throws Exception {
        
    	// check if we are the stock-owner
    	if (HasStock()) {
			writer.println("TRADESTOCK " + traderID + " " + this.customerID);
	        
	        // Reading the response
	        String line = reader.nextLine();
	        return line;
    	}
    	else {
    		return "you dont own stocks";
    	}
    }

    // ensure stuff is closed
    @Override
    public synchronized void close() throws Exception {
        reader.close();
        writer.close();
    }
}
