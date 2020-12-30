package client;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


public class ClientProgram {
	
	private static ClientGUI cgui;
	private static String TraderID;
	
    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            
            // shows if its a fresh connection or connection after a forced server-restart
            Boolean first = true;
            while (true) {
            	Connect(first);
            	first = false;
            }
            
          //scanner or connection  issues    
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
	}
    
    private static void Connect(Boolean first) {
    	try (Client client = new Client()) {

    		// ## Handles First connection / restarted connections ##
    		if (first) {
	        	// initialize client
	        	TraderID = client.NewClient();
	        	
	        	// Client GUI
	        	cgui = new ClientGUI(client);
    		}
    		else {
    			// reconnect with same id
    			client.ReconnectClient(TraderID, cgui.hasstock);
    			
    			// Client GUI
	        	cgui.setClient(client);
    		}
            
            
            // option messages (CMD only, Deprecated for Java)
        	System.out.println("Logged in successfully.\n");
        	System.out.println("Welcome: "+TraderID+"\n");
        	System.out.println("these are your options:\n");
        	System.out.println("TradeList: shows active traders");
        	System.out.println("StockOwner: shows the stockowner");
        	System.out.println("HasStock: Shows stock status of current trader");
        	System.out.println("TradeStock <id>: Tradestock");
        	System.out.println("Close: Ends session\n");

        	
        	// ## Update GUI ##
			while(true) {
				
				// find out stock-status ( to reconnecting clients after server-restarts)
				cgui.hasstock = client.HasStock();
				
				// Trader List update     					
				List<String> Tarr = client.GetTraders();
				
				// Get old list
				List<String> Current = new ArrayList<>();
				for(int i = 0; i< cgui.TraderList.getModel().getSize();i++){
				    Current.add(cgui.TraderList.getModel().getElementAt(i));
				    
				}
				
				// Compare Old and New list -> update list
				if (!Tarr.equals(Current)) {
					System.out.println("updating Trader list");
					cgui.TraderData = Tarr.toArray(new String[0]);
					cgui.TraderList.setListData(cgui.TraderData);	
					
				}   	
				
				// StockOwner update
				List<String> StockOwners = client.GetStockOwner();
				
				// List color update
				cgui.TraderList.setCellRenderer(new DefaultListCellRenderer() {
				      public Component getListCellRendererComponent(JList list, Object value,
				          int index, boolean isSelected, boolean cellHasFocus) {
				        super.getListCellRendererComponent(list, value, index, isSelected,
				            cellHasFocus);
				        String target = (String) value;
				        
				        for (String s : StockOwners) {
				        	// set Current CLient is StockOwner GREEN
				        	if(target.equals(s) & target.equals(TraderID)) {
				        		setBackground(new Color(0,255,51));
				        	}
				        	// set stockOwner RED
				        	else if (target.equals(s)) {
					        	setBackground(new Color(255,102,102)); 	
					        }
					        // set current client BLUE
					        else if(target.equals(TraderID)) {
					        	setBackground(new Color(51,204,255));
					        }
				        }
				        return this;
				      }
				});
				
				// update every x/1000 seconds
				Thread.sleep(1000);
			}
        // body issue
        } catch (Exception e) {
        	cgui.message.setText("Restarting and reconnecting to Server...");
            System.out.println(e.getMessage());
            System.out.println("Waiting for server...");
                       
            // restart server if server disconnects
            String command[] = {"java.exe","-cp","bin","server.ServerProgram"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            try {
				Process process = processBuilder.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            // Time delay to allow server to start.
            try {
				Thread.sleep(1000);
				cgui.message.setText("Reconnected to Server...");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
}
// ### DEPRACETED CMD VERSION -> NOW USING GUI ###    
//        				
//                while (true) {
//                	
//                	String line = in.nextLine();
//                	
//                    String[] substrings = line.split(" ");
//                    switch (substrings[0].toLowerCase()) {
//                        case "tradelist":
//                        	
//	                	System.out.println("Availible traders: \n");
//	        
//	                	// get trader list
//	                	List<String> Tarr = client.GetTraders();
//	                	
//	                	// print trader list with index
//	                	System.out.println("Currently active traders:");
//	                	int i = 0;
//	                	for(String s : Tarr) {
//	                		System.out.println("Trader "+i +": "+s);
//	                		i++;
//	                	}
//	                	break;
//	                	
//                        case "tradestock":
//                        	String TargetTraderID = substrings[1];
//                        	System.out.println("Trade:"+client.TradeStock(TargetTraderID));
//                        	break;
//                        	
//                        case "hasstock":
//                        	Boolean ans = client.HasStock();
//                        	client.hasStock = ans;
//                        	System.out.println("stock status: "+ans);
//                        	break;
//                        
//                        case "stockowner":
//                        	for(String s : client.GetStockOwner()) {
//                        		System.out.println("Owner: "+s);
//                        	}
//                        	break;
//               	
//                        case "close":
//                        	//dont forget to close GUI!
//                        	clientGUI.dispose();
//                        	throw new Exception("close app");
//                        	
//	                	
//                        default:
//                        	System.out.println("Unknown command: " + substrings[0]);
//                    }
//
//                }
                
                
                

