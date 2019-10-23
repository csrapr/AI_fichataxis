package Main;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MainContainer {

	Runtime rt;
	ContainerController container;

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainContainer a = new MainContainer();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		a.initMainContainerInPlatform("localhost", "9090", "MainContainer");

		// Name of the Agent + Class Path of Agent's source Code

		a.startAgentInPlatform("TaxiAgent1", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent2", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent3", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent4", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent5", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent6", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent7", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent8", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent9", "Agents.TaxiAgent");
		a.startAgentInPlatform("TaxiAgent10", "Agents.TaxiAgent");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		a.startAgentInPlatform("ManagerAgent", "Agents.ManagerAgent");
		long agentNumber = 0;
		while (true) {
			a.startAgentInPlatform("ClientAgent" + agentNumber, "Agents.ClientAgent");
			agentNumber++;
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// remover este break quando os taxis estiverem a funcionar
			//break;
		}
	}
}