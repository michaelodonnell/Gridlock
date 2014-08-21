package ie.michaelodonnell.simulation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ie.michaelodonnell.GridLock;

public class Vehicle extends Sprite {
    private int id;
    private int speed;
    private int defaultSpeed;
    private int maxSpeed;
    private int direction;
    private int initialDirection;
    private int targetDirection;
    private int initialRotation;
    private int targetRotation;
    private int vx;
    private int vy;
    private int startX;
    private int startY;
    private boolean released;
    private boolean obstacleAhead;
    private boolean obstructed;
    private String obstructedReason = "...";
    private boolean diverting;

    // Junction Specific Vars
    private Junction junction = null;
    private boolean requestedJunction;

    public Vehicle (int id, TextureAtlas.AtlasRegion texture, int x, int y, int speed, int maxSpeed, int direction) {
        super(texture);
        this.id = id;
        this.startX = x;
        this.startY = y;
        this.speed = speed;
        this.defaultSpeed = speed;
        this.maxSpeed = maxSpeed;
        this.setDirection(direction);
        this.initialDirection = direction;
        this.targetDirection = direction;

        if (this.getRotation() < 2 && this.getRotation() > -2) this.setRotation(360.0f);
        this.initialRotation = (int)this.getRotation();
        this.targetRotation = (int)this.getRotation();

        this.reset();
    }

    public Integer getSpeed() { return speed; }

    public void setSpeed(int speed) {
        if (speed > maxSpeed) speed = maxSpeed;
        this.speed = speed;
    }

    public void brake() {
        this.speed--;
    }

    public int getDirection() { return this.direction; }

    public int getInitialDirection() { return this.initialDirection; }

    public void setDirection(int direction) {
        if (direction > 3) direction = 0;
        if (this.getRotation() < 2 && this.getRotation() > -2) this.setRotation(360.0f);
        this.direction = direction;
        switch (this.direction) {
            case 0:
                vx = 0;
                vy = 1;
                if (this.isDiverting() == false) this.setRotation(360);
                break;
            case 1:
                vx = 1;
                vy = 0;
                if (this.isDiverting() == false) this.setRotation(270);
                break;
            case 2:
                vx = 0;
                vy = -1;
                if (this.isDiverting() == false) this.setRotation(180);
                break;
            case 3:
                vx = -1;
                vy = 0;
                if (this.isDiverting() == false) this.setRotation(90);
                break;
        }
    }

