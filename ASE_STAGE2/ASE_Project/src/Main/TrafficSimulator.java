 package Main;
import Controller.SimulationController;
import Model.ArrivalProcess;
import Model.Intersection;
import Model.Intersection;
import View.GUITraffic;

public class TrafficSimulator {

	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Intersection model = new Intersection() ; // Model
		GUITraffic view = new GUITraffic(model) ; // View
		SimulationController controller = new SimulationController(view, model) ; // Controller 
		Thread th = new Thread(model) ;
		th.start(); //Start the thread for the model(Intersection)
		 
		ArrivalProcess ap = new ArrivalProcess(model) ;
		Thread th1 = new Thread(ap) ;
		th1.start();
	
	}
}
