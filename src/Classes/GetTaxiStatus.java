package Classes;

import jade.core.AID;

public class GetTaxiStatus implements java.io.Serializable {

	private AID client;
	private int clientCoordX;
	private int clientCoordY;
	private int clientDestCoordX;
	private int clientDestCoordY;

	public GetTaxiStatus(AID client, int clientCoordX, int clientCoordY, int clDX, int clDY) {
		this.client = client;
		this.clientCoordX = clientCoordX;
		this.clientCoordY = clientCoordY;
		this.clientDestCoordX = clDX;
		this.clientDestCoordY = clDY;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836999945075039947L;

	public AID getClient() {
		return client;
	}

	public void setClient(AID client) {
		this.client = client;
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

	public int getClientDestCoordX() {
		return clientDestCoordX;
	}

	public void setClientDestCoordX(int clientDestCoordX) {
		this.clientDestCoordX = clientDestCoordX;
	}

	public int getClientDestCoordY() {
		return clientDestCoordY;
	}

	public void setClientDestCoordY(int clientDestCoordY) {
		this.clientDestCoordY = clientDestCoordY;
	}
	
	
}
