package Model;

public class InvalidInputException extends Exception {

	/**
	 * Custom class to show error to user.
	 * To show wrong segment(Anything besides s1,s2, s3....s8 is a wrong segment) 
	 * To show wrong wrong vehicle type(Anything besides car, truck and bus is not valid) 
	 * To show wrong Vehicle direction(direction can only be left, right or straight) 
	 * To show wrong Vehicle status(vehicle status can only be waiting, crossed)
	 */
	public InvalidInputException(String errorMessage) {
		super(errorMessage);
	}
}