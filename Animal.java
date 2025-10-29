import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    
    private String gender;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        this.gender = randomGender();
    }
    
    
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param timeOfDay The current time of day.
     */
    abstract public void act(Field currentField, Field nextFieldState, Conditions conditions);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    private String randomGender(){
        String[] genders = { "female" , "male" };
        Random rand = new Random();
        return genders[rand.nextInt(2)];

    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
    
    protected boolean isMale(){
        return gender.equals("male");
    }
    /**
     * This ensures that animal can only breed if it is of the same species,
     * also if the animals are next to each other and one of them is male.
     * 
     */
    public <E> boolean canBreed(Field field, Class<E> type )
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location partnerLocation = null;
        while(partnerLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(type.isInstance(animal) && animal.isAlive() && animal.isMale()) {
                return true;

            }

        }
        return false;
    }
}
