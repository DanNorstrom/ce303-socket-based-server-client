package server;
/*Creator: Dan Norstrom, dn18657, 1807572, NORST42202
Course: CE303 Advanced Programming
Related to: Assignment
Date: 2020-11-28 */

public class Trader {

	public String traderID;
	public Boolean stock = false;
	
	public Trader(String ID) {
		this.traderID = ID;
	}
	
	public void SetStock(Boolean q) {
		this.stock = q;
	}
}
