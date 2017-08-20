package invaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.*;

import static invaders.game.Invaders.SCREEN_HEIGHT;
import static invaders.game.Invaders.SCREEN_WIDTH;

public class GameScreen implements Screen {

    private static final int MILLIS_BETWEEN_ENEMY_SHOTS = 400;
    private static final int ENEMY_PLAYER_OVERLAP_HEIGHT = 10;
    private static final int KILL_COUNTER_HIGH = 63;
    private static final int KILL_COUNTER_MEDIUM = 56;
    private static final int ENEMY_HIGH_VELOCITY = 100;
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int INVINCIBILITY_TIME = 3000;
    private static final int FLASH_TIME = 100;
    private static final int MILLIS_BETWEEN_UFO_SPAWNS = 5000;
    private static final int RANDOM_UFO_SPAWN_CHANCE = 500;
    private static final float LOW_VOLUME = 0.2f;
    private static final float MEDIUM_VOLUME = 0.05f;
    private final Invaders game;

    static int score = 0;
    private OrthographicCamera camera;
    static int lives = 3;
    private int direction;
    private int ufoDirection;
    private int enemyVelocity;
    private int shootSpeed;
    private int playerAlpha;
    private int killCounter;

    private boolean ufoUP;
    private boolean down;
    private boolean dead;
    private boolean invincible;
    private boolean gameOver;

    private Sound explosionSound;
    private Sound playerShotSound;
    private Sound ufoSound;
    private Sound wallHitSound;
    private Sound gameOverSound;
    private Sound playerHitSound;
    private Sound invaderSound1;
    private Sound invaderSound2;
    private Sound invaderSound3;
    private Sound invaderSound4;
    private Sound gameWinSound;

    private long lastShotTime;
    private long lastEnemyShot;
    private long timeSinceChange;
    private long ufoTimer;
    private long ufoChangeTime;
    private long invincibleTimer;
    private long flashTimer;
    private long invaderSoundTracker;

    private Texture bigInvaderDown;
    private Texture bigInvaderUp;
    private Texture smallInvaderDown;
    private Texture smallInvaderUp;
    private Texture ufo0;
    private Texture ufo1;
    private Texture ufo2;
    private Texture ufo3;
    private Texture ufo4;

    static Texture backgroundImg;
    private Texture green;
    private Texture playerGIF;
    private Texture shotUp;
    private Texture shotDown;

    private Sprite ufo;
    private Sprite bigInvader;
    private Sprite smallInvader;
    private Sprite playerSprite;

    private ParticleEffect explosionPartEffect;
    private ParticleEffect invaderDeathPartEffect;
    private ParticleEffect ufoDeathPartEffect;

    private ArrayList<Sprite> enemies;
    private List<Rectangle> wallList;
    private List<Sprite> playerShots;
    private List<Sprite> enemyShots;

