package ie.michaelodonnell.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ie.michaelodonnell.GridLock;

public class Hint extends Image {
    private boolean visible;
    private Skin skin;
    private TextureAtlas atlas;
    private TextButton closeButton;

    public Hint(Texture texture) {
        super(texture);
        this.setBounds(Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 - 200, 600, 400);
    }

    public void showHint(BitmapFont font) {
        atlas = new TextureAtlas("ui/buttons.pack");
        skin = new Skin(atlas);
        TextButton.TextButtonStyle closeButtonStyle = new TextButton.TextButtonStyle();
        closeButtonStyle.up = skin.getDrawable("close");
        closeButtonStyle.down = skin.getDrawable("close");
        closeButtonStyle.font = font;
        closeButton = new TextButton("", closeButtonStyle);
        closeButton.setPosition(Gdx.graphics.getWidth() / 2 + 220, Gdx.graphics.getHeight() / 2 + 120);
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hideHint();
                return true;
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hideHint();
                return true;
            }
        });
        GridLock.stage.addActor(this);
        GridLock.stage.addActor(closeButton);
        this.setVisible(true);
    }

    private void hideHint() {
        this.remove();
        this.closeButton.remove();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }
}