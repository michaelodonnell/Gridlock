package ie.michaelodonnell.archive;

/**
 * Created by Michael on 8/17/2014.
 */
public class Unused {

    // unused Zoom method from the GridLock class:
    public boolean zoom(float initialDistance, float distance) {
        /*if ( (distance > initialDistance) && (camera.zoom - 0.01 >= 1.0) ) { // Zoom In:
            camera.zoom -= 0.01;
        } else if ( (mapPixelWidth / (camera.zoom + 0.01)) > Gdx.graphics.getWidth() && (mapPixelHeight / (camera.zoom + 0.01)) > Gdx.graphics.getHeight() ) { // Zoom Out:
            camera.zoom += 0.01; // camera.position.x
        }

        // Keep the map in the bounds of the screen:
        int minWidth = (Gdx.graphics.getWidth() / 2);
        int maxWidth = mapPixelWidth - (Gdx.graphics.getWidth() / 2);
        int minHeight = (Gdx.graphics.getHeight() / 2);
        int maxHeight = mapPixelHeight - (Gdx.graphics.getHeight() / 2);
        if (camera.position.x < minWidth * camera.zoom) camera.translate(minWidth * camera.zoom - camera.position.x, 0);
        if (camera.position.x > maxWidth / camera.zoom) // camera.translate(maxWidth / camera.zoom - camera.position.x, 0);

        message = camera.position.x + ", " + Gdx.graphics.getWidth() + ", " + mapPixelWidth * camera.zoom + ", " + maxWidth * camera.zoom;*/
        return true;
    }

}
