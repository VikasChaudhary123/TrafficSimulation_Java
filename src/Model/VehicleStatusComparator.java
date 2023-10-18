package Model;

import java.util.Comparator;

// Defines an ordering on Vehicle objects on the Status
// To sort Vehicles with Status
public class VehicleStatusComparator implements Comparator<Vehicle> {

	@Override
	public int compare(Vehicle v1, Vehicle v2) {
		// TODO Auto-generated method stub
		return v1.getStatus().compareTo(v2.getStatus());
	}
}
