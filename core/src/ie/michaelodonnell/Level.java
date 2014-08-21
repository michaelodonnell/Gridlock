package ie.michaelodonnell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;

import ie.michaelodonnell.simulation.Hint;
import ie.michaelodonnell.simulation.Junction;
import ie.michaelodonnell.simulation.RoadWorks;
import ie.michaelodonnell.simulation.Street;
import ie.michaelodonnell.simulation.TrafficCone;
import ie.michaelodonnell.simulation.Vehicle;

public class Level {
    private int streetsX;
    private int streetsY;
    private int maxVehicles;
    private int maxSpeed;
    private boolean gridLocked;
    private ArrayList<Street> streets = new ArrayList<Street>();
    private Junction[][] junctions = new Junction[5][3];
    private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
    private VehicleFactory factory;
    private ArrayList<TrafficCone> trafficCones = new ArrayList<TrafficCone>();
    private ArrayList<RoadWorks> roadWorks = new ArrayList<RoadWorks>();
    private int numberOfTrafficCones;
    private boolean trafficConesLaid;
    private boolean roadWorksLaid;
    private boolean junctionRoadWorksLaid;
    private Hint hint;

    // Clock
    private int releaseFrequency;
    private int hour;
    private long startTime = System.currentTimeMillis();
    private long lastClockUpdate = System.currentTimeMillis();
    private int secondsPerHour = 30;
    private int secondsUntilRushhour = 60;
    private long secondsUntilRushhourLastUpdate;

    // Gameplay Variables
    private int gridLockSignPosX;
    private int gridLockSignPosY;

    public Level(int sx, int sy, int releaseFrequency, int hour) {

        // Clear old actors:
        this.clearTrafficCones();
        this.clearImages();

        this.streetsX = sx;
        this.streetsY = sy;
        this.releaseFrequency = releaseFrequency;
        this.hour = hour;
        // Initialize the array of streets:
        for (int x = 0; x < streetsX; x++) {
            for (int y = 0; y < streetsY; y++) {
                if(y == 0){
                    streets.add(new Street(x, y, 0)); // North Bound
                    streets.add(new Street(x, y, 2)); // South Bound
                }
                if(x == 0){
                    streets.add(new Street(x, y, 1)); // East Bound
                    streets.add(new Street(x, y, 3)); // West Bound
                }
            }
        }

        // Initialize the array of junctions:
        int m = 0;
        int n;
        for (Street si : streets) { // North Bound Streets Only
            if (si.getDirection() != 0) continue;
            n = 0;
            for (Street sj : streets) { // East Bound Streets Only
                if (sj.getDirection() != 1) continue;
                junctions[m][n] = new Junction(si.getPosX() - 3, sj.getPosY() + 37);
                n++;
            }
            m++;
        }
    }

    private void clearImages() {
        if (GridLock.stage != null) {
            boolean exist = true;
            while(exist) {
                exist = false;
                for (Actor actor : GridLock.stage.getActors()) {
                    if (actor instanceof Image) {
                        actor.remove();
                        exist = true;
                    }
                }
            }
        }
    }

    public void displayGridLock(Stage stage) {
        Image gridLockSign = new Image(new Texture(Gdx.files.internal("gridlock.png")));
        gridLockSign.setBounds(this.getGridLockSignPosX(), this.getGridLockSignPosY(), 300, 100);
        stage.addActor(gridLockSign);
    }

    public int getReleaseFrequency() {
        return this.releaseFrequency;
    }

    public void setReleaseFrequency(int releaseFrequency) {
        this.releaseFrequency = releaseFrequency;
    }

    public int getHour() {
        if ((System.currentTimeMillis() - this.lastClockUpdate) / 1000 > this.secondsPerHour) {
            this.hour++;
            this.lastClockUpdate = System.currentTimeMillis();
        }
        if (this.hour > 23) this.hour = 0;
        return this.hour;
    }

    public ArrayList<Street> getStreets() {
        return streets;
    }

