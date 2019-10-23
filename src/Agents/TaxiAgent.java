package Agents;

import java.io.*;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import Classes.BankVocabulary;
import Classes.GetTaxiStatus;
import Classes.TaxiNotAvailable;
import Classes.TaxiStatus;
import Classes.TaxiTask;

import java.security.SecureRandom;

public class TaxiAgent extends Agent implements BankVocabulary {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private boolean available = false;
	private int coordX = -1;
	private int coordY = -1;
	private String thisAgentName;
// -------------------------------------------------------------------

	protected void setup() {
		this.coordX = new SecureRandom().nextInt(101);
		this.coordY = new SecureRandom().nextInt(101);
		this.thisAgentName = this.getLocalName();
		SequentialBehaviour sb = new SequentialBehaviour();
		sb.addSubBehaviour(new RegisterInDF(this));
		sb.addSubBehaviour(new ReceiveManagerRequests(this));
		this.available = true;
		addBehaviour(sb);
		// addBehaviour(new ReceiveManagerRequests(this));
	}

	class RegisterInDF extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -384739035322726143L;

		public RegisterInDF(Agent a) {
			super(a);
		}

		public void action() {

			ServiceDescription sd = new ServiceDescription();
			sd.setType("TAXI_AGENT");
			sd.setName(getName());
			sd.setOwnership("CA79014");
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			dfd.addServices(sd);
			try {
				DFAgentDescription[] dfds = DFService.search(myAgent, dfd);
				if (dfds.length > 0) {
					DFService.deregister(myAgent, dfd);
				}
				DFService.register(myAgent, dfd);
				System.out.println(getLocalName() + " is ready.");
			} catch (Exception ex) {
				System.out.println("Failed registering with DF! Shutting down...");
				ex.printStackTrace();
				doDelete();
			}
		}
	}

	class ReceiveManagerRequests extends CyclicBehaviour {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		// ------------------------------------------------ Get the user command

		ReceiveManagerRequests(Agent a) {
			super(a);
		}

		public void action() {

			ACLMessage msg = receive();
			if (msg == null) {
				block();
				return;
			}
			try {
				Object content = msg.getContentObject();

				switch (msg.getPerformative()) {

				case (ACLMessage.REQUEST):

					//System.out.println("Taxi received request from " + msg.getSender().getLocalName());

					if (content instanceof GetTaxiStatus) {
						// manager envia pedido de status genérico para todos os agentes taxi <---- ?
						// recebe pedido de status
						// envia coordenadas, se pode receber novo cliente, e o seu próprio local name,
						// entre outras coisas
						// (tudo no mesmo objecto)
						// o manager por sua vez escolhe o melhor e envia pedido específico para o taxi
						// através do local name do taxi
						//System.out.println("Taxi received msg from manager");
						addBehaviour(new HandleGetTaxiStatus(myAgent, msg));
					}
					break;
				case (ACLMessage.PROPOSE):
					TaxiTask order = (TaxiTask) content;
					if (available) {
						available = false;
						addBehaviour(new HandleTravelToClientAndDestination(myAgent, 1000, order));
					}
					else {
						addBehaviour(new HandleTaxiIsNotAvailable(myAgent, order));
					}
					break;

				default:
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	class HandleTaxiIsNotAvailable extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2883015406344886448L;
		private TaxiTask order;
		public HandleTaxiIsNotAvailable(Agent a, TaxiTask order) {
			super(a);
			this.order = order;
		}
		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
			msg.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
			TaxiNotAvailable tna = new TaxiNotAvailable(order.getClient());
			try {
				msg.setContentObject(tna);
				send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	class HandleTravelToClientAndDestination extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8001686170414443748L;
		// private Agent thisTaxi;
		private TaxiTask order;
		private boolean travelingToClient;
		private boolean transportingClient;

		public HandleTravelToClientAndDestination(Agent a, int period, TaxiTask order) {
			super(a, period);
			// torna taxi indisponivel enquanto estiver nesta tarefa
			//a titulo de experimental tentei meter o available = false antes de entrar no behaviour
			//available = false;
			this.order = order;
			this.travelingToClient = true;
			this.transportingClient = false;
			System.out.println("Taxi " + thisAgentName + " assigned new client at coordinates: "
					+ order.getClientCoordX() + "x, " + order.getClientCoordY() + "y");
		}

		@Override
		protected void onTick() {
			if (this.travelingToClient) {
				System.out.println("Taxi " + thisAgentName + "  moving to client. Current coords: " + coordX + "x, "
						+ coordY + "y");
				if (coordX < order.getClientCoordX()) {
					coordX++;
				} else if (coordX > order.getClientCoordX()) {
					coordX--;
				}
				if (coordY < order.getClientCoordY()) {
					coordY++;
				} else if (coordY > order.getClientCoordY()) {
					coordY--;
				}

				if (coordX == order.getClientCoordX() && coordY == order.getClientCoordY()) {
					this.travelingToClient = false;
					this.transportingClient = true;
					System.out.println("Taxi " + thisAgentName + "   arrived at client coordinates: " + coordX + "x, "
							+ coordY + "y");
					System.out.println("Client desired destination coordinates: " + order.getDestinationX() + "x, "
							+ order.getDestinationY());
					// stop();
				}
			} else if (this.transportingClient) {
				System.out.println("Taxi " + thisAgentName + "   transporting client " + order.getClient().getLocalName() + " to destination. Current coords: "
						+ coordX + "x, " + coordY + "y");
				if (coordX < order.getDestinationX()) {
					coordX++;
				} else if (coordX > order.getDestinationX()) {
					coordX--;
				}
				if (coordY < order.getDestinationY()) {
					coordY++;
				} else if (coordY > order.getDestinationY()) {
					coordY--;
				}

				if (coordX == order.getDestinationX() && coordY == order.getDestinationY()) {
					this.travelingToClient = false;
					this.transportingClient = false;
					System.out.println("Taxi arrived at destination: " + coordX + "x, " + coordY + "y");
					available = true;
					stop();
				}
			}
		}

	}

	class HandleGetTaxiStatus extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8129888221390366812L;
		private ACLMessage request;
		private AID proposedClient;
		private int proposedClientCoordX;
		private int proposedClientCoordY;
		private int proposedClientDestCoordX;
		private int proposedClientDestCoordY;

		public HandleGetTaxiStatus(Agent a, ACLMessage request) {

			super(a);
			this.request = request;
			try {
				this.proposedClient = ((GetTaxiStatus) request.getContentObject()).getClient();
				this.proposedClientCoordX = ((GetTaxiStatus) request.getContentObject()).getClientCoordX();
				this.proposedClientCoordY = ((GetTaxiStatus) request.getContentObject()).getClientCoordY();
				this.proposedClientDestCoordX = ((GetTaxiStatus) request.getContentObject()).getClientDestCoordX();
				this.proposedClientDestCoordY = ((GetTaxiStatus) request.getContentObject()).getClientDestCoordY();

			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void action() {
			TaxiStatus taxiStatus = new TaxiStatus(available, coordX, coordY, myAgent.getAID(), proposedClient,
					proposedClientCoordX, proposedClientCoordY, proposedClientDestCoordX, proposedClientDestCoordY);
			ACLMessage reply = request.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			try {
				reply.setContentObject((java.io.Serializable) taxiStatus);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(reply);
		}
	}

//--------------------------- Utility methods ----------------------------//

}
