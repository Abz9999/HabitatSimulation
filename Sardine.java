
import java.util.List;
import java.util.Random;

/**
 * A simple model of a sardine.
 * PopulationSardine age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Sardine extends Animal
{
    // Characteristics shared by all sardine (class variables).
    // The age at which a sardine can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a sardine can live.
    private static final int MAX_AGE = 6;
    // The likelihood of a sardine breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The sardine's age.
    private int age;

    /**
     * Create a new sardine. A sardine may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the sardine will have a random age.
     * @param location The location within the field.
     */
    public Sardine(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the sardine does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState, Conditions conditions)
    {   incrementAge();
        
        // Sardines sleep at night
        if(isAlive() && conditions.isDay()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
                
            }
        }else if (isAlive()){
            setLocation(getLocation());
            Location location = getLocation();
            nextFieldState.placeAnimal(this, location);
        }
        
    }

    @Override
    public String toString() {
        return "Sardine{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the sardine's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this sardine is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New sardine are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Sardine young = new Sardine(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Field currentField)
    {
        int births;
        if(canBreed(currentField) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Sardine can breed if it has reached the breeding age, is female and uses
     * super method to check if there are any mates next to them
     * @return true if the sardine can breed, false otherwise.
     */
    private boolean canBreed(Field currentField)
    {
        return super.canBreed(currentField, Sardine.class) && age >= BREEDING_AGE && !isMale();
    }
}
