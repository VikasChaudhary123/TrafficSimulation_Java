package Model;

public class SharedObject {

	Vehicle sharedVehicleBuffer ;
	private boolean empty; // no value available to get
	private boolean done; // production has finished
	
	public SharedObject() {
		sharedVehicleBuffer = null ;
		empty = true ;
		done = false ;
	}
	
	public Vehicle getSharedObject() {
		return sharedVehicleBuffer ;
	}
	
	public synchronized Vehicle getVehicle() {
		if (empty) {
			System.out.println("Shared buffer is empty");
			return null ;
		}	
		
//		System.out.println("Got from Shared buffer "+sharedVehicleBuffer.getPlateId());
		empty = true ;
		return sharedVehicleBuffer;
	}
	
	public synchronized boolean putVehicle(Vehicle v) {
		if(!empty) {
			return false ; 
		}
		sharedVehicleBuffer = v ;
		empty = false ;
//		System.out.println("Vehicle put in shared buffer "+v.getPlateId());
		return true ;
	}
	
	public void setDone() {
		done = true ;
	}
	
	public boolean getDone() {
		return done ;
	}
}
