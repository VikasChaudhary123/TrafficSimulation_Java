package Model;
public class CarPlateNumberInvalid extends Exception {
	

	/**
	 * Custom exception class, to show error message when Car's Number plate is not valid
	 */
	public CarPlateNumberInvalid(String errorMessage) {
		super(errorMessage);
	}
}