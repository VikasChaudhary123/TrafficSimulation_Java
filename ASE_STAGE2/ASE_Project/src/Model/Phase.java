package Model;
/**
 * This class stores information of 8 phases with respective durations
 */
public class Phase {

	private int phaseNumber;
	private int phaseDuration;

	/**
	 * @param phaseNumber - Each phase will have a phase number like 1,2,3,4,5,6,7 or 8
	 * @param phaseDuration - Each phase will have duration in seconds
	 */
	public Phase(int phaseNumber, int phaseDuration) {
		this.phaseNumber = phaseNumber;
		this.phaseDuration = phaseDuration;
	}

	/**
	 * @return phaseNumber - It can be 1, 2, 3, 4, 5, 6, 7 or 8
	 */
	public int getPhaseNumber() {
		return phaseNumber;
	}

	
	/**
	 * @return phaseDuration - Each phase will have different phase duration in seconds
	 */
	public int getPhaseDuration() {
		return phaseDuration;
	}

	
	/**
	 * String representation of Phase Object
	 * Not being used, can come handy if required
	 */
	@Override
	public String toString() {
		return phaseNumber + " " + phaseDuration;
	}
}
