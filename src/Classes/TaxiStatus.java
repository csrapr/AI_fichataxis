package Classes;

import jade.core.AID;

public class TaxiStatus implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2985945691137909760L;

	private boolean available;
	private int coordX;
	private int coordY;
	private AID taxiName;
	private AID proposedClient;
	private int proposedClientCoordX;
	private int proposedClientCoordY;
	private int proposedClientDestCoordX;
	private int proposedClientDestCoordY;

	public TaxiStatus() {
		this.available = false;
		this.coordX = -1;
		this.coordY = -1;
	}

	public TaxiStatus(boolean available, int x, int y, AID taxiName, AID proposedClient, int propCX, int propCY,
			int proposedClientDestCoordX, int proposedClientDestCoordY) {
		this.available = available;
		this.coordX = x;
		this.coordY = y;
		this.taxiName = taxiName;
		this.proposedClient = proposedClient;
		this.proposedClientCoordX = propCX;
		this.proposedClientCoordY = propCY;
		this.proposedClientDestCoordX = proposedClientDestCoordX;
		this.proposedClientDestCoordY = proposedClientDestCoordY;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getCoordX() {
		return coordX;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

	public AID getTaxiName() {
		return taxiName;
	}

	public void setTaxiName(AID taxiName) {
		this.taxiName = taxiName;
	}

	public AID getProposedClient() {
		return proposedClient;
	}

	public void setProposedClient(AID proposedClient) {
		this.proposedClient = proposedClient;
	}

	public int getProposedClientCoordX() {
		return proposedClientCoordX;
	}

	public void setProposedClientCoordX(int proposedClientCoordX) {
		this.proposedClientCoordX = proposedClientCoordX;
	}

	public int getProposedClientCoordY() {
		return proposedClientCoordY;
	}

	public void setProposedClientCoordY(int proposedClientCoordY) {
		this.proposedClientCoordY = proposedClientCoordY;
	}

	public int getProposedClientDestCoordX() {
		return proposedClientDestCoordX;
	}

	public void setProposedClientDestCoordX(int proposedClientDestCoordX) {
		this.proposedClientDestCoordX = proposedClientDestCoordX;
	}

	public int getProposedClientDestCoordY() {
		return proposedClientDestCoordY;
	}

	public void setProposedClientDestCoordY(int proposedClientDestCoordY) {
		this.proposedClientDestCoordY = proposedClientDestCoordY;
	}
}
