/** Keeps track of the time of day and the weather (tides)
 *  Day and night tracked in steps of 12 where 24 steps is a full day
 * @author Rahima Oqubay K23007340 
 * @version 7.0
 */
public class Conditions {
    // Fields below
    private static final int DAY_DURATION = 8; 
    private static final int NIGHT_DURATION = 2; 
    private static boolean highTide = false;
    private int currentStep;
    
    /**
     * Constructor for time of day class.
     */
    public Conditions() {
        this.currentStep = 0; // Start at the beginning of the day
    }

    /**
     * Advance the time by one step.
     */
    public void advance() {
        currentStep++;
        if (currentStep >= DAY_DURATION + NIGHT_DURATION) {
            currentStep = 0; // Reset to the beginning of the day when we reach 24 steps
        }
    }

    /**
     * Check if it's currently day
     */
    public boolean isDay() {
        return currentStep < DAY_DURATION;
    }
    /**
     * check if the tide is high
     */
    public boolean isHighTide(){
        return highTide;
    }
    /**
     * set the tide
     */
    public void setHighTide(boolean choice){
        highTide = choice;
        
    }

    /**
     * Check if it's currently night
     */
    public boolean isNight() {
        return !isDay();
    }

    /**
     * Getter method for the current step aka "time" in the day.
     */
    public int getCurrentStep() {
        return currentStep;
    }
}