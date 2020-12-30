package server;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerGUI extends JFrame {

	public String[] TraderData = {"test"};
	public JList<String> TraderList = new JList<>(TraderData);
	public JTextArea message = new JTextArea("Server Console >>\n");
	
	
	public ServerGUI(){
   
        //Creating the Frame
        this.setTitle("Dan Norstrom Dn18657 1807572 Ce303 Assignment");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 600);
    
        
        // p1: Trader view
        JScrollPane panel1 = new JScrollPane(TraderList);
        
        // p2: Console log
        JScrollPane panel2 = new JScrollPane(message);
        message.setEditable(false);
 
        // layout
        this.getContentPane().add(BorderLayout.NORTH, panel1);
        this.getContentPane().add(BorderLayout.CENTER, panel2);
        this.setVisible(true);
        
        // Server-GUI update
        new Thread(new Runnable() {
        	
        	// timer to verify if stock-owner left during a restart
        	long startTime = System.nanoTime();
        	Boolean doOnce = true;
        	@Override
			public void run() {
        		
        		while(true) {
        		
        		// ## Restart (Check for stock) ##
        		if (doOnce) {
	        		long elapsedTime = System.nanoTime() - startTime;
	        		
	        		// 1.5 seconds
	        		if (elapsedTime > 1500000000) {
	        			Boolean hasstock = false;
	        			// is there a stock owner?
	        			for (Trader t: ServerProgram.Market){
	        				if (t.stock) hasstock = true;
	        			}
	        			// Redistribute Stock if stock-owner leaves Market	
	        			if (!hasstock & (ServerProgram.Market.size() > 0)) {
	    	            	int rand = (int) (Math.random() * (ServerProgram.Market.size()-1));
	    	            	Trader t = ((Trader) ServerProgram.Market.get(rand));
	    	            	t.stock = true;
	    	            	String msg2 = "Trader Receives Stock From Server: "+t.traderID+"\n";
	    	            	System.out.println(msg2);
	    	            	ServerProgram.sgui.message.append(msg2);
	    	            	
	    	            	doOnce = false;
	                        }
	        			}
	        		}
        		
		        // ## Update Server GUI ##
		        
		        // Update Trader List
		        List<String> Traders = new ArrayList<>();
		        for (Trader t : ServerProgram.Market) {
		        	Traders.add(t.traderID);
		    	}
		        TraderData = Traders.toArray(new String[0]);
		        TraderList.setListData(TraderData);
		        
		        // Set current StockOwner color
		        List<String> StockOwners = new ArrayList<>();
		    	for (Trader t : ServerProgram.Market) {
		        	if (t.stock)  StockOwners.add(t.traderID);
		    	}
		        
		    	// set Jlist colors
		        TraderList.setCellRenderer(new DefaultListCellRenderer() {
				      public Component getListCellRendererComponent(JList list, Object value,
				          int index, boolean isSelected, boolean cellHasFocus) {
				        super.getListCellRendererComponent(list, value, index, isSelected,
				            cellHasFocus);
				        String target = (String) value;
				        
				        for (String s : StockOwners) {
				        	// set Active Trader->stockOwner Green
				        	if (target.equals(s)) {
					        	setBackground(new Color(0,255,51)); 	
					        }
				        }
				        return this;
				      }
				});
		        
		        // update every x/1000 seconds
				try {Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
        	}
        }}).start();	
	}	
}
