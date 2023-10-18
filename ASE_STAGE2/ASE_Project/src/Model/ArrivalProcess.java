package Model;

import java.util.Random;
public class ArrivalProcess implements Runnable {

	Intersection intersection ;
	int num = 1 ;
	
	public ArrivalProcess(Intersection intersection) {
		// TODO Auto-generated constructor stub
		this.intersection = intersection ;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Random random = new Random();
		int max = 3000;
		int min = 1000 ;
		int maxVehiclesToCreate = 100 ;
		while(true && num<=maxVehiclesToCreate) {
		
	        int randomNumber = random.nextInt(max - min) + min;
//	        System.out.println("Random number is "+randomNumber);
			try {
				Thread.sleep(randomNumber);
				String[] vehicleParameters = new String[8] ;
				vehicleParameters[0] = "S"+Integer.toString(random.nextInt(4)+1) ; //Segment 
				vehicleParameters[1] = getVehicleNumber() ; // Car plate number
				vehicleParameters[2] = getType(random.nextInt(3)) ; //Type
				vehicleParameters[3] = Integer.toString(random.nextInt(8-3)+3) ; // crossingTime
				vehicleParameters[4] = getDirection(random.nextInt(3)) ; // direction
				vehicleParameters[5] = Integer.toString(random.nextInt(40-5)+5) ; // length
				vehicleParameters[6] = Integer.toString(random.nextInt(45-5)+5) ; // emission
				vehicleParameters[7] = "waiting" ; // crossingStatus
				intersection.addNewVehicle(vehicleParameters, true);
				
			} catch (InterruptedException | NumberFormatException 
					|CarPlateNumberInvalid | InvalidInputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getType(int t) {
		if (t == 0) {
			return "CAR";
		}else if(t==1) {
			return "TRUCK" ;
		}
		return "BUS" ;
	}
	
	private String getDirection(int t) {
		if (t == 0) {
			return "LEFT";
		}else if(t==1) {
			return "RIGHT" ;
		}
		return "STRAIGHT" ;
	}
	
	private String getVehicleNumber() {
		String res = "" ;
		if (num < 10) {
			res = "00" ;
		}else if(num < 100) {
			res = "0" ;
		}
		res = res + Integer.toString(num);
		num++ ;
		return "VEH"+res ;
	}
	
	
}