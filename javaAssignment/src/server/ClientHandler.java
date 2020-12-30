package server;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket socket;
    
    public Trader trader;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    

    @Override
    public void run() {

        try (
    		// create in and output to socket
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            try {
                while (true) {
                	// listen for next command
                    String line = scanner.nextLine();
                    String[] substrings = line.split(" ");
                    switch (substrings[0].toLowerCase()) {
                        case "traders":
                        	
                        	// copy trader to assert array size
                        	List<Trader> Tarr = new ArrayList<Trader>(ServerProgram.Market);

                        	// pass array size to client
                        	int tnr = Tarr.size();
                        	writer.println(tnr);
                        	
                        	// pass trader list to client
                        	for (int i=0; i<tnr; i++) {
                                writer.println( ((Trader)Tarr.get(i)).traderID );
                        	}
                            break;
                        
                        // Return Clients Stock status
                        case "hasstock":
                        	if (this.trader.stock) {
                        		writer.println("true");
                        	}
                        	else {
                        		writer.println("false");
                        	}
                        	break;
                        
                        // Return Stock Owner(s)
                        case "stockowner":
                        	try {
	                        	List<Trader> stockOwnerArr = new ArrayList<>();
	                        	for (Trader t : ServerProgram.Market) {
	        	                	if (t.stock)  stockOwnerArr.add(t);
	        	                }
	                        	
	                        	int size = stockOwnerArr.size();
	                        	writer.println(size);
	                        	
	                        	for(int i=0;i<size;i++) {
	                        		writer.println(((Trader) stockOwnerArr.get(i)).traderID );
	                        	}
                        	}
                        	//Stock not found, return stock to trader
                        	catch (Exception e) {
                        		writer.println("failure! "+e);
                        	}
                        	break;
                        	
                        // Client Trades stock to other trader
                        case "tradestock":
                        	String TargetTraderID = substrings[1];
                        	String SenderTraderID = substrings[2];
                        	// if trade to oneself
                        	if (TargetTraderID.equals(SenderTraderID)) {
                        		writer.println("success! - you traded the stock to yourself!");
                        		String msg = "Self-Trade: "+SenderTraderID+"\n";
                            	System.out.println(msg);
                            	ServerProgram.sgui.message.append(msg);
                        	}
                        	// if trade to other trader
                        	else{
	                        	try {
	                        		// try to find targetTrader to give stocks
	                        		Boolean found = false;
	                        		for (Trader t : ServerProgram.Market) {
	                        			if (t.traderID.equals(TargetTraderID)) {
	                        				t.stock = true;
	                        				found = true;
	                        			}
	                        		}
	                        		if (found) {
	                        			// This trader has given away the stock
	                        			this.trader.stock = false;
	                        			writer.println("success! "+SenderTraderID +" to: "+TargetTraderID);
	                        			String msg1 = "Trade: "+SenderTraderID +" to: "+TargetTraderID+"\n";
	                                	System.out.println(msg1);
	                                	ServerProgram.sgui.message.append(msg1);
	                        		}
	                        		else {
	                        			// targetTrader not found, return stock to trader
	                        			writer.println("failure! NotFound");
	                        			
	                        			String msg1 = "Trade Failed: "+SenderTraderID +" to: "+TargetTraderID+"\n";
	                                	System.out.println(msg1);
	                                	ServerProgram.sgui.message.append(msg1);
	                                	
	                                	String msg2 = "Server Returns Stock to: "+SenderTraderID+"\n";
	                                	System.out.println(msg2);
	                                	ServerProgram.sgui.message.append(msg2);
	                        		}
	                        	}
	                        	
	                        	catch (Exception e) {
	                        		writer.println("failure! "+e);
	                        	}
                        	}
                        	break;
                        	                    
                        	
                        case "newclient":
                        	// generate Unique ID and send it to client so that the client knows its TraderID(=customerID)
                            String customerID = UUID.randomUUID().toString();
                            
                            // send id to client so its aware (GUI implementation)
                            writer.println(customerID);
                            
                            // add new trader to current active Traders list
                            this.trader = new Trader(customerID);
                            
                            // add stock if this is the first trader
                            if ((ServerProgram.Market.size()) <= 0) {
                            	this.trader.stock = true;
                            	String msg = "Trader receives stock from server: "+customerID+"\n";
                            	System.out.println(msg);
                            	ServerProgram.sgui.message.append(msg);
                            }
                            
                            // add trader to market
                            ServerProgram.Market.add(this.trader);
                            String msg1 = "Trader Joins Market: " + customerID+"\n";
                        	System.out.println(msg1);
                        	ServerProgram.sgui.message.append(msg1);
                        	
                        	// print current market state
                        	String msg2 = "Active Traders: \n";
                        	System.out.println(msg2);
                        	ServerProgram.sgui.message.append(msg2);
                        	
                        	for (Trader t : ServerProgram.Market) {
            		        	String msg3 = t.traderID+"\n";
                            	System.out.println(msg3);
                            	ServerProgram.sgui.message.append(msg3);
            		    	}
                        	
                            break;
                            
                        case "reconnectclient":
                        	String TraderID = substrings[1];
                        	Boolean StockStatus = Boolean.parseBoolean(substrings[2]);
                        	this.trader = new Trader(TraderID);
                        	
                        	this.trader.stock = StockStatus;
                        	if (StockStatus) {
                        		String msg = "Trader receives stock from server: "+TraderID+"\n";
                            	System.out.println(msg);
                            	ServerProgram.sgui.message.append(msg);
                        	}
                        	
                        	// add trader to market
                            ServerProgram.Market.add(this.trader);
                            String msg3 = "Trader Joins Market: " + TraderID+"\n";
                        	System.out.println(msg3);
                        	ServerProgram.sgui.message.append(msg3);
                        	
                        	break;
                            
                        case "close":
                        	socket.close();
                        	break;
                        	
                        default:
                            throw new Exception("Unknown command: " + substrings[0]);
                    }
                }
            } // end of code
            catch (Exception e) {
                writer.println("ERROR " + e.getMessage());
                
                // remove trader on exit 
                ServerProgram.Market.remove(this.trader);
                
                
                // Redistribute Stock if stock-owner leaves Market
                if (this.trader.stock) {
	            	int rand = (int) (Math.random() * (ServerProgram.Market.size()-1));
	            	Trader t = ((Trader) ServerProgram.Market.get(rand));
	            	t.stock = true;
	            	String msg2 = "Trader Receives Stock From Server: "+t.traderID+"\n";
	            	System.out.println(msg2);
	            	ServerProgram.sgui.message.append(msg2);
                }
            	
            	// close process
                socket.close();
            }
        } // end of print writer
        catch (Exception e) {
        } finally {
            String msg = "Trader Left Market: " + this.trader.traderID+"\n";
        	System.out.println(msg);
        	ServerProgram.sgui.message.append(msg);
        	
        	// print current market state
        	String msg2 = "Active Traders: \n";
        	System.out.println(msg2);
        	ServerProgram.sgui.message.append(msg2);
        	
        	for (Trader t : ServerProgram.Market) {
	        	String msg3 = t.traderID+"\n";
            	System.out.println(msg3);
            	ServerProgram.sgui.message.append(msg3);
	    	}
        }
    }
}
