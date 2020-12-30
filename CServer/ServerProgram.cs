/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Collections.Generic;
using System.Collections;

namespace CServer
{
    class ServerProgram
    {
        private const int port = 8888;

        // not thread safe yet, we need locks in C3 compared to synchronized in Java
        private static List<Trader> Market =  new List<Trader>();

        static void Main(string[] args)
        {
            RunServer();
        }


        private static void RunServer() {
            TcpListener listener = new TcpListener(IPAddress.Loopback, port);

            try {
                listener.Start();
                Console.WriteLine("Waiting for incoming connections...");
                while (true) {
                    // run new client per incoming trader
                    TcpClient tcpClient = listener.AcceptTcpClient();
                    new Thread(HandleIncomingConnection).Start(tcpClient);    
                }
            } catch (IOException e) {
                Console.WriteLine( e.Message);
            }
        }


        private static void HandleIncomingConnection(object param) {

            Trader trader = null; // this threads Trader 

            // create in and output to socket
            TcpClient tcpClient = (TcpClient) param;
            using (Stream stream = tcpClient.GetStream()) {
                StreamWriter writer = new StreamWriter(stream);
                writer.AutoFlush = true;
                StreamReader reader = new StreamReader(stream); 

                    
                try {
                    while (true) {
                        String line = reader.ReadLine();
                        String[] substrings = line.Split(" ");
                        switch (substrings[0].ToLower()) {
                            case "traders":
                                // copy trader to assert array size
                                List<Trader> Target = new List<Trader>(Market);

                                // pass array size to client
                                int tnr = Target.Count;
                                writer.WriteLine(tnr);
                                
                                // pass trader list to client
                                for (int i=0; i<tnr; i++) {
                                    writer.WriteLine( ((Trader)Target[i]).traderID );
                                }
                                break;
                    
                            case "hasstock":
                                if (trader.stock) {
                                    writer.WriteLine("true");
                                }
                                else {
                                    writer.WriteLine("false");
                                }
                                break;
                            
                                
                            // server-side find stock-owners	
                            case "stockowner":
                                try {
                                    List<Trader> stockOwnerArr = new List<Trader>();
                                    foreach(Trader t in Market) {
                                        if (t.stock)  stockOwnerArr.Add(t);
                                    }
                                    
                                    int size = stockOwnerArr.Count;
                                    writer.WriteLine(size);
                                    
                                    for(int i=0;i<size;i++) {
                                        writer.WriteLine(((Trader) stockOwnerArr[i]).traderID );
                                    }
                                }
                                //Stock not found, return stock to trader
                                catch (Exception e) {
                                    writer.WriteLine("failure! "+e);
                                }
                                break;
                                
                                
                            case "tradestock":
                                String TargetTraderID = substrings[1];
                                String SenderTraderID = substrings[2];
                                // if trade to oneself
                                if (TargetTraderID.Equals(SenderTraderID)) {
                                    writer.WriteLine("success! - you traded the stock to yourself!");
                                    Console.WriteLine("Self Trade: "+TargetTraderID);  
                                }
                                // if trade to other trader
                                else{
                                    try {
                                        // try to find targetTrader to give stocks
                                        Boolean found = false;
                                        foreach (Trader t in Market) {
                                            if (t.traderID.Equals(TargetTraderID)) {
                                                t.stock = true;
                                                found = true;
                                                // send success to trader
                                                
                                            }
                                        }
                                        if (found) {
                                            // This trader has given away the stock
                                            trader.stock = false;
                                            writer.WriteLine("success!");
                                            // report success in CMD
                                            Console.WriteLine("Trade Successful: "+SenderTraderID +" To "+TargetTraderID);
                                        }
                                        else {
                                            writer.WriteLine("failure! NotFound");
                                            // report failure in CMD
                                            Console.WriteLine("Trade Failed - Trader not Found: "+SenderTraderID +" To "+TargetTraderID);
                                        }
                                    }
                                    catch (Exception e) {
                                        writer.WriteLine("failure! "+e);
                                        Console.WriteLine("Trade Error: "+SenderTraderID +" To "+TargetTraderID);
                                    }
                                }
                                break;
                                                    
                                
                            case "newclient":
                                // generate Unique ID and send it to client so that the client knows its
                                String customerID = (Guid.NewGuid()).ToString();
                                
                                // send id to client so its aware (GUI implementation)
                                writer.WriteLine(customerID);
                                
                                // add new trader to current active Traders list
                                trader = new Trader(customerID);
                                
                                // add stock if this is the first trader
                                if (Market.Count <= 0) {
                                    trader.stock = true;
                                    Console.WriteLine("Trader Receives Stock From Server: "+trader.traderID);
                                }
                                
                                // add trader to market
                                ServerProgram.Market.Add(trader);
                                Console.WriteLine("Trader Joined Market: " + customerID);

                                // show market state
                                Console.WriteLine("Current Market State:");
                                foreach(Trader t in Market){
                                    Console.WriteLine(t.traderID);
                                }
                                break;
                                
                                
                            case "close":
                                tcpClient.Close();
                                break;
                                
                            default:
                                throw new Exception("Unknown command: " + substrings[0]);
                        }
                    }
                } // end of code
                catch (Exception e) {
                    try {
                        writer.WriteLine("ERROR " + e.Message);
                        tcpClient.Close();
                    }
                    catch {
                        Console.WriteLine("Client Forcefully Closed From Client-Side");
                    }
                }
                finally {
                ServerProgram.Market.Remove(trader);
                Console.WriteLine("Trader Left Market: " + trader.traderID);

                // If stock-owner leaves, distribute stock to a random trader
                if ((trader.stock) & (Market.Count >0)) {

                    Random rnd = new Random();
                    int rand = rnd.Next(0,Market.Count-1);

                    Trader target = ((Trader) Market[rand]);
                    target.stock = true;

                    Console.WriteLine("Trader Receives Stock From Server: "+target.traderID);
                }
                
                // show market state
                Console.WriteLine("Current Market State:");
                foreach(Trader t in Market){
                    Console.WriteLine(t.traderID);
                }
                }
            }
        }
    }   
}

