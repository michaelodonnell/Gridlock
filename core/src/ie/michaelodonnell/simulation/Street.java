package ie.michaelodonnell.simulation;

import ie.michaelodonnell.GridLock;

public class Street {
    private int direction;
    private int posX;
    private int posY;

    public Street(int x, int y, int direction) {
        this.direction = direction;
        switch (this.direction) {
            case 0: // North
                this.posX = (256 * x) + (7 * 16) + 10;
                this.posY = 0;
                break;
            case 1: // East
                this.posX = 0;
                this.posY = (256 * y) + (6 * 16) + 2;
                break;
            case 2: // South
                this.posX = (256 * x) + (7 * 16) - 18;
                this.posY = GridLock.mapPixelHeight;
                break;
            case 3: // West
                this.posX = GridLock.mapPixelWidth + 50;
                this.posY = (256 * y) + (6 * 16) + 30;
                break;
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getDirection() {
        return direction;
    }
}