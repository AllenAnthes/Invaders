package invaders.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Invaders extends Game {

	SpriteBatch batch;
	BitmapFont font;
	public static int SCREEN_WIDTH = 640;
	public static int SCREEN_HEIGHT = 480;

	public void create() {
		batch = new SpriteBatch();

		font = new BitmapFont();
		font.setColor(Color.LIME);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}