    public Junction[][] getJunctions() {
        return this.junctions;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(int maxVehicles, int maxSpeed) {
        this.maxVehicles = maxVehicles;
        this.maxSpeed = maxSpeed;
        this.factory = new VehicleFactory();
        this.vehicles = factory.createVehicles(streets, maxVehicles, maxSpeed);
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setGameOver(int x, int y) {
        this.setGridLocked(true);
        GridLock.holdVehicles = true;
        GridLock.setLevelInProgress(false);
        GridLock.setGameState("gameover");
        gridLockSignPosX = x - 150;
        gridLockSignPosY = y - 50;
    }

    public int getGridLockSignPosX() {
        return gridLockSignPosX;
    }

    public int getGridLockSignPosY() {
        return gridLockSignPosY;
    }

    public boolean isGridLocked() {
        return gridLocked;
    }

    public void setGridLocked(boolean gridLocked) {
        this.gridLocked = gridLocked;
    }


    // Traffic Cone Related Methods:

    public ArrayList<TrafficCone> getTrafficCones() {
        return this.trafficCones;
    }

    public int getNumberOfTrafficCones() {
        return numberOfTrafficCones;
    }

    public void setNumberOfTrafficCones(int numberOfTrafficCones) {
        this.numberOfTrafficCones = numberOfTrafficCones;
    }

    public boolean isTrafficConesLaid() {
        return trafficConesLaid;
    }

    public void setTrafficConesLaid(boolean trafficConesLaid) {
        this.trafficConesLaid = trafficConesLaid;
    }

    public void layTrafficCones(Stage stage) {
        for (int i = 0; i < this.getNumberOfTrafficCones(); i++) {
            TrafficCone cone = new TrafficCone(stage, new Texture(Gdx.files.internal("cone.png")), true);
            cone.setPosition(Gdx.graphics.getWidth() - 290 + (i * 30), Gdx.graphics.getHeight() - 60);
            this.trafficCones.add(cone);
        }
        this.setTrafficConesLaid(true);
    }

    public void setRoadWorks() {
        this.roadWorks.add(works);
    }

    public void layRoadWorks(Stage stage) {
        for (RoadWorks roadWorks : this.roadWorks) {
            if (roadWorks.isJunctionRoadWords() == false) {
                RoadWorks works = new RoadWorks(stage, 0, 0, new Texture(Gdx.files.internal("roadworks_35_31.png")), false);
            }
        }

        this.setRoadWorksLaid(true);
    }

    public void layJunctionRoadWorks(Stage stage) {
        for (Integer i = 0; i < this.getJunctions().length; i++) {
            for (Integer j = 0; j < this.getJunctions()[i].length; j++) {
                if (this.getJunctions()[i][j].isRoadWords()) {
                    RoadWorks works = new RoadWorks(stage, this.getJunctions()[i][j].getPosX() - 17, this.getJunctions()[i][j].getPosY() - 15, new Texture(Gdx.files.internal("roadworks_35_31.png")), true);
                    this.roadWorks.add(works);
                }
            }
        }
        this.setJunctionRoadWorksLaid(true);
    }

    public void closeJunctionExits() {
        int maxConeDistanceFromJunction = 100;
        int maxAlignmentTolerance = 25;
        int direction;

        // For each junction:
        for (Integer i = 0; i < this.getJunctions().length; i++) {
            for (Integer j = 0; j < this.getJunctions()[i].length; j++) {

                // For each traffic cone:
                for (TrafficCone tc : this.trafficCones) {
                    // Check if this cone affect any junction exits:
                    if (tc.isPlaced() == false) continue;

                    // North bound exits:
                    direction = 0;
                    if (this.getJunctions()[i][j].getPosX() - tc.getCenterX() < maxAlignmentTolerance && this.getJunctions()[i][j].getPosX() - tc.getCenterX() > -maxAlignmentTolerance ){
                        if (tc.getCenterY() - this.getJunctions()[i][j].getPosY() < maxConeDistanceFromJunction && tc.getCenterY() - this.getJunctions()[i][j].getPosY()  > 0 ) {
                            // The cone affects the junction, close the junction exit:
                            if (this.getJunctions()[i][j].getExits()[direction].isOpen()){
                                // System.out.println("Junction " + i + " " + j + " " + direction + " exit is closing.");
                                this.getJunctions()[i][j].getExits()[direction].setOpen(false);
                                tc.setJunctionVars(i, j, direction);
                            }
                        }
                    }

                    // East bound exits:
                    direction = 1;
                    if (this.getJunctions()[i][j].getPosY() - tc.getCenterY() < maxAlignmentTolerance && this.getJunctions()[i][j].getPosY() - tc.getCenterY() > -maxAlignmentTolerance ){
                        if (tc.getCenterX() - this.getJunctions()[i][j].getPosX() < maxConeDistanceFromJunction && tc.getCenterX() - this.getJunctions()[i][j].getPosX()  > 0 ) {
                            // The cone affects the junction, close the junction exit:
                            if (this.getJunctions()[i][j].getExits()[direction].isOpen()){
                                // System.out.println("Junction " + i + " " + j + " " + direction + " exit is closing.");
                                this.getJunctions()[i][j].getExits()[direction].setOpen(false);
                                tc.setJunctionVars(i, j, direction);
                            }
                        }
                    }
                    // South bound exits:
                    direction = 2;
                    if (this.getJunctions()[i][j].getPosX() - tc.getCenterX() < maxAlignmentTolerance && this.getJunctions()[i][j].getPosX() - tc.getCenterX() > -maxAlignmentTolerance ){
                        if (this.getJunctions()[i][j].getPosY() - tc.getCenterY() < maxConeDistanceFromJunction && this.getJunctions()[i][j].getPosY() - tc.getCenterY() > 0 ) {
                            // The cone affects the junction, close the junction exit:
                            if (this.getJunctions()[i][j].getExits()[direction].isOpen()){
                                // System.out.println("Junction " + i + " " + j + " " + direction + " exit is closing.");
                                this.getJunctions()[i][j].getExits()[direction].setOpen(false);
                                tc.setJunctionVars(i, j, direction);
                            }
                        }
                    }

                    // West bound exits:
                    direction = 3;
                    if (this.getJunctions()[i][j].getPosY() - tc.getCenterY() < maxAlignmentTolerance && this.getJunctions()[i][j].getPosY() - tc.getCenterY() > -maxAlignmentTolerance ){
                        if (this.getJunctions()[i][j].getPosX() - tc.getCenterX() < maxConeDistanceFromJunction && this.getJunctions()[i][j].getPosX() - tc.getCenterX() > 0 ) {
                            // The cone affects the junction, close the junction exit:
                            if (this.getJunctions()[i][j].getExits()[direction].isOpen()){
                                // System.out.println("Junction " + i + " " + j + " " + direction + " exit is closing.");
                                this.getJunctions()[i][j].getExits()[direction].setOpen(false);
                                tc.setJunctionVars(i, j, direction);
                            }
                        }
                    }
                }
            }
        }
    }

    public void clearTrafficCones () {
        if (GridLock.stage != null) {
            boolean conesExist = true;
            while(conesExist) {
                conesExist = false;
                for (Actor actor : GridLock.stage.getActors()) {
                    if (actor instanceof TrafficCone) {
                        actor.remove();
                        conesExist = true;
                    }
                }
            }
        }
    }

    public Integer getSecondsUntilRushhour() {
        // System.out.println(secondsUntilRushhour);
        return secondsUntilRushhour;
    }

    public void setSecondsUntilRushhour(int secondsUntilRushhour) {
        this.secondsUntilRushhour = secondsUntilRushhour;
    }

    public void updateSecondsUntilRushhour() {
        if ( (System.currentTimeMillis() - this.secondsUntilRushhourLastUpdate > 1000) && this.secondsUntilRushhour > 0) {
            secondsUntilRushhour--;
            secondsUntilRushhourLastUpdate = System.currentTimeMillis();
        }
    }

    public boolean isRoadWorksLaid() {
        return roadWorksLaid;
    }

    public void setRoadWorksLaid(boolean roadWorksLaid) {
        this.roadWorksLaid = roadWorksLaid;
    }

    public Hint getHint() {
        return hint;
    }

    public void setHint(Integer index) {
        this.hint = new Hint(new Texture(Gdx.files.internal("hints/" + index.toString() + ".png")));
    }

    public boolean isJunctionRoadWorksLaid() {
        return junctionRoadWorksLaid;
    }

    public void setJunctionRoadWorksLaid(boolean junctionRoadWorksLaid) {
        this.junctionRoadWorksLaid = junctionRoadWorksLaid;
    }
}