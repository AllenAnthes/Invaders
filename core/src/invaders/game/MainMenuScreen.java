package invaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import static invaders.game.Invaders.SCREEN_HEIGHT;
import static invaders.game.Invaders.SCREEN_WIDTH;

public class MainMenuScreen implements Screen {

    private final Invaders game;

    private OrthographicCamera camera;
    private Texture titleImage;


    MainMenuScreen(final Invaders game) {

        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        titleImage = new Texture(Gdx.files.internal("spaceinvaders.png"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.disableBlending();
        game.batch.draw(titleImage,0,0);
        game.batch.enableBlending();
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}