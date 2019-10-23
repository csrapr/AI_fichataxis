package Classes;

public class GetTaxi implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8209762070704487611L;
	private int coordX, coordY, destinationCoordX, destinationCoordY;
	
	public GetTaxi(int cX, int cY, int dcX, int dcY) {
		this.coordX = cX;
		this.coordY = cY;
		this.destinationCoordX = dcX;
		this.destinationCoordY = dcY;
	}
	
	public GetTaxi() {
		this.coordX = -1;
		this.coordY = -1;
	}

	public int getCoordX() {
		return coordX;
	}

	public int getCoordY() {
		return coordY;
	}
	
	
	public int getDestinationCoordX() {
		return destinationCoordX;
	}

	public void setDestinationCoordX(int destinationCoordX) {
		this.destinationCoordX = destinationCoordX;
	}

	public int getDestinationCoordY() {
		return destinationCoordY;
	}

	public void setDestinationCoordY(int destinationCoordY) {
		this.destinationCoordY = destinationCoordY;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

	public void setCoordinates(int x, int y) {
		this.coordX = x;
		this.coordY = y;
	}
	
	public void setDestinationCoordinates(int x, int y) {
		this.destinationCoordX = x;
		this.destinationCoordY = y;
	}
	
	
}
