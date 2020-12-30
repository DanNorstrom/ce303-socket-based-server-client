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
    class Client : IDisposable
    {
        const int port = 8888;

        private readonly StreamReader reader;
        private readonly StreamWriter writer;

        public String customerID = null;

        // flag for updating client
        public Boolean hasStock = false; // was null in java

        public Client() {
            // Connecting to the server and creating objects for communications
            TcpClient tcpClient = new TcpClient("localhost", port);
            NetworkStream stream = tcpClient.GetStream();
            reader = new StreamReader(stream);
            writer = new StreamWriter(stream);
            writer.AutoFlush = true;	// no need to "writer.Flush();"
        }

        public List<String> GetTraders() {
            // Sending command
            writer.WriteLine("TRADERS");

            // Reading the number of accounts
            String line = reader.ReadLine();
            int numberOfTraders = int.Parse(line);

            // Reading the account numbers
            List<String> Target = new List<String>();
            for (int i = 0; i < numberOfTraders; i++) {
                Target.Add(reader.ReadLine());
            }

            return Target;
        }


        public List<String> GetStockOwner() {
            // Sending command
            writer.WriteLine("STOCKOWNER");

            // Reading the number of accounts
            String line = reader.ReadLine();

            int numberOfStockOwners = int.Parse(line);
            
            // Reading the account numbers
            List<String> Target = new List<String>();
            for (int i = 0; i < numberOfStockOwners; i++) {
                Target.Add(((String) reader.ReadLine() ));
            }
            return Target;
        }

        public String NewClient() {
            writer.WriteLine("NEWCLIENT");
            String welcomeLine = reader.ReadLine();
            
            // set client id
            this.customerID = welcomeLine;
            return welcomeLine;
        }

        public Boolean HasStock() {
            writer.WriteLine("HASSTOCK");
            Boolean ans =  Boolean.Parse(reader.ReadLine()); // bool.Parse() ?
            return ans;
        }


        public String TradeStock(String traderID) {
        
            // check if we are the stock-owner
            if (HasStock()) {
                writer.WriteLine("TRADESTOCK " + traderID + " " + this.customerID);
                
                // Reading the response
                String line = reader.ReadLine();
                
                return line;
            }
            else {
                return "you dont own stocks";
            }
        }


        public void Dispose() { // was close() in java
            reader.Close();
            writer.Close();
        }

        
    }
}
