package ie.michaelodonnell.simulation;

import java.util.ArrayList;

public class Junction {
    private int posX;
    private int posY;
    private ArrayList<Vehicle> queue = new ArrayList<Vehicle>();
    private long timeOfOccupation;
    private int maxTimeOfOccupation = 1000;
    private int durationOfOccupation = 0;
    private int exitSpeed = 3;
    private boolean open = true;
    private boolean roadWords = false;
    private JunctionExit[] exits = new JunctionExit[4];

    public Junction(int x, int y) {
        this.setPosX(x);
        this.setPosY(y);
        this.exits[0] = new JunctionExit();
        this.exits[1] = new JunctionExit();
        this.exits[2] = new JunctionExit();
        this.exits[3] = new JunctionExit();
    }

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public ArrayList<Vehicle> getQueue() {
        return this.queue;
    }

    public void setQueue(ArrayList<Vehicle> queue) {
        this.queue = queue;
    }

    public void request(Vehicle v) {
        this.queue.add(v);
        if (this.queue.size() == 1) this.timeOfOccupation = System.currentTimeMillis();
    }

    public long getTimeOfOccupation() {
        return timeOfOccupation;
    }

    public void setTimeOfOccupation() {
        this.timeOfOccupation = System.currentTimeMillis();
    }

    public int getMaxTimeOfOccupation() {
        return maxTimeOfOccupation;
    }

    public Integer getExitSpeed() {
        return exitSpeed;
    }

    public void setExitSpeed(int exitSpeed) {
        this.exitSpeed = exitSpeed;
    }

    public Integer getDurationOfOccupation() {
        return durationOfOccupation;
    }

    public void setDurationOfOccupation(int durationOfOccupation) {
        this.durationOfOccupation = durationOfOccupation;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isRoadWords() {
        return roadWords;
    }

    public void setRoadWords(boolean roadWords) {
        this.roadWords = roadWords;
    }

    public JunctionExit[] getExits() {
        return exits;
    }

    public void setExits(JunctionExit[] exits) {
        this.exits = exits;
    }

    public int requestExit(int direction) {
        exitSearch : switch (direction) {
            case 0: // North Bound
                if (this.getExits()[1].isOpen()) { // Check East
                    return 1;
                } else if (this.getExits()[3].isOpen()) { // Check West
                    return 3;
                }
                break;
            case 1: // East Bound
                if (this.getExits()[2].isOpen()) { // Check South
                    return 2;
                } else if (this.getExits()[0].isOpen()) { // Check North
                    return 0;
                }
                break;
            case 2: // South Bound
                if (this.getExits()[3].isOpen()) { // Check West
                    return 3;
                } else if (this.getExits()[1].isOpen()) { // Check East
                    return 1;
                }
                break;
            case 3: // West Bound
                if (this.getExits()[0].isOpen()) { // Check North
                    return 0;
                } else if (this.getExits()[2].isOpen()) { // Check South
                    return 2;
                }
                    break;
        }
        return direction;
    }
}