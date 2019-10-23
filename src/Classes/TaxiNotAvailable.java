package Classes;

import jade.core.AID;

public class TaxiNotAvailable implements java.io.Serializable {

	private AID client;
	public TaxiNotAvailable(AID client) {
		this.client = client;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723967321848209958L;
	public AID getClient() {
		return client;
	}

	public void setClient(AID client) {
		this.client = client;
	}
	 
	
}
