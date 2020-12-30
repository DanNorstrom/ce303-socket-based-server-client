/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

using System;
using System.IO;
using System.Net.Sockets;
using System.Collections.Generic;

namespace CClient
{
    class ClientProgram
    {
        static void Main(string[] args) {

            try {
                using (Client client = new Client()) {
                    Console.WriteLine("Logged in successfully.\n");

                    // initialize client
                    String customerID = client.NewClient();
                    Console.WriteLine("Welcome: "+customerID+"\n");
                    
                    // option messages (non-GUI)
                    Console.WriteLine("these are your options:\n");
                    Console.WriteLine("Traders: shows active traders");
                    Console.WriteLine("StockOwner: shows the stockowner");
                    Console.WriteLine("HasStock: Shows stock status of current trader");
                    Console.WriteLine("TradeStock <id>: Tradestock");
                    Console.WriteLine("Close: Ends session\n");

                    // program loop
                    while (true) {
                	
                	String line = Console.ReadLine();
                	
                    String[] substrings = line.Split(" ");
                    switch (substrings[0].ToLower()) {
                        case "traders":
	                	Console.WriteLine("Available traders: \n");
	        
	                	// get trader list
	                	List<String> Target = client.GetTraders();
	                	
	                	// print trader list with index
	                	Console.WriteLine("Currently active traders:");
	                	int i = 0;
	                	foreach(String s in Target) {
	                		Console.WriteLine("Trader "+i +": "+s);
	                		i++;
	                	}
	                	break;
	                	
                        case "tradestock":
                        	String TargetTraderID = substrings[1];
                        	Console.WriteLine("Trade:"+client.TradeStock(TargetTraderID));
                        	break;
                        	
                        case "hasstock":
                        	Boolean ans = client.HasStock();
                        	client.hasStock = ans;
                        	Console.WriteLine("stock status: "+ans);
                        	break;
                        
                        case "stockowner":
                        	foreach(String s in client.GetStockOwner()) {
                        		Console.WriteLine("Owner: "+s);
                        	}
                        	break;
               	
                        case "close":
                        	client.Dispose();
                        	throw new Exception("close app"); // do we need this?
                        	
	                	
                        default:
                        	Console.WriteLine("Unknown command: " + substrings[0]);
                            break;
                        }

                }

                }    
            }        
            catch (Exception e) {
                Console.WriteLine(e.Message);
            }
        }
    }
}