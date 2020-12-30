package client;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ClientGUI extends JFrame{
		
	Client client;
	
	public String[] TraderData = {"test"};
	public JList<String> TraderList = new JList<>(TraderData);
	public JLabel message = new JLabel("<< Message >>");
	public String selectedTrader = "placeholder";
	
	public Boolean hasstock = false;
	
	public void setClient(Client c) {
		this.client = c;
	}
	
	public ClientGUI(Client c){
		this.client = c;
    
        //Creating the Frame
        this.setTitle("Dan Norstrom Dn18657 1807572 Ce303 Assignment");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(610, 300);
    
        
        // p2: TraderList View
        JPanel panel2 = new JPanel();
        panel2.add(TraderList);
        
        // p3: Console Log
        JPanel panel3 = new JPanel();
        panel3.add(message); // Components Added using Flow Layout
        
        // p1: Trade Button
        JPanel panel1 = new JPanel(); 
        JButton send = new JButton("Send Stock (to selected)");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try {   
	            	String TargetTraderID = (TraderList.getSelectedValue()).toString();
	            	
	            	String ans = client.TradeStock(TargetTraderID);
					message.setText("Trade: "+ ans);
					
					//update.notify();
				} catch (InterruptedException e1) {
					//e1.getStackTrace();
					message.setText(e1.getMessage());
				} catch (Exception e1) {
					//e1.getStackTrace();
					message.setText(e1.getMessage());
					
				}	
            }
        });
        panel1.add(send);
           
        // layout
        this.getContentPane().add(BorderLayout.NORTH, panel1);
        this.getContentPane().add(BorderLayout.CENTER, panel2);
        this.getContentPane().add(BorderLayout.SOUTH, panel3);
        this.setVisible(true);
        
        // close related client when closing GUI
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
					client.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        });
	}
	
}