    GameScreen(final Invaders game) {
        this.game = game;

        dead = false;
        // load background image
        backgroundImg = new Texture(Gdx.files.internal("space.gif"));
        backgroundImg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // load sound files
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/invaderkilled.wav"));
        playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));
        ufoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ufo_highpitch.wav"));
        wallHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pacman_death.wav"));
        playerHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/atari_boom5.wav"));
        invaderSound1 = Gdx.audio.newSound(Gdx.files.internal("sounds/invader1.wav"));
        invaderSound2 = Gdx.audio.newSound(Gdx.files.internal("sounds/invader2.wav"));
        invaderSound3 = Gdx.audio.newSound(Gdx.files.internal("sounds/invader3.wav"));
        invaderSound4 = Gdx.audio.newSound(Gdx.files.internal("sounds/invader4.wav"));
        gameWinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gamewin.wav"));

        // load the image for the player
        playerGIF = new Texture(Gdx.files.internal("player2.gif"));
        playerAlpha = 1;
        shotUp = new Texture(Gdx.files.internal("shot1.gif"));
        shotDown = new Texture(Gdx.files.internal("shot2.gif"));
        lastShotTime = TimeUtils.millis();

        // wall files
        green = new Texture(Gdx.files.internal("green.png"));
        wallList = new ArrayList<>();
        explosionPartEffect = new ParticleEffect();
        explosionPartEffect.load(Gdx.files.internal("explosionPartEffect.p"), Gdx.files.internal("img"));
        invaderDeathPartEffect = new ParticleEffect();
        invaderDeathPartEffect.load(Gdx.files.internal("invaderdeath.p"), Gdx.files.internal("img"));
        ufoDeathPartEffect = new ParticleEffect();
        ufoDeathPartEffect.load(Gdx.files.internal("ufoDeathPart.p"), Gdx.files.internal("img"));

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Load invader images and initialize in down state
        bigInvaderDown = new Texture(Gdx.files.internal("invader3.gif"));
        bigInvaderUp = new Texture(Gdx.files.internal("invader4.gif"));
        smallInvaderDown = new Texture(Gdx.files.internal("invader1.gif"));
        smallInvaderUp = new Texture(Gdx.files.internal("invader2.gif"));
        bigInvader = new Sprite(bigInvaderDown);
        smallInvader = new Sprite(smallInvaderDown);
        timeSinceChange = TimeUtils.millis();
        direction = 1;
        down = false;

        // initialize ufo textures and sprite
        ufo0 = new Texture(Gdx.files.internal("ufo0.gif"));
        ufo1 = new Texture(Gdx.files.internal("ufo1.gif"));
        ufo2 = new Texture(Gdx.files.internal("ufo2.gif"));
        ufo3 = new Texture(Gdx.files.internal("ufo3.gif"));
        ufo4 = new Texture(Gdx.files.internal("ufo4.gif"));
        ufo = new Sprite(ufo0);
        ufoUP = false;

        // start ufo timer at -4 seconds
        ufoTimer = TimeUtils.millis() - 4000;
        invaderSoundTracker = 1;

        // spawn initial enemies
        enemies = new ArrayList<>();
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 12; column++) {
                if (row < 3) spawnBigInvader(150 + column * 30, 350 + row * 25);
                else spawnSmallInvader(150 + column * 30, 350 + row * 25);
            }
        }
        // initialize velocity multiplier to 1
        enemyVelocity = 1;
        killCounter = 0;

        // spawn player
        playerSprite = new Sprite(playerGIF);
        playerSprite.setCenter(SCREEN_WIDTH / 2, 25);
        gameOver = false;
        shootSpeed = 350;
        playerShots = new ArrayList<>();
        enemyShots = new ArrayList<>();
        addWalls();
    }

    //TODO: Get rid of magic numbers here.  This is also still ugly as hell
    private void addWalls() {
        int[] starts = {50, 200, 350, 500};
        for (int startX : starts) {
            int startY = 70;
            for (int row = 0; row < 35; row++) {
                for (int column = 0; column < 70; column++) {
                    // skip pixels for the gaps to make arch
                    if ((row < 10 && column > 10 && column < 60) ||
                            (row < 20 && column > 20 && column < 50) || (row < 30 && column > 30 && column < 40))
                        continue;
                    wallList.add(new Rectangle(startX + column, startY + row, 1, 1));
                }
            }
        }
    }

    private void spawnBigInvader(int xPos, int yPos) {
        bigInvader = new Sprite(bigInvaderDown, 20, 20);
        bigInvader.setPosition(xPos, yPos);
        enemies.add(bigInvader);
    }

    private void spawnSmallInvader(int xPos, int yPos) {
        smallInvader = new Sprite(smallInvaderDown, 20, 20);
        smallInvader.setPosition(xPos,yPos);
        enemies.add(smallInvader);
    }

    //graphics rendering and primary loop function
    @Override
    public void render(float delta) {

        checkIfGameOver();
        renderingSetup(delta);
        renderImages();
        processUserInput();
        checkPlayerBounds();
        handlePlayerShots();
        handleEnemyShots();
        checkInvincibility();
        animateEnemies();
        handleUFO();
    }

    private void checkIfGameOver() {
        if (dead && lives < 1) {
            gameOverSound.play(LOW_VOLUME);
            gameOver = true;
        }

        // TODO: Make this more interesting.  Maybe keep walls destroyed?  Faster enemies?  Enemies shoot faster?
        if (enemies.size() == 0 ) {
            ufoSound.stop(); //  stop ufo sound if playing
            game.setScreen(new GameScreen(game));
            gameWinSound.play(LOW_VOLUME);
        }

        if (gameOver) {
            ufoSound.stop();
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if (dead && lives >= 0) {
            playerHitSound.play(LOW_VOLUME);
            dead = false;
            lives -= 1;
            makeInvincible();
        }
    }
    private void makeInvincible() {
        invincible = true;
        invincibleTimer = TimeUtils.millis();
        flashTimer = TimeUtils.millis();
    }

    private void renderingSetup(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        explosionPartEffect.update(delta);
        invaderDeathPartEffect.update(delta);
        ufoDeathPartEffect.update(delta);
        game.batch.setProjectionMatrix(camera.combined);
    }

    private void renderImages() {
        game.batch.begin();
        game.batch.disableBlending();
        game.batch.draw(backgroundImg, 0,0,0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
        game.batch.enableBlending();
        game.font.draw(game.batch, "Current Score: " + score, 0, SCREEN_HEIGHT - 20);
        game.font.draw(game.batch, "Lives Remaining: " + lives, 0, SCREEN_HEIGHT - 40);
        if (playerAlpha != 0 || !invincible)
            game.batch.draw(playerSprite,playerSprite.getX(), playerSprite.getY(), 32, 20);
        for (Sprite enemy : enemies)
            game.batch.draw(enemy, enemy.getX(), enemy.getY());
        for (Sprite shot : playerShots)
            game.batch.draw(shot, shot.getX(), shot.getY());
        for (Sprite shot : enemyShots)
            game.batch.draw(shot, shot.getX(), shot.getY());
        for (Rectangle wall : wallList)
            game.batch.draw(green,wall.getX(), wall.getY(),1,1);
        explosionPartEffect.draw(game.batch);
        invaderDeathPartEffect.draw(game.batch);
        ufoDeathPartEffect.draw(game.batch);
        if (ufoUP)
            game.batch.draw(ufo,ufo.getX(), ufo.getY());
        game.batch.end();
    }

    private void processUserInput() {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) playerSprite.setX(playerSprite.getX() - 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) playerSprite.setX(playerSprite.getX() + 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            if (TimeUtils.millis() - lastShotTime > shootSpeed) playerShoot();
        }
        if (Gdx.input.isKeyPressed(Keys.ENTER)) gameOver = true;
        // activate debug/cheat mode
        if (Gdx.input.isKeyPressed(Keys.F1)) shootSpeed = 200;
        if (Gdx.input.isKeyPressed(Keys.F2)) shootSpeed = 100;
        if (Gdx.input.isKeyPressed(Keys.F3)) shootSpeed = 50;
        if (Gdx.input.isKeyPressed(Keys.F8)) lives = 10;
    }

    private void playerShoot() {
        Sprite pew = new Sprite(shotUp,10, 15);
        pew.setPosition(playerSprite.getX() + playerSprite.getWidth() / 2 - 4,50);
        playerShots.add(pew);
        playerShotSound.play(LOW_VOLUME);
        lastShotTime = TimeUtils.millis();
    }

    private void enemyShoot(Sprite enemy) {
        Sprite pew = new Sprite(shotDown, 10, 15);
        pew.setPosition(enemy.getX(), enemy.getY() -5);
        enemyShots.add(pew);
        lastEnemyShot = TimeUtils.millis();
    }

    private void checkPlayerBounds() {
        if (playerSprite.getX() < 0) playerSprite.setX(0);
        if (playerSprite.getX() > SCREEN_WIDTH - playerSprite.getWidth()) playerSprite.setX(SCREEN_WIDTH - playerSprite.getWidth());
    }

    private void handleUFO() {
        // spawn UFO quasi-randomly if 5 seconds has gone by and it is not up
        if (TimeUtils.millis() - ufoTimer > MILLIS_BETWEEN_UFO_SPAWNS && !ufoUP) {
            // choose UFO starting side
            if (new Random().nextInt(RANDOM_UFO_SPAWN_CHANCE) == 1) {
                if (new Random().nextInt(2) == 0) {
                    ufo.setPosition(SCREEN_WIDTH - 15, SCREEN_HEIGHT - 40);
                    ufoDirection = -1;
                }
                else {
                    ufo.setPosition(0, SCREEN_HEIGHT - 40);
                    ufoDirection = 1;
                }
                ufoUP = true;
                ufoChangeTime = TimeUtils.millis();
                ufoSound.loop(MEDIUM_VOLUME);
            }
        }
        if (ufoUP)
            animateUFO();
    }

    private void animateUFO() {
        ufo.setX(ufo.getX() + (ufoDirection * 200) * Gdx.graphics.getDeltaTime());
        if (ufo.getX() + 50 < 0 || ufo.getX() > SCREEN_WIDTH) {
            ufoUP = false;
            ufoTimer = TimeUtils.millis();
            ufo.setX(-50);
            ufoSound.pause();
        }
        // move ufo light every 0.1 seconds
        if (TimeUtils.millis() - ufoChangeTime > 100) {
            if (ufo.getTexture() == ufo0)
                ufo.setTexture(ufo1);
            else if (ufo.getTexture() == ufo1)
                ufo.setTexture(ufo2);
            else if (ufo.getTexture() == ufo2)
                ufo.setTexture(ufo3);
            else if (ufo.getTexture() == ufo3)
                ufo.setTexture(ufo4);
            else if (ufo.getTexture() == ufo4)
                ufo.setTexture(ufo0);
            // reset change timer
            ufoChangeTime = TimeUtils.millis();
        }
    }

    private void handlePlayerShots() {
        Iterator<Sprite> iter = playerShots.iterator();
        while(iter.hasNext()) {
            Sprite shot = iter.next();
            shot.setY(shot.getY() + MILLIS_BETWEEN_ENEMY_SHOTS * Gdx.graphics.getDeltaTime());
            if (shot.getY() > SCREEN_HEIGHT) {
                iter.remove();
                return;
            }
            // check for enemy hits
            if (enemies.removeIf(enemy -> shot
                    .getBoundingRectangle()
                    .overlaps(enemy.getBoundingRectangle()))) {
                enemyHit(shot);
                iter.remove();
                return;
            }
            // check for wall hits
            if (wallList.removeIf(wall -> Math.abs(shot.getX() - wall.getX()) < 8 &&
                                            Math.abs(shot.getY() - wall.getY()) < 9)) {
                wallHit(shot);
                iter.remove();
                return;
            }
            // check for UFO hit
            if (shot.getBoundingRectangle().overlaps(ufo.getBoundingRectangle())) {
                ufoHit();
                iter.remove();
            }
        }
    }

    private void enemyHit(Sprite shot) {
        explosionSound.play(LOW_VOLUME);
        enemyVelocity +=1;
        killCounter +=1;
        invaderDeathPartEffect.setPosition(shot.getX() + shot.getWidth() / 2,shot.getY() + shot.getHeight() / 2);
        invaderDeathPartEffect.setDuration(1);
        invaderDeathPartEffect.start();
        score += 30;
    }

    private void wallHit(Sprite sprite) {
        explosionPartEffect.setPosition(sprite.getX(), sprite.getY());
        explosionPartEffect.setDuration(1);
        explosionPartEffect.start();
        wallHitSound.play(LOW_VOLUME);
    }

    private void ufoHit() {
        score += 300;
        ufoTimer = TimeUtils.millis();
        explosionSound.play(LOW_VOLUME);
        ufoDeathPartEffect.setPosition(ufo.getX(), ufo.getY());
        ufoDeathPartEffect.start();
        ufoSound.pause();
        ufoUP = false;
        ufo.setX(-50);
    }

    private void handleEnemyShots() {
        Iterator<Sprite> iter = enemyShots.iterator();
        while(iter.hasNext()) {
            Sprite shot = iter.next();
            shot.setY(shot.getY() - 300 * Gdx.graphics.getDeltaTime());
            if (shot.getY() < 0)
                iter.remove();
            else if (shot.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle()) && !invincible) {
                dead = true;
                iter.remove();
            }
            else if (wallList.removeIf(wall -> (Math.abs(shot.getX() - wall.getX()) < 8) &&
                                               (Math.abs(shot.getY() - wall.getY()) < 9))) {
                wallHit(shot);
                iter.remove();
                return;
            }
        }
    }

    private void checkInvincibility() {
        if (invincible) {
            // expire invincibility after 3 seconds
            if (TimeUtils.millis() - invincibleTimer > INVINCIBILITY_TIME) {
                invincible = false;
                playerAlpha = 1;
            }
            // Flash player sprite every .1 seconds
            if (TimeUtils.millis() - flashTimer > FLASH_TIME) {
                if (playerAlpha == 0)   { playerAlpha = 1; }
                else                    { playerAlpha = 0; }
                flashTimer = TimeUtils.millis();
            }
        }
    }

    private void animateEnemies() {

        for (Sprite enemy : enemies) {
            if (!invincible && (enemy.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle()) || enemy.getY() < ENEMY_PLAYER_OVERLAP_HEIGHT)) {
                dead = true;
            }

            // randomly pick an enemy to shoot after a certain time has passed
            if (TimeUtils.millis() - lastEnemyShot > MILLIS_BETWEEN_ENEMY_SHOTS) {
                if (new Random().nextInt(10) == 1)
                    enemyShoot(enemy);
            }

            // move enemies
            enemy.setX(enemy.getX() +(2.5f * enemyVelocity + 100) * Gdx.graphics.getDeltaTime() * direction);

            // set final enemy speeds
            if (enemies.size() == 1) {
                killCounter = KILL_COUNTER_HIGH;
                enemies.get(0).setX(enemies.get(0).getX() + (340 * Gdx.graphics.getDeltaTime()) * direction);
            }
            else if (enemies.size() == 5) {
                killCounter = KILL_COUNTER_MEDIUM;
                enemyVelocity = ENEMY_HIGH_VELOCITY;
            }

            // reverse direction and set down flag to true if an enemy his the edge
            if (enemy.getX() < 0) {
                direction = RIGHT;
                down = true;
            }
            if (enemy.getX() > SCREEN_WIDTH - enemy.getWidth()) {
                direction = LEFT;
                down = true;
            }
        }
        // move enemies down if down flag is set
        if (down) {
            for (Sprite enemy : enemies) {
                enemy.setY(enemy.getY() - 10);
                if (enemy.getY() < 10) {
                    lives = 0;
                    dead = true;
                }
            }
            down = false;
        }
        // switch enemy image
        if (TimeUtils.millis() - timeSinceChange > 1500 - (killCounter * 23)) {
            for (Sprite enemy : enemies) {
                if (enemy.getTexture() == bigInvaderUp)         enemy.setTexture(bigInvaderDown);
                else if (enemy.getTexture() == bigInvaderDown)  enemy.setTexture(bigInvaderUp);
                else if (enemy.getTexture()== smallInvaderUp)   enemy.setTexture(smallInvaderDown);
                else                                            enemy.setTexture(smallInvaderUp);
            }
            if (invaderSoundTracker == 1)
                invaderSound1.play(LOW_VOLUME);
            else if (invaderSoundTracker == 2)
                invaderSound2.play(LOW_VOLUME);
            else if (invaderSoundTracker == 3)
                invaderSound3.play(LOW_VOLUME);
            else if (invaderSoundTracker == 4)
                invaderSound4.play(LOW_VOLUME);
            timeSinceChange = TimeUtils.millis();
            invaderSoundTracker++;
            if (invaderSoundTracker == 5) invaderSoundTracker = 1;
        }
    }

    @Override
    public void show() {
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
        playerGIF.dispose();
        smallInvaderDown.dispose();
        smallInvaderUp.dispose();
        bigInvaderDown.dispose();
        bigInvaderDown.dispose();
        explosionSound.dispose();
        playerShotSound.dispose();
        shotDown.dispose();
        shotUp.dispose();
        green.dispose();
        wallHitSound.dispose();
        ufo0.dispose();
        ufo1.dispose();
        ufo2.dispose();
        ufo3.dispose();
        ufo4.dispose();
        playerHitSound.dispose();
        invaderSound1.dispose();
        ufoSound.dispose();
    }
}