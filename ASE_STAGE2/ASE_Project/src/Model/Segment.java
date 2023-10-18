package Model;

import java.util.ArrayList;
import java.util.List;

import LoggerPackage.MyLogger;
import Model.Vehicle.Status;

public class Segment {

	public enum SignalStatus {
	    GREEN, ORANGE, RED
	}
	
	private SignalStatus signalStatus ;
	
	private String segment_id; // Segment id could be S1, S2, S3 or S4
	

	private Phase phaseLeft ;
	private Phase phaseRightStraight ;
	
	private List<Vehicle> vehiclesLeft;
	private List<Vehicle> vehiclesRightStraight;
	
	private SharedObject leftBuffer ;
	private SharedObject rightStraightBuffer ;
	
	private int indexLeftVehicle = 0 ;
	private int indexRightStraightVehicle = 0 ;
	
	private int segment_remainingTime = 0 ;
	
	
	public Segment(String segment_id) {
		this.segment_id = segment_id ;
		signalStatus = SignalStatus.RED ;
		vehiclesLeft = new ArrayList<>() ;
		vehiclesRightStraight = new ArrayList<>() ;
		setLeftBuffer(new SharedObject()) ;
		setRightStraightBuffer(new SharedObject()) ;
	}
	
	public void setPhases(Phase left, Phase rightst) {
		this.phaseLeft = left; 
		this.phaseRightStraight = rightst ;
	}
	
	public Phase getPhaseLeft() {
		return phaseLeft ;
	}
	
	public Phase getPhaseRightStraight() {
		return phaseRightStraight;
	}
	
	public Vehicle getNextLeftWaitingVehicle() {
		if (getIndexLeftVehicle() >= vehiclesLeft.size()) {
			return null ;
		}
		
		while(vehiclesLeft.get(getIndexLeftVehicle()).getStatus() == Status.CROSSED) {
			setIndexLeftVehicle(getIndexLeftVehicle() + 1) ;
		}
		if (getIndexLeftVehicle() >= vehiclesLeft.size()) {
			return null ;
		}
		
		return vehiclesLeft.get(getIndexLeftVehicle()) ;
	}
	
	public Vehicle getNextRightStraightWaitingVehicle() {
		if (getIndexRightStraightVehicle() >= vehiclesRightStraight.size()) {
			return null ;
		}
		
		while(vehiclesRightStraight.get(getIndexRightStraightVehicle()).getStatus() == Status.CROSSED) {
			setIndexRightStraightVehicle(getIndexRightStraightVehicle() + 1) ;
		}
		if (getIndexRightStraightVehicle() >= vehiclesRightStraight.size()) {
			return null ;
		}
		
		return vehiclesRightStraight.get(getIndexRightStraightVehicle()) ;
	}
	
	// This will return waiting time to cross for new vehicle being added in the lane,
	// based on the vehicles that are already waiting
	
	public int getWaitingTimeForNewLeftVehicle() {
		int currentIndex = getIndexLeftVehicle() ;
		if(currentIndex >= vehiclesLeft.size()) {
			return 0 ;
		}
		while(vehiclesLeft.get(currentIndex).getStatus()==Status.CROSSED) {
			currentIndex++ ;
		}
		
		int t = 0 ;
		while(currentIndex < vehiclesLeft.size()) {
			t+=vehiclesLeft.get(currentIndex).getCrossingTime() ;
			currentIndex++ ;
		}
		return t ;
	}
	
	public int getWaitingTimeForNewRightVehicle() {
		int currentIndex = getIndexRightStraightVehicle() ;
		if(currentIndex >= vehiclesRightStraight.size()) {
			return 0 ;
		}
		while(vehiclesRightStraight.get(currentIndex).getStatus()==Status.CROSSED) {
			currentIndex++ ;
		}
		
		int t = 0 ;
		while(currentIndex < vehiclesRightStraight.size()) {
			t+=vehiclesRightStraight.get(currentIndex).getCrossingTime() ;
			currentIndex++ ;
		}
		return t ;
	}
	
	// Getters and Setters
	
	/**
	 * @return the signalStatus
	 */
	public SignalStatus getSignalStatus() {
		return signalStatus;
	}

	/**
	 * @param signalStatus the signalStatus to set
	 */
	public void setSignalStatus(SignalStatus signalStatus) {
		if (this.signalStatus == SignalStatus.GREEN && signalStatus == SignalStatus.RED) {
//			System.out.println("Segment "+segment_id+" signal changed to Red");
			// Vehicles in that segment should update their positions
		}
		this.signalStatus = signalStatus;
		if (signalStatus == SignalStatus.GREEN ){
			String mesg = signalStatus+" Signal for Segment: "+segment_id +"----------------------------";
			MyLogger.getInstance().log(mesg);
			// Vehicles should start moving in that segment
		}
		else if(signalStatus == SignalStatus.ORANGE){
			String mesg = signalStatus+" Signal for Segment: "+segment_id ;
			MyLogger.getInstance().log(mesg);
		}
	}

	/**
	 * @return the segment_id
	 */
	public String getSegment_id() {
		return segment_id;
	}

	/**
	 * @param segment_id the segment_id to set
	 */
	public void setSegment_id(String segment_id) {
		this.segment_id = segment_id;
	}

	/**
	 * @return the vehiclesLeft
	 */
	public List<Vehicle> getVehiclesLeft() {
		return vehiclesLeft;
	}

	/**
	 * @param vehiclesLeft the vehiclesLeft to set
	 */
	public void setVehiclesLeft(List<Vehicle> vehiclesLeft) {
		this.vehiclesLeft = vehiclesLeft;
	}

	/**
	 * @return the vehiclesRightStraight
	 */
	public List<Vehicle> getVehiclesRightStraight() {
		return vehiclesRightStraight;
	}

	/**
	 * @param vehiclesRightStraight the vehiclesRightStraight to set
	 */
	public void setVehiclesRightStraight(List<Vehicle> vehiclesRightStraight) {
		this.vehiclesRightStraight = vehiclesRightStraight;
	}

	/**
	 * @return the leftBuffer
	 */
	public SharedObject getLeftBuffer() {
		return leftBuffer;
	}

	/**
	 * @param leftBuffer the leftBuffer to set
	 */
	public void setLeftBuffer(SharedObject leftBuffer) {
		this.leftBuffer = leftBuffer;
	}

	/**
	 * @return the rightStraightBuffer
	 */
	public SharedObject getRightStraightBuffer() {
		return rightStraightBuffer;
	}

	/**
	 * @param rightStraightBuffer the rightStraightBuffer to set
	 */
	public void setRightStraightBuffer(SharedObject rightStraightBuffer) {
		this.rightStraightBuffer = rightStraightBuffer;
	}

	/**
	 * @return the indexLeftVehicle
	 */
	public int getIndexLeftVehicle() {
		return indexLeftVehicle;
	}

	/**
	 * @param indexLeftVehicle the indexLeftVehicle to set
	 */
	public void setIndexLeftVehicle(int indexLeftVehicle) {
		this.indexLeftVehicle = indexLeftVehicle;
	}

	/**
	 * @return the indexRightStraightVehicle
	 */
	public int getIndexRightStraightVehicle() {
		return indexRightStraightVehicle;
	}

	/**
	 * @param indexRightStraightVehicle the indexRightStraightVehicle to set
	 */
	public void setIndexRightStraightVehicle(int indexRightStraightVehicle) {
		this.indexRightStraightVehicle = indexRightStraightVehicle;
	}
}
