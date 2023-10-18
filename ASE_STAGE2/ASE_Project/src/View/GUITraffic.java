package View;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Model.Intersection;
import javax.swing.table.*;
import LoggerPackage.MyLogger;
import Model.Vehicle;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

/**
 * Simple GUI for Traffic Simulation
 */
@SuppressWarnings("deprecation")
public class GUITraffic extends JFrame implements Observer, Runnable
{
    private Intersection intersection ;

	
    private JTextField carbonEmissions ;
    private JTextArea vehicleTable;
    private JScrollPane scrollListvehicle;
    private JTextArea phaseTable;
    private JScrollPane scrollListphase;
    private JTextArea segmentsummaryTable;
    private JScrollPane scrollListsegment;
    private JButton exitbutton;
    //GUI components
   
    // To create Vehicle Table
    String header = "|"+String.format("%-8s", "SEGMENT")
    +"|" + String.format("%-8s","VEHICLE") 
    + "|"+ String.format("%-6s", "TYPE")
	+ "|"+ String.format("%-14s", "CROSSING TIME") 
	+ "|"+ String.format("%-10s", "DIRECTION")
	+ "|"+ String.format("%-7s", "LENGTH") 
	+ "|"+ String.format("%-9s", "EMISSION")
	+ "|"+ String.format("%-8s", "STATUS") 
	+"\n"
    +"|"+String.format("%-8s", "-------")
    +"|" + String.format("%-8s","-------") 
    + "|"+ String.format("%-6s", "----")
	+ "|"+ String.format("%-14s", "-------------") 
	+ "|"+ String.format("%-10s", "---------")
	+ "|"+ String.format("%-7s", "------") 
	+ "|"+ String.format("%-9s", "--------")
	+ "|"+ String.format("%-8s", "------") 
	+"\n" ;
    
    /**
     * Create the frame with its panels.
     * @param list	Intersection which is Model of MVC pattern
     */
    public GUITraffic(Intersection inter)
    {
        this.intersection = inter;
        intersection.attach(this);
        
        //set up window title
        setTitle("Traffic Simulation");
        //ensure program ends when window closes
		setDefaultCloseOperation(EXIT_ON_CLOSE);
 
        //search panel contains label, text field and button
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(1,3));
        searchPanel.add(new JLabel("CO2 Emissions"));
        carbonEmissions = new JTextField(5);
        carbonEmissions.setEditable(false);
        carbonEmissions.setText("0.00 Kgs");
        searchPanel.add(carbonEmissions);   
        exitbutton = new JButton("Exit");  
        searchPanel.add(exitbutton);    
        
        
        //Set up the area where the results will be displayed.
       
        
        //set up south panel containing 2 previous areas
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(2,1));
        southPanel.add(searchPanel);
      
        
        //add south panel to the content pane
        Container contentPane = getContentPane();
        contentPane.add(southPanel, BorderLayout.SOUTH);
        
        //add north panel containing some buttons
        JPanel northPanel = new JPanel();
//       
//        northPanel.add (showListById);
//        northPanel.add(showListByName);
//        contentPane.add(northPanel, BorderLayout.NORTH);
        
        vehicleTable = new JTextArea(15,20);
        vehicleTable.setFont(new Font (Font.MONOSPACED, Font.PLAIN,14));
        vehicleTable.setEditable(false);
        scrollListvehicle = new JScrollPane(vehicleTable);
        contentPane.add(scrollListvehicle,BorderLayout.NORTH);
        
        segmentsummaryTable = new JTextArea(15,20);
        segmentsummaryTable.setFont(new Font (Font.MONOSPACED, Font.PLAIN,14));
        segmentsummaryTable.setEditable(false);
        scrollListsegment = new JScrollPane(segmentsummaryTable);
        contentPane.add(scrollListsegment,BorderLayout.CENTER);
        
        phaseTable = new JTextArea(15,20);
        phaseTable.setFont(new Font (Font.MONOSPACED, Font.PLAIN,14));
        phaseTable.setEditable(false);
        phaseTable.setText(intersection.listPhases());
        scrollListphase = new JScrollPane(phaseTable);
        contentPane.add(scrollListphase,BorderLayout.WEST);
        
        
        pack();
     // Set the size of the window
//        setSize(1000, 800); // Set the width to 800 and the height to 600

        // OR to maximize the window, uncomment the following line:
         setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        //pack and set visible
        pack();
        setVisible(true);
    }
    
    public void addSetListener(ActionListener a1) {
    	exitbutton.addActionListener(a1);
    }
    public void update(Observable o, Object arg) {
 
    	if (arg instanceof List<?>) {
            List<?> list = (List<?>) arg;
            if (!list.isEmpty() && list.get(0) instanceof Vehicle) {
                List<Vehicle> v = (List<Vehicle>) arg;
                setVehicleTableText(v) ;
             } else{
                // handle the case where the list is not a list of Vehicle objects but is a String
            	 
            }
        } else if(arg instanceof String[]){
            // handle the case where the arg object is not a list 
        	// Update Segment summary table and co2 emission
        	String[] res = (String[])arg ;
        	segmentsummaryTable.setText(res[0]);
        	carbonEmissions.setText(res[1]+" Kgs");
        	
        }
    }
    
    private void setVehicleTableText(List<Vehicle> v) {
    	StringBuffer allEntries = new StringBuffer();
        
        for (Vehicle vh : v) {
            // do something with each Vehicle object
        	allEntries.append(vh.toString()+"\n") ;
        }
        
        vehicleTable.setText(header+allEntries.toString());
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			System.out.println("GUI THREAD");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}