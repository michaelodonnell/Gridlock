package ie.michaelodonnell;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;

import ie.michaelodonnell.screens.Menu;
import ie.michaelodonnell.simulation.Clock;
import ie.michaelodonnell.simulation.Vehicle;

public class GridLock extends ApplicationAdapter implements InputProcessor, GestureDetector.GestureListener {

    // The Map
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    MapProperties mapProperties;
    int mapWidth;
    int mapHeight;
    int tilePixelWidth;
    int tilePixelHeight;
    public static int mapPixelWidth;
    public static int mapPixelHeight;
    private static String gameState;
    private static boolean levelInProgress;
    private static long lastStateChange;
    private static long lastRunTime;
    private boolean mapDraggable = false;

    // Levels, Stage & Screens:
    public static LevelFactory levelFactory;
    public static Level currentLevel;
    public static Menu menu;
    public static Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private Table table;
    private Table cornerTable;
    private TextButton buttonMenu;
    private Clock clock;

    private SpriteBatch batch;

    // Traffic Control
    TrafficControl trafficControl;
    long timeOfLastRelease;
    int directionOfLastRelease;
    public static boolean holdVehicles;

    // Debugging:
    public static boolean debugging = false;
    private String message = "No gesture performed yet";
    public static BitmapFont font;
    private Pixmap pixmap;
    private ArrayList<Sprite> jSprites = new ArrayList<Sprite>();
    private long lastRenderTime;

    // Profiling:
    private FPSLogger FrameLogger = new FPSLogger();

    public static String getGameState() {
        return GridLock.gameState;
    }

    public static void setGameState(String gameState) {
        GridLock.gameState = gameState;
        GridLock.lastStateChange = System.currentTimeMillis();
        if (gameState.equals("menu")) {
            Gdx.input.setInputProcessor(GridLock.menu.stage);
        } else if (gameState.equals("level")) {
            Gdx.input.setInputProcessor(GridLock.stage);
            holdVehicles = false;
            menu.setVisible(false);
        }
    }

    public static long getLastStateChange() {
        return lastStateChange;
    }

    public static boolean isLevelInProgress() {
        return levelInProgress;
    }

    public static void setLevelInProgress(boolean levelInProgress) {
        GridLock.levelInProgress = levelInProgress;
    }

    public static long getLastRunTime() {
        return lastRunTime;
    }

    public static void setLastRunTime(long lastRunTime) {
        GridLock.lastRunTime = lastRunTime;
    }

