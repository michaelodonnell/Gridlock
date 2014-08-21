package ie.michaelodonnell;

import java.util.ArrayList;

public class LevelFactory {
    private ArrayList<Level> levels = new ArrayList<Level>();

    public LevelFactory() {

        // Create Level 0 (this is the level which will be displayed behind the menu):
        levels.add(new Level(5, 3, 500, 8));
        levels.get(0).setVehicles(10, 5);

        // Create Level 1 (an easy introductory level):
        levels.add(new Level(5, 3, 500, 8));
        levels.get(1).setVehicles(100, 5);
        //levels.get(1).getJunctions()[2][1].setOpen(false);
        levels.get(1).getJunctions()[2][1].setRoadWords(true);
        levels.get(1).setNumberOfTrafficCones(6);
        levels.get(1).setSecondsUntilRushhour(60);
        levels.get(1).setHint(0);

    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }
}
