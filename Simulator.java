import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * salmons and whales.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.04;
    // The probability that a salmon will be created in any given position.
    private static final double SALMON_CREATION_PROBABILITY = 0.12;   
    // The probability that a shark will be created in any given position.
    private static final double SHARK_CREATION_PROBABILITY = 0.05;
    // The probability that a sardine will be created in any given position.
    private static final double SARDINE_CREATION_PROBABILITY = 0.12;
    // The probability that a clownfish will be created in any given position.
    private static final double CLOWNFISH_CREATION_PROBABILITY = 0.12;
    
    // The current time of day
    private Conditions conditions;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        conditions = new Conditions(); //to intialise it
        
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each whale and salmon.
     */
    public void simulateOneStep()
    {
        if(step % 10 == 0){
            conditions.setHighTide(true);
        }else{ conditions.setHighTide(false);}
    
        conditions.advance();   
       
        
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState, conditions);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with whales and salmons.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, location);
                    field.placeAnimal(whale, location);
                }
                else if(rand.nextDouble() <= SALMON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Salmon salmon = new Salmon(true, location);
                    field.placeAnimal(salmon, location);
                }
                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, location);
                    field.placeAnimal(shark, location);
                }
                else if(rand.nextDouble() <= SARDINE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sardine sardine = new Sardine(true, location);
                    field.placeAnimal(sardine, location);
                }
                else if(rand.nextDouble() <= CLOWNFISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Clownfish clownfish = new Clownfish(true, location);
                    field.placeAnimal(clownfish, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