    @Override
    public void create () {

        // Renderer:
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Set up camera:
        camera = new OrthographicCamera();
        camera.setToOrtho(false,w,h);
        camera.update();

        // Set up map:
        tiledMap = new TmxMapLoader().load("Map2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        mapProperties = tiledMap.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;

        // Debugging:
        font = new BitmapFont(Gdx.files.internal("roadway.fnt"),false);
        font.setColor(Color.WHITE);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        // Profiling:
        // GLProfiler.enable();

        // Set up multi-touch:
        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);

        batch = new SpriteBatch();

        // Default Level:
        levelFactory = new LevelFactory();
        currentLevel = levelFactory.getLevels().get(0);

        // Traffic Control:
        trafficControl = new TrafficControl();

        // Main Menu:
        menu = new Menu();
        GridLock.setGameState("menu");

        // Init Corner Info Box:
        stage = new Stage();
        atlas = new TextureAtlas("ui/buttons.pack");
        skin = new Skin(atlas);
        cornerTable = new Table(skin);
        cornerTable.setBounds(Gdx.graphics.getWidth() - 325, Gdx.graphics.getHeight() - 65, 300, 40);
        cornerTable.setBackground(skin.getDrawable("infobox"));
        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
        menuButtonStyle.up = skin.getDrawable("home");
        menuButtonStyle.down = skin.getDrawable("home");
        menuButtonStyle.font = font;
        buttonMenu = new TextButton("", menuButtonStyle);
        buttonMenu.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                holdVehicles = true;
                GridLock.setGameState("menu");
                return true;
            }
        });
        Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        cornerTable.add(buttonMenu).right().padLeft(250);
        stage.addActor(cornerTable);
        Gdx.input.setInputProcessor(stage);

        this.run();
    }

    public void run () {
        this.setLastRunTime(System.currentTimeMillis());

        // Vehicles:
        for (Vehicle v : currentLevel.getVehicles()) {

            // Move Vehicles:
            v.move();

            // Release Vehicles:
            if ((v.isReleased() == false) && (System.currentTimeMillis() - timeOfLastRelease > currentLevel.getReleaseFrequency()) && (v.getDirection() != this.directionOfLastRelease)) {
                v.setReleased(true);
                this.timeOfLastRelease = System.currentTimeMillis();
                this.directionOfLastRelease = v.getDirection();
            }

            // Traffic Control: Find each vehicles next junction:
            if (v.getJunction() == null) v.findNextJunction(currentLevel.getJunctions());
        }

        // Traffic Control:
        trafficControl.clearJunctions(currentLevel, batch);
        trafficControl.controlTraffic(currentLevel);
    }

    @Override
    public void render () {

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Vehicles:
        for (Vehicle v : currentLevel.getVehicles()) {

            // Render the vehicles:
            if (v.isReleased()) v.draw(batch);

            // Debugging:
            if (debugging) {

                font.drawMultiLine(batch, v.getId().toString() + ", " + v.getDirection() + ", " + v.isDiverting(), v.getX(), v.getY());
                // if (v.getInitialDirection() == 0) System.out.println(v.getId() + ", " + v.getDirection() + ", " + v.getInitialDirection());

                for (Integer i = 0; i < currentLevel.getJunctions().length; i++) {
                    for (Integer j = 0; j < currentLevel.getJunctions()[i].length; j++) {
                        //GridLock.font.drawMultiLine(batch, currentLevel.getJunctions()[i][j].getDurationOfOccupation().toString(), currentLevel.getJunctions()[i][j].getPosX(), currentLevel.getJunctions()[i][j].getPosY());
                        int k = 0;
                        for (Vehicle vehicle : currentLevel.getJunctions()[i][j].getQueue()) {
                            GridLock.font.drawMultiLine(batch, vehicle.getId().toString(), currentLevel.getJunctions()[i][j].getPosX() + 60, currentLevel.getJunctions()[i][j].getPosY() - 60 - k);
                            k = k + 25;
                        }
                        /*int l = 0;
                        for (JunctionExit exit : currentLevel.getJunctions()[i][j].getExits()) {
                            GridLock.font.drawMultiLine(batch, exit.isOpen().toString(), currentLevel.getJunctions()[i][j].getPosX() + 100, currentLevel.getJunctions()[i][j].getPosY() - 60 - l);
                            l = l + 25;
                        }*/
                    }
                }
            }
        }

        batch.end();

        // Draw RoadWorks:
        if (currentLevel.isRoadWorksLaid() == false) currentLevel.layRoadWorks(stage);
        if (currentLevel.isJunctionRoadWorksLaid() == false) currentLevel.layJunctionRoadWorks(stage);

        // Draw Traffic Cones:
        if (currentLevel.isTrafficConesLaid() == false) currentLevel.layTrafficCones(stage);

        // Draw Clock:

        // Dragging the map:
        if (this.isMapDraggable()) {
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                int deltaMultiplier = 2;
                int minWidth = (Gdx.graphics.getWidth() / 2) + Gdx.input.getDeltaX() * deltaMultiplier;
                int maxWidth = mapPixelWidth - (Gdx.graphics.getWidth() / 2) + Gdx.input.getDeltaX() * deltaMultiplier;
                int minHeight = (Gdx.graphics.getHeight() / 2) - Gdx.input.getDeltaY() * deltaMultiplier;
                int maxHeight = mapPixelHeight - (Gdx.graphics.getHeight() / 2) - Gdx.input.getDeltaY() * deltaMultiplier;
                if ((camera.position.x >= minWidth) && camera.position.x <= maxWidth)
                    camera.translate((int)(-Gdx.input.getDeltaX() * deltaMultiplier), 0);
                if ((camera.position.y >= minHeight) && camera.position.y <= maxHeight)
                    camera.translate(0, (int)(Gdx.input.getDeltaY() * deltaMultiplier));
            }
        }

        // Display "Grid Lock" on game over:
        if (currentLevel.isGridLocked()) {
            currentLevel.displayGridLock(stage);
            if (System.currentTimeMillis() - GridLock.getLastStateChange() > 3000)
                GridLock.setGameState("menu");
        }

        // Draw the stage:
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Draw Hint:
        if (currentLevel.getHint() != null) {
            if (currentLevel.getHint().isVisible() == false) currentLevel.getHint().showHint(font);
        }

        // Kill all levels and Display Menu:
        if (GridLock.getGameState().equals("menu") && menu.isVisible() == false) {
            menu.displayMenu(batch, font);
        }
        if (GridLock.getGameState().equals("menu")) menu.render(0);

        // Profiling:
        //System.out.println(GLProfiler.textureBindings);
        //GLProfiler.reset();

        // Update the game if needed:
        if (System.currentTimeMillis() - this.getLastRunTime() > 20) this.run();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Vehicle v : currentLevel.getVehicles()) {
            v.getTexture().dispose();
        }
        font.dispose();
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT) {
            if (camera.position.x > 320)
                camera.translate(-16,0);
        }
        if(keycode == Input.Keys.RIGHT)
            camera.translate(16,0);
        if(keycode == Input.Keys.UP)
            camera.translate(0,16);
        if(keycode == Input.Keys.DOWN)
            camera.translate(0,-16);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    public long getLastRenderTime() {
        return lastRenderTime;
    }

    public void setLastRenderTime(long lastRenderTime) {
        this.lastRenderTime = lastRenderTime;
    }

    public boolean isMapDraggable() {
        return mapDraggable;
    }

    public void setMapDraggable(boolean mapDraggable) {
        this.mapDraggable = mapDraggable;
    }
}