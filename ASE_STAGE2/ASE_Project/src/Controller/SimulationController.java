package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Model.Intersection;
import View.GUITraffic;
import LoggerPackage.MyLogger ;
public class SimulationController {
	
	public SimulationController(GUITraffic view, Intersection model){
		view.addSetListener(new SetListener());
	}
	
	
	public class SetListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Exit button pressed");
			MyLogger.getInstance().writeLogsToFile();
			System.exit(0);
		}
	}

}
