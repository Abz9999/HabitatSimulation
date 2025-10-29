import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * Sharks age, move, eat salmon, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).
    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a shark can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.25;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single salmon and sardine. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int SALMON_FOOD_VALUE = 4;
    private static final int SARDINE_FOOD_VALUE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    private static final int MAX_FOOD_VALUE = 8;

    // Individual characteristics (instance fields).

    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating salmon.
    private int foodLevel;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = 11;
    }

    /**
     * This is what the shark does most of the time: it hunts for
     * salmon. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState, Conditions conditions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && conditions.isDay()) {
            List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField, conditions);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
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
        return "Shark{" +
        "age=" + age +
        ", alive=" + isAlive() +
        ", location=" + getLocation() +
        ", foodLevel=" + foodLevel +
        '}';
    }

    /**
     * Increase the age. This could result in the shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this shark more hungry. This could result in the shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for Salmon or Sardine adjacent to the current location.
     * Sardine takes priority because it's tastier, Sharks compete with whale
     * for salmon if no sardine is available. They are more likely to win if
     * the tide is high.
     * Only the first live Salmon or Sardine is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field, Conditions conditions) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        double prob;
        if(conditions.isHighTide()){
            prob = 0.5;
        }else{
            prob = 0.4;
                
            }
            
        
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);

            // Prioritize Salmon
            if (animal instanceof Sardine sardine) {
                if (sardine.isAlive()) {
                    sardine.setDead();
                    foodLevel += SARDINE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            // If no Sardine is found, look for Salmon
            else if (animal instanceof Salmon salmon) {
                if (salmon.isAlive()) {
                    // Check if a Whale is also competing for this Salmon
                    boolean whaleCompeting = isWhaleCompeting(field, loc);
                    if (!whaleCompeting || rand.nextDouble() < prob) { // 70% chance if competing with whale
                        salmon.setDead();
                        foodLevel += SALMON_FOOD_VALUE;
                        foodLocation = loc;
                    }
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check if a Whale is also targeting the same Salmon.
     * @param field The field currently occupied.
     * @param salmonLocation The location of the Salmon.
     * @return true if a Whale is competing, false otherwise.
     */
    private boolean isWhaleCompeting(Field field, Location salmonLocation) {
        // Check all adjacent locations of the salmon location
        for (Location loc : field.getAdjacentLocations(salmonLocation)) {
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Whale) {
                return true; // A Whale is competing for this Salmon
            }
        }
        return false; // No Whale is competing
    }

    /**
     * Check whether this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Shark young = new Shark(false, loc);
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
     * A Shark can breed if it has reached the breeding age, is female and uses
     * super method to check if there are any mates next to them
     * @return true if the shark can breed, false otherwise.
     */
    private boolean canBreed(Field currentField)
    {
        return super.canBreed(currentField, Shark.class) && age >= BREEDING_AGE && !isMale();
    }
}
