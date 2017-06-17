package invaders.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import invaders.game.Invaders;

import static invaders.game.Invaders.SCREEN_HEIGHT;
import static invaders.game.Invaders.SCREEN_WIDTH;

// TODO: All messed up on HTML.  Enemy lasers are black.  Game crashes on game over.  At least it compiles now.

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Invaders();
        }
}