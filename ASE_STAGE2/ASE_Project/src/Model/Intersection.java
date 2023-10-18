package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import Model.Segment.SignalStatus;
import Model.Vehicle.Status;
import Model.Vehicle.VehicleDirection;
import LoggerPackage.MyLogger ;

@SuppressWarnings("deprecation")
public class Intersection implements Subject, Runnable {

	private Segment[] segments = new Segment[4];
	private Phase[] phases = new Phase[8];
	private int segmentIndex = -1 ;
	private int currentPhaseDuration = 0 ;
	private int leftPhaseDurationLeft = 0 ;
	private int rightStraightPhaseDurationLeft = 0 ;
	private Segment activeSegment = null ;

	private int countCycles = 0 ;

	private List<Observer> registeredObservers = new LinkedList<Observer>();

	public Intersection() {

		// Reading phase durations from the file
		try {
			readPhaseDataFile();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(e1.getMessage());
			System.exit(1);
		}

		// Create Segments
		for(int i=0; i<segments.length;i++) {
			segments[i] = new Segment("S"+String.valueOf(i+1)) ;
		}

		segments[0].setPhases(phases[0], phases[5]);
		segments[1].setPhases(phases[2], phases[7]);
		segments[2].setPhases(phases[4], phases[1]);
		segments[3].setPhases(phases[6], phases[3]);

		// Reading vehicles data from the file
		try {
			readVehiclesDataFile();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.out.println(fnfe.getMessage());
			System.exit(1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (CarPlateNumberInvalid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);

		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void run() {

		while(true) {
			try {

				notifyObservers(getAllVehicles());
				notifyObservers(listSegmentSummary());

				segmentIndex = (segmentIndex+1) % segments.length ;

				activeSegment = segments[segmentIndex] ;

				currentPhaseDuration = Integer.max(activeSegment.getPhaseLeft().getPhaseDuration(), 
						activeSegment.getPhaseRightStraight().getPhaseDuration());

				leftPhaseDurationLeft = activeSegment.getPhaseLeft().getPhaseDuration() ;
				rightStraightPhaseDurationLeft = activeSegment.getPhaseRightStraight().getPhaseDuration() ;

				activeSegment.setSignalStatus(SignalStatus.GREEN) ;

				for(Segment segs:segments) {
					if (!segs.equals(activeSegment)) {
						segs.setSignalStatus(SignalStatus.RED) ;
					}
				}

				Vehicle nextLeftV = activeSegment.getNextLeftWaitingVehicle() ;
				if (nextLeftV != null) {
					activeSegment.getLeftBuffer().putVehicle(nextLeftV) ;
					//					System.out.println("In Buffer "+nextLeftV.getPlateId());
					Thread th = new Thread(nextLeftV) ;
					th.start();
				}

				Vehicle nextRightV = activeSegment.getNextRightStraightWaitingVehicle() ;
				if (nextRightV != null) {
					activeSegment.getRightStraightBuffer().putVehicle(nextRightV) ;
					//					System.out.println("In Buffer "+nextRightV.getPlateId());
					Thread t = new Thread(nextRightV) ;
					t.start();
				}

				//				System.out.println("Intersection thread sleeping for "+duration+" seconds");
				Thread.sleep(currentPhaseDuration*1000);

				if (segmentIndex == 3) {
					countCycles++; 
					MyLogger.getInstance().log(countCycles+" -Cycles completed");
				}

				activeSegment.setSignalStatus(SignalStatus.ORANGE);
				Thread.sleep(5 * 1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void VehicleCrossed(Vehicle v ) {
		notifyObservers(getAllVehicles());
		notifyObservers(listSegmentSummary());
		//		System.out.println(v.getPlateId()+" has reached to intersection");
		MyLogger.getInstance().log(v.getPlateId()+" crossed intersection went "+v.getDirection());
		v.changeStatus(v);

		if (v.getDirection().equals(VehicleDirection.LEFT)){
			activeSegment.getLeftBuffer().getVehicle() ;
			leftPhaseDurationLeft -= v.getCrossingTime() ;
			activeSegment.setIndexLeftVehicle(activeSegment.getIndexLeftVehicle() + 1) ;

			Vehicle nextLeftV = activeSegment.getNextLeftWaitingVehicle() ;
			if (nextLeftV != null) {
				if (nextLeftV.getCrossingTime() < leftPhaseDurationLeft) {
					boolean res = activeSegment.getLeftBuffer().putVehicle(nextLeftV) ;
					//					System.out.println("In Buffer "+nextLeftV.getPlateId());
					Thread th = new Thread(nextLeftV) ;
					th.start();
				}
			}
			else {
				System.out.println("No more vehicles going left");
			}

		}
		else {
			activeSegment.getRightStraightBuffer().getVehicle() ;
			rightStraightPhaseDurationLeft -= v.getCrossingTime() ;
			activeSegment.setIndexRightStraightVehicle(activeSegment.getIndexRightStraightVehicle() + 1) ;
			Vehicle nextRightV = activeSegment.getNextRightStraightWaitingVehicle() ;
			if (nextRightV != null) {
				if (nextRightV.getCrossingTime() < rightStraightPhaseDurationLeft) {
					boolean res = activeSegment.getRightStraightBuffer().putVehicle(nextRightV) ;
					//					System.out.println("In Buffer "+nextRightV.getPlateId());
					Thread t = new Thread(nextRightV) ;
					t.start();
				}

			}
			else {
				System.out.println("No more vehicles going right or straight");
			}
		}
	}

	public List<Vehicle> getAllVehicles(){
		List<Vehicle> v = new ArrayList<>() ;
		for(Segment s:segments) {
			v.addAll(s.getVehiclesLeft());
			v.addAll(s.getVehiclesRightStraight()) ;
		}
		return v ;
	}

	public String listPhases() {
		StringBuffer phasentries = new StringBuffer();
		phasentries.append("| Phase | Duration |\n");
		phasentries.append("| ----- | -------- |\n");
		for (Phase v : phases) {
			phasentries.append("| ");
			phasentries.append(v.getPhaseNumber());
			phasentries.append("     | ");
			phasentries.append(v.getPhaseDuration());
			phasentries.append("  |\n");
		}
		return phasentries.toString();
	}



	/**
	 * @return String array 
	 * Index 0 - Contains List<Vehicle>, it will be passed as message to observer VIEW(GUITraffic in our case) 
	 * Index 1 - Contains Segment summary as String
	 */
	public String[] listSegmentSummary()
	{  
		int countSegments = segments.length ;
		int[] countWaiting = new int[countSegments];
		int[] countCrossed = new int[countSegments] ;
		int[] lengths = new int[countSegments];
		int[] waitingTimes = new int[countSegments];
		float[] totalEmission = new float[countSegments];
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);


		//Iterate every segment
		for(int i=0; i<segments.length; i++) {
			//Iterate lane for left vehicles
			List<Vehicle> leftV = segments[i].getVehiclesLeft() ;
			for(Vehicle v:leftV) {
				if(v.getStatus() == Status.WAITING) {
					countWaiting[i]++ ;
					lengths[i]+= v.getLength() ;
				}
				else if(v.getStatus() == Status.CROSSED) { //Vehicle has crossed
					countCrossed[i]++ ;
					totalEmission[i]+= v.getEmission() * v.getTotalWaitingTime()/60 ;
					waitingTimes[i]+=v.getTotalWaitingTime() ;
				}
			}
			//Iterate lane for right and straight vehicles
			List<Vehicle> rigthStV = segments[i].getVehiclesRightStraight() ;
			for(Vehicle v:rigthStV) {
				if(v.getStatus() == Status.WAITING) {
					countWaiting[i]++ ;
					lengths[i]+= v.getLength() ;
				}
				else if(v.getStatus() == Status.CROSSED) {
					countCrossed[i]++ ;
					totalEmission[i]+= v.getEmission() * v.getTotalWaitingTime()/60 ;
					waitingTimes[i]+=v.getTotalWaitingTime() ;
				}
			}
		}

		float[] avgWaitingTimes = new float[countSegments];	
		for (int i = 0; i < countSegments; i++) {
			//	        	System.out.println(i+" waiting time"+waitingTimes[i]+" count crossed"+countCrossed[i]);
			avgWaitingTimes[i] = countCrossed[i] > 0 ? (float) waitingTimes[i] / (float)countCrossed[i] : 0;
		}

		StringBuffer segmententries = new StringBuffer();
		float totalCo2 = 0 ;

		String header = "|"+String.format("%-8s", "SEGMENT")
		+"|"+String.format("%-27s", "NUMBER OF VEHICLES WAITING")
		+"|"+String.format("%-19s", "WAITING LENGTH")
		+"|"+String.format("%-18s", "AVG WAITING TIME(FOR VEHICLES THAT HAVE CROSSED)") ;
		String headerLine = "|"+String.format("%-8s", "-------")
		+"|"+String.format("%-27s", "----------------")
		+"|"+String.format("%-19s", "--------------")
		+"|"+String.format("%-18s", "---------") ;
		segmententries.append(header+"\n");
		segmententries.append(headerLine+"\n");

		for (int i = 0; i < segments.length; i++) {
			segmententries.append("|" + String.format("%-8s", segments[i].getSegment_id()));
			segmententries.append("|" + String.format("%-27s",countWaiting[i]));
			segmententries.append("|" + String.format("%-19s",lengths[i]));
			if(Float.isNaN(avgWaitingTimes[i])) {
				segmententries.append("|" + String.format("%-18s","-"));

			}
			else {
				segmententries.append("|" + String.format("%-18s", df.format(avgWaitingTimes[i])));
			}
			segmententries.append("\n");

			totalCo2+= totalEmission[i] ;
		}

		//	        System.out.println(segmententries.toString());

		return new String[] {segmententries.toString(), df.format(totalCo2/1000)};
	}

	/**
	 * @throws FileNotFoundException - if vehicle.csv file not found
	 * @throws CarPlateNumberInvalid - Vehicle constructor throws this if
	 *                               plateNumber is not valid
	 * @throws InvalidInputException - Vehicle constructor throws this if direction,
	 *                               crossingStatus or vehicleType are not valid
	 */
	private void readVehiclesDataFile() throws FileNotFoundException, CarPlateNumberInvalid, InvalidInputException {
		// to store vehicle data - segment, plateNumber, crossingTime etc.
		String data[] = new String[8];
		int rowNumber = 0;
		InputStream dataFile = getClass().getResourceAsStream("/vehicle.csv") ;
		//		BufferedReader buff = new BufferedReader(new FileReader("src/vehicle.csv"));
		BufferedReader buff = new BufferedReader(new InputStreamReader(dataFile));
		try {
			String inputLine = buff.readLine(); // read first line
			while (inputLine != null) {
				if (rowNumber != 0) // rowNumber == 0 , will be skipped as it is just the heading
				{
					// split line into parts
					data = inputLine.split(",");
					// read vehicle info from data array
					data[0] = data[0].trim(); // segment number
					data[1] = data[1].trim(); // plateNumber
					data[2] = data[2].trim(); // vehicleType
					data[3] = data[3].trim(); // crossingTime
					data[4] = data[4].trim(); // direction
					data[5] = data[5].trim(); // length
					data[6] = data[6].trim(); // emissions
					data[7] = data[7].trim(); // crossingStatus

					// create vehicle object
					if (!isDuplicateVehicle(data[1])) {
						//						System.out.println("Add new vehicle for "+data[1]);
						addNewVehicle(data);
					}
					else {
						System.out.println(data[1]+" vehicle already exists");
					}
				}
				rowNumber++;
				// read next line
				inputLine = buff.readLine();

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				buff.close();
			} catch (IOException ioe) {
				// don't do anything
			}
		}
	}

	/**
	 * @param plateId to check if Vehicle exists with same plateID
	 */
	private boolean isDuplicateVehicle(String plateId) {
		for (Segment s : segments) {
			// checking vehicles on left lane of the segment
			for (Vehicle v : s.getVehiclesLeft()) {
				if (v.getPlateId().equals(plateId)) {
					return true;
				}
			}
			// checking vehicles on the right & straight lane of the segment
			for (Vehicle v : s.getVehiclesRightStraight()) {
				if (v.getPlateId().equals(plateId)) {
					return true;
				}
			}
		}
		return false;
	}

	// Wrapper to Simulate default parameter as java do not have default parameters
	public void addNewVehicle(String[] vehicleParameters)
			throws NumberFormatException, CarPlateNumberInvalid, InvalidInputException {
		addNewVehicle(vehicleParameters, false);
	}

	/**
	 * @param vehicleParameters - GUIClass will pass these to manager, to create
	 *                          Vehicle based on user input
	 * @param checkPlateId - if true, 
	 * @throws NumberFormatException - if crossingTime, length or emission can't be
	 *                               parsed to valid integer values
	 * @throws CarPlateNumberInvalid - If vehicle with same plateId already there in
	 *                               the list on GUI
	 * @throws InvalidInputException - Vehicle constructor throws this if direction,
	 *                               crossingStatus or type are not valid
	 */
	public void addNewVehicle(String[] vehicleParameters, boolean byPassDuplicateVehicle)
			throws NumberFormatException, CarPlateNumberInvalid, InvalidInputException {
		String segment = vehicleParameters[0];
		String plateId = vehicleParameters[1];
		String type = vehicleParameters[2];
		String crossingTime = vehicleParameters[3];
		String direction = vehicleParameters[4];
		String length = vehicleParameters[5];
		String emission = vehicleParameters[6];
		String crossingStatus = vehicleParameters[7];

		// Checking if user entered a Vehicle with plateId which is already there in the
		// list
		if (!isDuplicateVehicle(plateId.toUpperCase()) || byPassDuplicateVehicle  ) {
			Vehicle vehicle = new Vehicle(segment, plateId, type, crossingTime, direction, length, emission,
					crossingStatus);
			//			vehicle.setPhase(calculatePhase(vehicle)); // Now phase is not part of vehicle
			Segment vehicleSegment = null;
			//			System.out.println("VehicleSegment"+vehicle.getSegment());
			for (Segment s : segments) {
				//				System.out.println("SegmentId "+s.getSegment_id());
				if (s.getSegment_id().equals(vehicle.getSegment())) {
					vehicleSegment = s;
					break;
				}
			}
			if (vehicleSegment != null) {
				if (vehicle.getDirection() == VehicleDirection.LEFT) {
					int size_list = vehicleSegment.getVehiclesLeft().size();
					if (size_list > 0) {

						int timeToWait = vehicleSegment.getWaitingTimeForNewLeftVehicle() ;
						vehicle.setTotalWaitingTime(timeToWait);

						Vehicle lastVehicle = vehicleSegment.getVehiclesLeft().get(size_list-1) ;
						lastVehicle.setFollwingVehicle(vehicle);
						vehicle.setPosition(lastVehicle.getPosition() + vehicle.getLength()) ;

					}
					else {
						vehicle.setPosition(vehicle.getLength()) ;
						vehicle.setTotalWaitingTime(0);
					}
					vehicleSegment.getVehiclesLeft().add(vehicle);
				} else { // else case for direction straight and right
					int size_list = vehicleSegment.getVehiclesRightStraight().size();
					if (size_list > 0) {
						int timeToWait = vehicleSegment.getWaitingTimeForNewRightVehicle() ;
						vehicle.setTotalWaitingTime(timeToWait);

						Vehicle lastVehicle = vehicleSegment.getVehiclesRightStraight().get(size_list-1) ;
						lastVehicle.setFollwingVehicle(vehicle);
						vehicle.setPosition(lastVehicle.getPosition() + vehicle.getLength()) ;
					}
					else {
						vehicle.setPosition(vehicle.getLength()) ;
						vehicle.setTotalWaitingTime(0);
					}
					vehicleSegment.getVehiclesRightStraight().add(vehicle);
				}
				vehicle.setSegment_obj(vehicleSegment) ;
				vehicle.setIntersection(this) ;

				if (byPassDuplicateVehicle) {
					String msg = vehicle.getPlateId()+" added to Segment "+vehicle.getSegment()+" will go "+vehicle.getDirection() ;
					MyLogger.getInstance().log(msg);
				}
				//				System.out.println("Starting vehicle thread"+vehicle.getPlateId());
				//				Thread th = new Thread(vehicle) ;
				//				th.start();

				notifyObservers(getAllVehicles());
				notifyObservers(listSegmentSummary());

			}else {
				System.out.println("Segment is null");
			}

		} else {
			//			System.out.println("Plateid already exists"+plateId);
			throw new CarPlateNumberInvalid("Vehicle with same PlateNumber exists");
		}
	}

	/**
	 * It reads intersection.csv file to read phase data
	 * @throws FileNotFoundException - if csv file is not found
	 */
	private void readPhaseDataFile() throws FileNotFoundException {
		String phaseData[] = new String[2];
		int rowNumber = 0; // rowNumber == 0 will be skipped as first row is heading

		InputStream dataFile = getClass().getResourceAsStream("/intersection.csv") ;
		//		BufferedReader buff = new BufferedReader(new FileReader("src/intersection.csv"));
		BufferedReader buff = new BufferedReader(new InputStreamReader(dataFile));
		try {
			String inputLine = buff.readLine(); // read first line
			while (inputLine != null) {
				if (rowNumber != 0) {
					// split line into parts
					phaseData = inputLine.split(",");
					String phaseNumber = phaseData[0].trim();
					String phaseDuration = phaseData[1].trim();
					// create phase object
					Phase p = new Phase(Integer.parseInt(phaseNumber), Integer.parseInt(phaseDuration));
					// add to list
					phases[rowNumber - 1] = p;
				}
				rowNumber++;
				// read next line
				inputLine = buff.readLine();

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				buff.close();
			} catch (IOException ioe) {
				// don't do anything
			}
		}
	}


	@Override
	public void attach(Observer o) {
		// TODO Auto-generated method stub
		registeredObservers.add(o);
	}


	@Override
	public void detach(Observer o) {
		// TODO Auto-generated method stub
		registeredObservers.remove(o) ;
	}

	@Override
	public void notifyObservers(Object message) {
		// TODO Auto-generated method stub
		for(Observer ob:registeredObservers) {
			ob.update(null, message);
		}
	}

}