    public void move() {

        // System.out.println(this.getId() + ", " + this.getX() + ", " + this.getY() + ", Spd: " + this.getSpeed() + ", " + vx + ", " + vy + ", Dir: " + this.getDirection() + " (" + this.targetDirection + "), Int Dir: " + this.initialDirection + ", Rlsd? " + this.isReleased() + ", Obs? " + this.isObstructed() + " " + this.getObstructedReason());
        // System.out.println(this.getId() + ", " + this.getRotation() + ", " + this.getTargetRotation());

        if (this.released == true && GridLock.holdVehicles == false) {

            // Adjust Rotation;
            float rotationIncrement = 2.7f;
            int rotationDifferent = (int) this.getRotation() - this.getTargetRotation();
            if (rotationDifferent < 2 && rotationDifferent > -2) {
                // Only a tiny rotation adjustment:
                this.setRotation(this.getTargetRotation());
            } else {
                // Larger rotation adjustment:
                if ((this.getTargetDirection() == 0)) { // Turning North
                    if ((int) this.getRotation() > this.getTargetRotation()) {
                        this.rotate(-rotationIncrement); // Rotate clockwise:
                    } else if ((int) this.getRotation() < this.getTargetRotation()) {
                        this.rotate(rotationIncrement); // Rotate counter-clockwise:
                    }
                } else if ((this.getTargetDirection() == 1)) { // Turning East
                    if (this.getRotation() < 2 && this.getRotation() > -2) this.setRotation(360.0f);
                    if ((int) this.getRotation() > this.getTargetRotation()) {
                        this.rotate(-rotationIncrement); // Rotate clockwise:
                    } else if ((int) this.getRotation() < this.getTargetRotation()) {
                        this.rotate(rotationIncrement); // Rotate counter-clockwise:
                    }
                } else if ((this.getTargetDirection() == 2)) { // Turning South
                    if ((int) this.getRotation() > this.getTargetRotation()) {
                        this.rotate(-rotationIncrement); // Rotate clockwise:
                    } else if ((int) this.getRotation() < this.getTargetRotation()) {
                        this.rotate(rotationIncrement); // Rotate counter-clockwise:
                    }
                } else if ((this.getTargetDirection() == 3)) { // Turning West
                    if (this.getRotation() == 360.0) this.setRotation(0.0f);
                    if ((int) this.getRotation() < this.getTargetRotation()) {
                        this.rotate(rotationIncrement); // Rotate counter-clockwise:
                    } else if ((int) this.getRotation() > this.getTargetRotation()) {
                        this.rotate(-rotationIncrement); // Rotate clockwise:
                    }
                }
            }

            // If we're moving in the right direction....
            if (this.direction == this.targetDirection) {

                // Keep going, reset if we've gone too far:
                if ( (this.direction == 0) && (this.getY() > GridLock.mapPixelHeight) ) { // North Bound Limit
                    this.reset();
                } else if ( (this.direction == 1) && (this.getX() > GridLock.mapPixelWidth + this.getWidth()) ){ // East Bound Limit
                    this.reset();
                } else if ( (this.direction == 2) && (this.getY() < -this.getHeight() * 2) ){ // South Bound Limit
                    this.reset();
                } else if ( (this.direction == 3) && (this.getX() < -this.getWidth() * 2) ){ // West Bound Limit
                    this.reset();
                } else { // Continue driving
                    this.translate(vx * speed, vy * speed);
                }
            } else if (this.getJunction() != null) {
                // We're being diverted:
                switch (this.targetDirection) {
                    case 1: // Turning East
                        if (this.getDirection() == 2) { // South Bound
                            if (this.getJunction().getPosY() - this.getY() > 35){
                                this.setDirection(this.targetDirection);
                            }
                        } else if (this.getDirection() == 0) { // North Bound
                            if (this.getY() > this.getJunction().getPosY() - 38){
                                this.setDirection(this.targetDirection);
                            }
                        }
                        break;
                    case 2: // Turning South
                        if (this.getDirection() == 1) { // East Bound
                            if (this.getJunction().getPosX() - this.getX()  < 26) {
                                this.setDirection(this.targetDirection);
                            }
                        } else if (this.getDirection() == 3) { // West Bound
                            if (this.getJunction().getPosX() - this.getX() > 22) {
                                this.setDirection(this.targetDirection);
                            }
                        }
                        break;
                    case 3: // Turning West
                        if (this.getDirection() == 0) { // North Bound
                            if (this.getJunction().getPosY() - this.getY() < 10){
                                this.setDirection(this.targetDirection);
                            }
                        } else if (this.getDirection() == 2) { // South Bound
                            if (this.getY() - this.getJunction().getPosY() < -6){
                                this.setDirection(this.targetDirection);
                            }
                        }
                        break;
                    case 0: // Turning North
                        if (this.getDirection() == 3) { // West Bound
                            if (this.getJunction().getPosX() - this.getX() > -6) {
                                this.setDirection(this.targetDirection);
                            }
                        } else if (this.getDirection() == 1) { // East Bound
                            if (this.getJunction().getPosX() - this.getX() < -3) {
                                this.setDirection(this.targetDirection);
                            }
                        }
                        break;
                }
                this.translate(vx * speed, vy * speed);
            }

        }
    }

