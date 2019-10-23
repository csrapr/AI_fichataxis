package Agents;

import java.io.*;
import java.util.ArrayList;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import Classes.BankVocabulary;
import Classes.GetTaxi;
import Classes.GetTaxiStatus;
import Classes.TaxiNotAvailable;
import Classes.TaxiStatus;
import Classes.TaxiTask;
import Classes.TryAgain;

public class ManagerAgent extends Agent implements BankVocabulary {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
// -------------------------------------------------------------------

	protected void setup() {

		addBehaviour(new ReceiveClientRequests(this));
	}

	class ReceiveClientRequests extends CyclicBehaviour {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;
		private ArrayList<ACLMessage> statuses = new ArrayList<>();
		private int registeredTaxis;

		// ------------------------------------------------ Get the user command

		ReceiveClientRequests(Agent a) {
			super(a);

			ServiceDescription sd = new ServiceDescription();
			sd.setType("TAXI_AGENT");
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.addServices(sd);
			try {
				registeredTaxis = DFService.search(myAgent, dfd).length;
			} catch (FIPAException e) {
				e.printStackTrace();
			}
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

					System.out.println("Request from " + msg.getSender().getLocalName());

					if (content instanceof GetTaxi) {
						SequentialBehaviour handleChooseBestTaxi = new SequentialBehaviour();
						handleChooseBestTaxi.addSubBehaviour(new HandleRequestTaxi(myAgent, msg));
						// handleChooseBestTaxi.addSubBehaviour(new HandleGetTaxiReplies(myAgent));
						addBehaviour(handleChooseBestTaxi);
					}
					break;
				case (ACLMessage.INFORM):
					// este coiso (comentado) está a "roubar" as mensagens de resposta dos taxis do
					// behaviour "handlegettaxireplies" (comentado acima), porque?
					// System.out.println("Este taxi está disponível: " + ((TaxiStatus)
					// msg.getContentObject()).isAvailable());
					statuses.add(msg);
					if (statuses.size() == registeredTaxis) {
						ArrayList<TaxiStatus> availableStatuses = new ArrayList<TaxiStatus>();
						for(ACLMessage status : statuses) {
							TaxiStatus taxiStatus = (TaxiStatus) status.getContentObject();
							if(taxiStatus.isAvailable()) {
								availableStatuses.add(taxiStatus);
							}
						}
						statuses = new ArrayList<ACLMessage>();
						if(availableStatuses.size() > 0) {							
							addBehaviour(new HandleAssignBestTaxi(myAgent, availableStatuses));
						}
						else {
							TaxiStatus ts = (TaxiStatus) msg.getContentObject();
							addBehaviour(new HandleNoTaxiAvailable(myAgent, ts.getProposedClient()));
						}
					}
					break;
				case (ACLMessage.FAILURE):
					TaxiNotAvailable tna = (TaxiNotAvailable) msg.getContentObject();
					addBehaviour(new HandleNoTaxiAvailable(myAgent, tna.getClient()));
					break;
				default:
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	class HandleNoTaxiAvailable extends OneShotBehaviour {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3644675928959754491L;
		private AID proposedClient;
		
		public HandleNoTaxiAvailable(Agent a, AID client) {
			super(a);
			this.proposedClient = client;
		}
		
		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
			msg.addReceiver(proposedClient);
			TryAgain ta = new TryAgain();
			try {
				msg.setContentObject(ta);
				send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class HandleAssignBestTaxi extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3508542778598802916L;
		private ArrayList<TaxiStatus> statuses;
		private double bestDistance = -1;
		private AID bestTaxi;
		private TaxiStatus chosenTaxiStatus;

		public HandleAssignBestTaxi(Agent a, ArrayList<TaxiStatus> statuses) {
			super(a);
			this.statuses = statuses;
		}

		@Override
		public void action() {
			for (TaxiStatus taxiStatus : statuses) {
				AID taxi = taxiStatus.getTaxiName();
				int coordX = taxiStatus.getCoordX();
				int coordY = taxiStatus.getCoordY();
				int clientCoordX = taxiStatus.getProposedClientCoordX();
				int clientCoordY = taxiStatus.getProposedClientCoordY();
				double distance = Math.sqrt((coordX - clientCoordX) * (coordX - clientCoordX)
						+ (coordY - clientCoordY) * (coordY - clientCoordY));
				if (bestDistance == -1 || bestDistance > distance) {
					bestDistance = distance;
					bestTaxi = taxi;
					chosenTaxiStatus = taxiStatus;
				}
			}
			// falta pegar no melhor taxi e enviar mensagem ao melhor taxi para ir para as
			// coords do cliente
			// depois o taxi deve passar a nao estar disponivel e ir ter com o cliente
			// no fim deve levar o cliente ao sitio desejado
			// por fim, o taxi estaciona no sitio desejado e passa a estar disponivel outra
			// vez
			System.out.println("Taxi encontrado, enviando ordem de novo cliente... ");
			ACLMessage order = new ACLMessage(ACLMessage.PROPOSE);
			order.addReceiver(bestTaxi);
			// TODO taxitask deve receber tambem as coords de destino do cliente, nao so as
			// de origem
			TaxiTask tt = new TaxiTask(chosenTaxiStatus.getProposedClientCoordX(),
					chosenTaxiStatus.getProposedClientCoordY(), chosenTaxiStatus.getProposedClientDestCoordX(),
					chosenTaxiStatus.getProposedClientDestCoordY(), chosenTaxiStatus.getProposedClient());
			try {
				order.setContentObject(tt);
				send(order);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class HandleRequestTaxi extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8129888221390366812L;
		private ACLMessage request;

		public HandleRequestTaxi(Agent a, ACLMessage request) {

			super(a);
			this.request = request;
		}

		public void action() {

			try {
				int clientCoordX = ((GetTaxi) request.getContentObject()).getCoordX();
				int clientCoordY = ((GetTaxi) request.getContentObject()).getCoordY();
				int clientDestCoordX = ((GetTaxi) request.getContentObject()).getDestinationCoordX();
				int clientDestCoordY = ((GetTaxi) request.getContentObject()).getDestinationCoordY();
				System.out.println("PEDIDO DE TAXI DE " + request.getSender().getName());
				// enviar mensagem de pedir status a todos os taxis
				lookupTaxis(request.getSender(), clientCoordX, clientCoordY, clientDestCoordX, clientDestCoordY);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

//--------------------------- Utility methods ----------------------------//

	void lookupTaxis(AID client, int clientCoordX, int clientCoordY, int clientDestCoordX, int clientDestCoordY) {

		ServiceDescription sd = new ServiceDescription();
		sd.setType("TAXI_AGENT");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			if (dfds.length > 0) {
				// server = dfds[0].getName();
				System.out.println("Localized taxis");
				// definir destinatario da msg para todos os taxis
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for (DFAgentDescription dfad : dfds) {
					msg.addReceiver(dfad.getName());
				}
				// envio de mensagem
				GetTaxiStatus gts = new GetTaxiStatus(client, clientCoordX, clientCoordY, clientDestCoordX,
						clientDestCoordY);
				try {
					msg.setContentObject((java.io.Serializable) gts);
					System.out.println("Contacting taxis... Please wait!");
					send(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//

			} else
				System.out.println("Couldn't localize taxis!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed searching int the DF!");
		}
	}
}
