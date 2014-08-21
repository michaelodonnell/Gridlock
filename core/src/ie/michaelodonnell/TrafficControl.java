package ie.michaelodonnell;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import ie.michaelodonnell.simulation.TrafficCone;
import ie.michaelodonnell.simulation.Vehicle;

public class TrafficControl {

    float brakingDistance = 60.0f;

    public TrafficControl () {

    }

    public void controlTraffic(Level level) {
        ArrayList<Vehicle> vehicles = level.getVehicles();
        float distanceAhead = 0.0f;
        int maxDistanceAheadAlignmentTolerance = 10;

        for (Vehicle vi : vehicles) {

            // Skip the vehicles which haven't been released;
            if (vi.isReleased() == false) continue;

            // We must prove that the vehicle is obstructed:
            vi.setObstructed(false);

            // Check for closed junctions:
            if(vi.getJunction() != null && vi.getJunction().isOpen() == false){
                vi.setObstructed(true);
            }

            // Check for Traffic and Traffic Cones ahead:
            vi.setObstacleAhead(false);
            for (Vehicle vj : vehicles) {
                distanceAhead = 0.0f;
                if ((vi.getDirection() == vj.getDirection()) && (vi.getSpeed() >= vj.getSpeed())) {
                    switch(vi.getDirection()) {
                        case 0: // North Bound Traffic
                            if (vi.getX () - vj.getX() < maxDistanceAheadAlignmentTolerance && vi.getX () - vj.getX() > -maxDistanceAheadAlignmentTolerance) distanceAhead = vj.getY() - vi.getY();
                            break;
                        case 1: // East Bound Traffic
                            if (vi.getY () - vj.getY() < maxDistanceAheadAlignmentTolerance && vi.getY () - vj.getY() > -maxDistanceAheadAlignmentTolerance) distanceAhead = vj.getX() - vi.getX();
                            break;
                        case 2: // South Bound Traffic
                            if (vi.getX () - vj.getX() < maxDistanceAheadAlignmentTolerance && vi.getX () - vj.getX() > -maxDistanceAheadAlignmentTolerance) distanceAhead = vi.getY() - vj.getY();
                            break;
                        case 3: // West Bound Traffic
                            if (vi.getY () - vj.getY() < maxDistanceAheadAlignmentTolerance && vi.getY () - vj.getY() > -maxDistanceAheadAlignmentTolerance) distanceAhead = vi.getX() - vj.getX();
                            break;
                    }
                    if(distanceAhead > 0.0 && distanceAhead <= brakingDistance){
                        vi.setSpeed(0);
                        vi.setObstacleAhead(true);
                        vi.setObstructed(true, vj.getId().toString());
                        break;
                    }
                }

                // For each traffic cone in the level:
                if (vi.isDiverting() == false) {
                    for (TrafficCone tc : GridLock.currentLevel.getTrafficCones()) {
                        if (tc.isPlaced() == false) continue;
                        int maxAlignmentTolerance = 15;
                        int brakingDistance = 25;

                        switch(vi.getDirection()) {
                            case 0: // North Bound Traffic
                                if (tc.getCenterX() - (vi.getX() + 8) < maxAlignmentTolerance && tc.getCenterX() - (vi.getX() + 12) > -maxAlignmentTolerance ){
                                    if (tc.getCenterY() - vi.getY() < brakingDistance + vi.getHeight() && tc.getCenterY() - vi.getY() > 0 ) {
                                        vi.setSpeed(0);
                                        vi.setObstacleAhead(true);
                                        vi.setObstructed(true, "C");
                                        break;
                                    }
                                }
                                break;
                            case 1: // East Bound Traffic
                                if (tc.getCenterY() - (vi.getY() + 24) < maxAlignmentTolerance && tc.getCenterY() - (vi.getY() + 24) > -maxAlignmentTolerance ){
                                    if (tc.getCenterX() - vi.getX() < brakingDistance + vi.getWidth() + 10 && tc.getCenterX() - vi.getX() > 0 ) {
                                        vi.setSpeed(0);
                                        vi.setObstacleAhead(true);
                                        vi.setObstructed(true, "C");
                                        break;
                                    }
                                }
                                break;
                            case 2: // South Bound Traffic
                                if (tc.getCenterX() - (vi.getX() + 12) < maxAlignmentTolerance && tc.getCenterX() - (vi.getX() + 12) > -maxAlignmentTolerance ){
                                    if (vi.getY() - tc.getCenterY() < brakingDistance && vi.getY() - tc.getCenterY() > 0 ) {
                                        vi.setSpeed(0);
                                        vi.setObstacleAhead(true);
                                        vi.setObstructed(true, "C");
                                        break;
                                    }
                                }
                                break;
                            case 3: // West Bound Traffic
                                if (tc.getCenterY() - (vi.getY() + 24) < maxAlignmentTolerance && tc.getCenterY() - (vi.getY() + 24) > -maxAlignmentTolerance ){
                                    if (vi.getX() - tc.getCenterX() < brakingDistance && vi.getX() - tc.getCenterX() > 0 ) {
                                        vi.setSpeed(0);
                                        vi.setObstacleAhead(true);
                                        vi.setObstructed(true, "C");
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }

            }

            // Check Junction:
            if ( (vi.getJunction() != null) && (vi.isDiverting() == false) ) {
                if (vi.getJunction().isOpen()) {
                    // If this vehicle is top in the queue....
                    if (vi.getJunction().getQueue().get(0).equals(vi)) {
                        // Take possession the junction if there in nothing ahead or if the vehicle is diverting:
                        if (vi.isObstacleAhead() == false) {
                            // Check that our desired exit is open:
                            if (vi.getJunction().getExits()[vi.getDirection()].isOpen()) {
                                // Go ahead:
                                vi.setObstructed(false);
                            } else {
                                // If there another exit available:
                                int newDirection = vi.getJunction().requestExit(vi.getDirection());
                                // System.out.println(vi.getId() + ": " + vi.getDirection() + ", " + newDirection);
                                if (newDirection != vi.getDirection()) {
                                    // Divert the vehicle:
                                    vi.setDiverting(true);
                                    vi.setTargetDirection(newDirection);
                                } else {
                                    // If not:
                                    vi.setSpeed(0);
                                    vi.setObstructed(true, "J");
                                }
                            }
                        }
                    } else {
                        // Wait in the queue for the junction to become available
                        vi.setSpeed(0);
                        vi.setObstructed(true, "J");
                    }
                } else {
                    // Wait for the junction to open:
                    vi.setSpeed(0);
                    vi.setObstructed(true, "J");
                }
            }

            // Resume stopped traffic that isn't obstructed:
            if(vi.isObstructed() == false){
                if (vi.getJunction() != null) {
                    if (vi.getSpeed() < vi.getJunction().getExitSpeed()) vi.setSpeed(vi.getJunction().getExitSpeed());
                } else {
                    vi.setSpeed(3);
                }
            }

        }
    }

    public void clearJunctions(Level level, SpriteBatch batch) {
        for (Integer i = 0; i < level.getJunctions().length; i++) {
            for (Integer j = 0; j < level.getJunctions()[i].length; j++) {

                // Clear the junctions:
                try {
                    if (level.getJunctions()[i][j].getQueue().get(0) != null && System.currentTimeMillis() - level.getJunctions()[i][j].getTimeOfOccupation() > level.getJunctions()[i][j].getMaxTimeOfOccupation()) {
                        Vehicle topVehicle = level.getJunctions()[i][j].getQueue().get(0);
                        float distance = 0.0f;
                        switch (topVehicle.getDirection()) {
                            case 0: // North Bound Traffic
                                distance = (level.getJunctions()[i][j].getPosY() - topVehicle.getY()) - topVehicle.getHeight();
                                break;
                            case 1: // East Bound Traffic
                                distance = (level.getJunctions()[i][j].getPosX() - topVehicle.getX()) - topVehicle.getWidth();
                                break;
                            case 2: // South Bound Traffic
                                distance = (topVehicle.getY() - level.getJunctions()[i][j].getPosY());
                                break;
                            case 3: // West Bound Traffic
                                distance = (topVehicle.getX() - level.getJunctions()[i][j].getPosX());
                                break;
                        }

                        if ( (distance > 50 || distance < -50) ) {
                            // System.out.println(topVehicle.getId() + " (" + i + " " + j + "): Distance:" + distance + ", " + topVehicle.getX() + ", " + topVehicle.getY() + ", " + level.getJunctions()[i][j].getPosX() + ", " + level.getJunctions()[i][j].getPosY());
                            topVehicle.forgetJunction();
                            topVehicle.setDiverting(false);
                            level.getJunctions()[i][j].getQueue().remove(0);
                            level.getJunctions()[i][j].setTimeOfOccupation();
                        }

                        // Check for gridlock:
                        /*level.getJunctions()[i][j].setDurationOfOccupation((int) ((System.currentTimeMillis() - level.getJunctions()[i][j].getTimeOfOccupation()) / 1000));
                        if ( (level.getJunctions()[i][j].getDurationOfOccupation() >= 7) && level.getJunctions()[i][j].isOpen() && (GridLock.getGameState().equals("level")) ) {
                            level.setGameOver(level.getJunctions()[i][j].getPosX(), level.getJunctions()[i][j].getPosY());
                        }*/

                        topVehicle = null;
                    }
                } catch (IndexOutOfBoundsException ioobe) {
                    // This junction has no queue
                }
            }
        }
    }

}
