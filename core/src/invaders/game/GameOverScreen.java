package invaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import static invaders.game.GameScreen.score;
import static invaders.game.GameScreen.lives;

import static invaders.game.GameScreen.backgroundImg;
import static invaders.game.Invaders.SCREEN_HEIGHT;
import static invaders.game.Invaders.SCREEN_WIDTH;


public class GameOverScreen implements Screen {

    private final Invaders game;
    private Label goLbl;
    private Label scoreLbl;
    private Label againLbl;


    private OrthographicCamera camera;

    GameOverScreen(final Invaders game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // create assorted labels and fonts for game over text
        BitmapFont font32 = new BitmapFont(Gdx.files.internal("fonts/font32.fnt"));
        BitmapFont font24 = new BitmapFont(Gdx.files.internal("fonts/font24.fnt"));
        Label.LabelStyle goStyle = new Label.LabelStyle(font32,Color.LIME);
        Label.LabelStyle scoreStyle = new Label.LabelStyle(font24, Color.LIME);
        goLbl = new Label("Game Over", goStyle);
        int row_height = SCREEN_HEIGHT / 12;
        goLbl.setPosition(SCREEN_WIDTH / 2,SCREEN_HEIGHT / 2 + row_height * 2,Align.center);
        scoreLbl = new Label("Score: " + score,scoreStyle);
        scoreLbl.setPosition(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 + row_height, Align.center);
        againLbl = new Label("Press \"n\" to play again or ENTER to quit", scoreStyle);
        againLbl.setPosition(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, Align.center);
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
        game.batch.draw(backgroundImg,0,0,0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        game.batch.enableBlending();
        goLbl.draw(game.batch,1);
        scoreLbl.draw(game.batch,1);
        againLbl.draw(game.batch, 1);
        game.batch.end();


        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            dispose();
            System.exit(0);       // TODO: HTML deployment doesn't like this for some reason
                                        // TODO: find out why

        }
        if (Gdx.input.isKeyPressed(Input.Keys.N)) {
            game.setScreen(new GameScreen(game));
            lives = 3;
            score = 0;
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
