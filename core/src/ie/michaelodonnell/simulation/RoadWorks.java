package ie.michaelodonnell.simulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class RoadWorks extends Image {
    private boolean junctionRoadWords;
    private

    public RoadWorks(Stage stage, int x, int y, Texture texture, boolean junctionRoadWords) {
        super(texture);
        this.setBounds(x, y, 35, 31);
        this.setJunctionRoadWords(junctionRoadWords);
        stage.addActor(this);
    }

    public boolean isJunctionRoadWords() {
        return junctionRoadWords;
    }

    public void setJunctionRoadWords(boolean junctionRoadWords) {
        this.junctionRoadWords = junctionRoadWords;
    }
}
