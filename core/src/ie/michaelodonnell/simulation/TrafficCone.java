package ie.michaelodonnell.simulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ie.michaelodonnell.GridLock;

public class TrafficCone extends Image {
    private boolean placed = false;
    private Integer junctionX = null;
    private Integer junctionY = null;
    private Integer junctionExit = null;

    public TrafficCone(Stage stage, Texture texture, boolean draggable) {
        super(texture);
        this.setBounds(50, 125, 30, 35);
        stage.addActor(this);
        if (draggable) this.makeDraggable();
    }

    public void makeDraggable() {
        this.addListener(new DragListener() {
            private float offsetX, offsetY;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                TrafficCone tc = (TrafficCone) event.getTarget();
                tc.setPlaced(false);

                Actor target = event.getTarget();
                this.offsetX = event.getStageX() - target.getX();
                this.offsetY = event.getStageY() - target.getY();
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                TrafficCone tc = (TrafficCone) event.getTarget();

                // Open any closed exits:
                if (tc.junctionExit != null){
                    // System.out.println("Junction " + tc.getJunctionX() + " " + tc.getJunctionY() + " " + tc.getJunctionExit() + " exit is opening.");
                    GridLock.currentLevel.getJunctions()[tc.getJunctionX()][tc.getJunctionY()].getExits()[tc.getJunctionExit()].setOpen(true);
                    boolean exitsClosed = true;
                    while(exitsClosed) {
                        exitsClosed = false;
                        for (JunctionExit exit : GridLock.currentLevel.getJunctions()[tc.getJunctionX()][tc.getJunctionY()].getExits()) {
                            if (exit.isOpen() == false) {
                                exitsClosed = true;
                                exit.setOpen(true);
                            }
                        }
                    }
                }
                tc.clearJunctionVars();

                // Set the new cones position:
                tc.setPosition(event.getStageX() - offsetX, event.getStageY() - offsetY);
            }

            @Override
            public void dragStop (InputEvent event, float x, float y, int pointer) {
                TrafficCone tc = (TrafficCone) event.getTarget();

                // Disable the cone if it's on the toolbar:
                Float j = event.getStageX() - offsetX;
                Float k = event.getStageY() - offsetY;
                if (j > 960 && j < 1230 && k > 737 && k < 771) {
                    tc.setPlaced(false);
                } else {
                    tc.setPlaced(true);
                }

                // Close any relevant junctions:
                GridLock.currentLevel.closeJunctionExits();
            }
        });
    }

    private void clearJunctionVars() {
        if (this.junctionX != null) this.junctionX = null;
        if (this.junctionY != null) this.junctionY = null;
        if (this.junctionExit != null) this.junctionExit = null;
    }

    public Integer getJunctionX() {
        return junctionX;
    }

    public void setJunctionX(Integer junctionX) {
        this.junctionX = junctionX;
    }

    public Integer getJunctionY() {
        return junctionY;
    }

    public void setJunctionY(Integer junctionY) {
        this.junctionY = junctionY;
    }

    public Integer getJunctionExit() {
        return junctionExit;
    }

    public void setJunctionExit(Integer junctionExit) {
        this.junctionExit = junctionExit;
    }

    public void setJunctionVars(Integer x, Integer y, int exit) {
        this.setJunctionX(x);
        this.setJunctionY(y);
        this.setJunctionExit(exit);
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}