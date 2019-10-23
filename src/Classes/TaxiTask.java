package Classes;

import jade.core.AID;

public class TaxiTask implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1522248641629420053L;
	private int clientCoordX;
	private int clientCoordY;
	private int destinationX;
	private int destinationY;
	private AID client;

	public TaxiTask(int x, int y, int destX, int destY, AID clientAID) {
		this.clientCoordX = x;
		this.clientCoordY = y;
		this.destinationX = destX;
		this.destinationY = destY;
		this.client = clientAID;
	}

	public int getClientCoordX() {
		return clientCoordX;
	}

	public void setClientCoordX(int clientCoordX) {
		this.clientCoordX = clientCoordX;
	}

	public int getClientCoordY() {
		return clientCoordY;
	}

	public void setClientCoordY(int clientCoordY) {
		this.clientCoordY = clientCoordY;
	}

	public int getDestinationX() {
		return destinationX;
	}

	public void setDestinationX(int destinationX) {
		this.destinationX = destinationX;
	}

	public int getDestinationY() {
		return destinationY;
	}

	public void setDestinationY(int destinationY) {
		this.destinationY = destinationY;
	}

	public AID getClient() {
		return client;
	}

	public void setClient(AID client) {
		this.client = client;
	}
}
