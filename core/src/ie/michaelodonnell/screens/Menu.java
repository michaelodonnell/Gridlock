package ie.michaelodonnell.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ie.michaelodonnell.GridLock;
import ie.michaelodonnell.LevelFactory;
import ie.michaelodonnell.simulation.TrafficCone;

public class Menu implements Screen {
    public Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private Table table;
    private TextButton buttonL1, buttonL2, buttonL3, buttonL4, buttonL5, buttonL6, buttonL7, buttonL8, buttonL9, buttonL10, buttonL11, buttonL12, buttonResume, buttonExit;
    private BitmapFont font;
    private BitmapFont fontBlack;
    private Sprite background;

    private boolean visible;
    int menuX;
    int menuY;

    /*public Menu (BitmapFont font) {
        this.font = font;
    }*/

    public void displayMenu(SpriteBatch batch, BitmapFont font) {
        menuX = Gdx.graphics.getWidth() / 2 - 300;
        menuY = Gdx.graphics.getHeight() / 2 - 200;
        fontBlack = new BitmapFont(Gdx.files.internal("roadway_black.fnt"),false);

        // Display Background:
        //background = new Sprite(new Texture(Gdx.files.internal("menu.png")));

        stage = new Stage();
        atlas = new TextureAtlas("ui/buttons.pack");
        skin = new Skin(atlas);

        table = new Table(skin);
        table.setBounds(menuX, menuY, 600, 400);
        table.setBackground(skin.getDrawable("bg"));

        TextButton.TextButtonStyle blankButtonStyle = new TextButton.TextButtonStyle();
        blankButtonStyle.up = skin.getDrawable("blank");
        blankButtonStyle.down = skin.getDrawable("blank");
        blankButtonStyle.font = font;

        TextButton.TextButtonStyle unlockedButtonStyle = new TextButton.TextButtonStyle();
        unlockedButtonStyle.up = skin.getDrawable("unlock");
        unlockedButtonStyle.down = skin.getDrawable("unlock");
        unlockedButtonStyle.font = fontBlack;

        TextButton.TextButtonStyle lockedButtonStyle = new TextButton.TextButtonStyle();
        lockedButtonStyle.up = skin.getDrawable("lock");
        lockedButtonStyle.down = skin.getDrawable("lock");
        lockedButtonStyle.font = font;

        buttonL1 = new TextButton("level 1", unlockedButtonStyle);
        buttonL1.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GridLock.levelFactory = new LevelFactory();
                GridLock.currentLevel = GridLock.levelFactory.getLevels().get(1);
                GridLock.setGameState("level");
                GridLock.setLevelInProgress(true);
                Gdx.input.setInputProcessor(GridLock.stage);
                return true;
            }
        });

        buttonL2 = new TextButton("", lockedButtonStyle);
        buttonL2.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        buttonL3 = new TextButton("", lockedButtonStyle);
        buttonL3.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("3");
                return true;
            }
        });

        buttonL4 = new TextButton("", lockedButtonStyle);
        buttonL4.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("4");
                return true;
            }
        });

        buttonL5 = new TextButton("", lockedButtonStyle);
        buttonL5.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("5");
                return true;
            }
        });

        buttonL6 = new TextButton("", lockedButtonStyle);
        buttonL6.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("6");
                return true;
            }
        });

        buttonL7 = new TextButton("", lockedButtonStyle);
        buttonL7.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("7");
                return true;
            }
        });

        buttonL8 = new TextButton("", lockedButtonStyle);
        buttonL8.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("8");
                return true;
            }
        });

        buttonL9 = new TextButton("", lockedButtonStyle);
        buttonL9.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("9");
                return true;
            }
        });

        buttonL10 = new TextButton("", lockedButtonStyle);
        buttonL10.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("10");
                return true;
            }
        });

        buttonL11 = new TextButton("", lockedButtonStyle);
        buttonL11.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("11");
                return true;
            }
        });

        buttonL12 = new TextButton("", lockedButtonStyle);
        buttonL12.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("12");
                return true;
            }
        });

        buttonResume = new TextButton("RESUME", blankButtonStyle);
        buttonResume.pad(20);
        buttonResume.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GridLock.setGameState("level");
                return true;
            }
        });

        buttonExit = new TextButton("EXIT", blankButtonStyle);
        buttonExit.pad(20);
        buttonExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });

        table.padTop(150);
        table.row().padBottom(10);
        table.add(buttonL1);
        table.add(buttonL2);
        table.add(buttonL3);
        table.add(buttonL4);
        table.add(buttonL5);
        table.add(buttonL6);
        table.row().padBottom(10);
        table.add(buttonL7);
        table.add(buttonL8);
        table.add(buttonL9);
        table.add(buttonL10);
        table.add(buttonL11);
        table.add(buttonL12);
        table.row();
        if(GridLock.isLevelInProgress()) {
            table.add(buttonResume).colspan(3).left();
            table.add(buttonExit).colspan(3).right();
        } else {
            table.add(buttonExit).colspan(6).right();
        }

        // table.debug();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        this.setVisible(true);
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