    public void reset() {
        this.setReleased(false);
        this.setDirection(this.initialDirection);
        this.targetDirection = this.initialDirection;
        this.targetRotation = this.initialRotation;
        switch (this.direction) {
            case 0: // North
                this.setPosition(this.startX, this.startY - this.getHeight() * 2);
                break;
            case 1: // East
                this.setPosition(this.startX - this.getWidth() * 2, this.startY);
                break;
            case 2: // South
                this.setPosition(this.startX, this.startY);
                break;
            case 3: // West
                this.setPosition(this.startX, this.startY);
                break;
        }
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRequestedJunction() {
        return this.requestedJunction;
    }

    public void setRequestedJunction(boolean requestedJunction) {
        this.requestedJunction = requestedJunction;
    }

    public boolean isObstructed() {
        return obstructed;
    }

    public void setObstructed(boolean obstructed) {
        this.obstructed = obstructed;
        this.obstructedReason = "...";
    }

    public void setObstructed(boolean obstructed, String reason) {
        this.obstructed = obstructed;
        this.obstructedReason = reason;
    }

    public String getObstructedReason() {
        return obstructedReason;
    }


    // Junction Related Methods:

    public void findNextJunction(Junction[][] junctions) {
        for (Integer i = 0; i < junctions.length; i++) {
            for (Integer j = 0; j < junctions[i].length; j++) {

                boolean xProximity = false;
                boolean yProximity = false;
                switch (this.direction) {
                    case 0: // North Bound
                        xProximity = junctions[i][j].getPosX() - this.getX() > -75 && junctions[i][j].getPosX() - this.getX() < 100;
                        yProximity = junctions[i][j].getPosY() - this.getY() > 15 && junctions[i][j].getPosY() - this.getY() < 90;
                        break;
                    case 1: // East Bound
                        xProximity = junctions[i][j].getPosX() - this.getX() > -15 && junctions[i][j].getPosX() - this.getX() < 75;
                        yProximity = junctions[i][j].getPosY() - this.getY() > -100 && junctions[i][j].getPosY() - this.getY() < 100;
                        break;
                    case 2: // South Bound
                        xProximity = junctions[i][j].getPosX() - this.getX() > -100 && junctions[i][j].getPosX() - this.getX() < 100;
                        yProximity = this.getY() - junctions[i][j].getPosY() > -15 && this.getY() - junctions[i][j].getPosY() < 50;
                        break;
                    case 3: // West Bound
                        xProximity = this.getX() - junctions[i][j].getPosX() > -15 && this.getX() - junctions[i][j].getPosX() < 50;
                        yProximity = junctions[i][j].getPosY() - this.getY() > -100 && junctions[i][j].getPosY() - this.getY() < 100;
                        break;
                }

                if (xProximity && yProximity) {
                    try {
                        if (this.getJunction() != null) {
                            if (this.junction.getPosX() != i || this.junction.getPosY() != j) {
                                this.requestedJunction = false;
                                this.setJunction(junctions[i][j]);
                            }
                        } else {
                            this.setJunction(junctions[i][j]);
                        }
                    } catch (NullPointerException npe) { }

                    // Request the junction:
                    if (this.isRequestedJunction() == false && !this.getJunction().getQueue().contains(this)) {
                        this.junction.request(this);
                        requestedJunction = true;
                    }
                }
            }
        }
    }

    public void forgetJunction() {
        this.requestedJunction = false;
        this.junction = null;
    }

    public Junction getJunction() {
        return junction;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    public boolean isObstacleAhead() {
        return obstacleAhead;
    }

    public void setObstacleAhead(boolean obstacleAhead) {
        this.obstacleAhead = obstacleAhead;
    }

    public void setDefaultSpeed() {
        this.speed = this.defaultSpeed;
    }

    public int getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(int targetDirection) {
        this.targetDirection = targetDirection;
        switch (targetDirection) {
            case 0: // North Bound
                if (this.getDirection() == 3) { // West Bound
                    this.targetRotation = 0;
                } else if (this.getDirection() == 1) { // East Bound
                    this.targetRotation = 360;
                }
                break;
            case 1: // East Bound
                this.targetRotation = 270;
                break;
            case 2: // South Bound
                this.targetRotation = 180;
                break;
            case 3: // West Bound
                this.targetRotation = 90;
                break;
        }
    }

    public int getTargetRotation() {
        return targetRotation;
    }

    public boolean isDiverting() {
        return diverting;
    }

    public void setDiverting(boolean diverting) {
        this.diverting = diverting;
    }
}
