package Agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.security.SecureRandom;
import Classes.GetTaxi;

public class ClientAgent extends Agent {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
// -------------------------------------------------------------------

	private AID server;
	private int command = -1;
	private int coordX = -1;
	private int coordY = -1;
	private int destinationCoordX = -1;
	private int destinationCoordY = -1;
	static final int GET_TAXI = 1;

	protected void setup() {
		this.coordX = new SecureRandom().nextInt(101);
		this.coordY = new SecureRandom().nextInt(101);
		do {
			this.destinationCoordX = new SecureRandom().nextInt(101);
			this.destinationCoordY = new SecureRandom().nextInt(101);
		} while (this.destinationCoordX == this.coordX && this.destinationCoordY == this.coordY);
		
		SequentialBehaviour sb = new SequentialBehaviour();
		sb.addSubBehaviour(new RequestTaxi(this));
		sb.addSubBehaviour(new HandleReceiveMessages(this));
		addBehaviour(sb);
	}
	
	class HandleReceiveMessages extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 248240233361085715L;
		
		public HandleReceiveMessages(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg == null) {
				block();
				return;
			}
			try {
				switch (msg.getPerformative()) {

				case (ACLMessage.FAILURE):
					System.out.println("Client didn't find any taxis. Trying again soon...");
					int sleepTime = new SecureRandom().nextInt(5001-1000) + 1000; //sleep entre 1000 e 5000 ms inclusive
					Thread.sleep(sleepTime);
					System.out.println("Trying to request taxi again (slept for " + sleepTime + "ms)");
					addBehaviour(new RequestTaxi(myAgent));
					break;
				default:
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

	class RequestTaxi extends OneShotBehaviour {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		// ------------------------------------------------ Get the user command

		RequestTaxi(Agent a) {
			super(a);
			command = GET_TAXI;
		}

		public void action() {

			if (command == GET_TAXI) {
				getTaxi();
			}
		}
	}

	void getTaxi() {
// ----------------------  Process to the server agent the request
//                         to create a new account

		GetTaxi gt = new GetTaxi();
		gt.setCoordinates(this.coordX, this.coordY);
		gt.setDestinationCoordinates(this.destinationCoordX, this.destinationCoordY);
		sendMessage(ACLMessage.REQUEST, gt);
		// espera resposta taxi
	}


//--------------------------- Utility methods ----------------------------//

	void sendMessage(int performative, Object content) {
// ----------------------------------------------------

		server = new AID("ManagerAgent", AID.ISLOCALNAME);
		ACLMessage msg = new ACLMessage(performative);
		try {
			msg.setContentObject((java.io.Serializable) content);
			msg.addReceiver(server);
			System.out.println("Contacting server... Please wait!");
			send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
