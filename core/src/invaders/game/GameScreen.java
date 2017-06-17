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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

import static invaders.game.Invaders.SCREEN_HEIGHT;
import static invaders.game.Invaders.SCREEN_WIDTH;

public class GameScreen implements Screen {
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

    private Random rand;

    private Sound explosion;
    private Sound photon;
    private Sound ufoSound;
    private Sound wallhit;
    private Sound gameoverSound;
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
    private long flashtimer;
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

    private Array<Sprite> enemies;
    private Array<Rectangle> wallList;
    private Array<Sprite> playerShots;
    private Array<Sprite> enemyShots;

    GameScreen(final Invaders game) {
        this.game = game;

        dead = false;
        rand = new Random();

        // load background image
        backgroundImg = new Texture(Gdx.files.internal("space.gif"));
        backgroundImg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // load sound files
        explosion = Gdx.audio.newSound(Gdx.files.internal("sounds/invaderkilled.wav"));
        photon = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));
        ufoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ufo_highpitch.wav"));
        wallhit = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        gameoverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pacman_death.wav"));
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
        wallList = new Array<Rectangle>();
        explosionPartEffect = new ParticleEffect();
        explosionPartEffect.load(Gdx.files.internal("explosionPartEffect.p"), Gdx.files.internal("img"));
        invaderDeathPartEffect = new ParticleEffect();
        invaderDeathPartEffect.load(Gdx.files.internal("invaderdeath.p"), Gdx.files.internal("img"));
        ufoDeathPartEffect = new ParticleEffect();
        ufoDeathPartEffect.load(Gdx.files.internal("ufoDeathPart.p"), Gdx.files.internal("img"));

        addWall(50);
        addWall(200);
        addWall(350);
        addWall(500);

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
        enemies = new Array<Sprite>();
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

        playerShots = new Array<Sprite>();
        enemyShots = new Array<Sprite>();
    }

    private void makeInvincible() {
        invincible = true;
        invincibleTimer = TimeUtils.millis();
        flashtimer = TimeUtils.millis();
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

    private void addWall(int startX) {

        int startY = 70;
        for (int row = 0; row < 35; row ++) {
            for (int column = 0; column < 70; column++) {

                // skip pixels for the gaps to make arch
                if ((row < 10 && column > 10 && column < 60) ||
                    (row < 20 && column > 20 && column < 50) || (row < 30 && column > 30 && column < 40))
                    continue;
                wallList.add(new Rectangle(startX + column, startY + row,1,1
                ));
            }
        }
    }

    /** When called create new laser at player position and add to list  */
    private void playerShoot() {
        Sprite pew = new Sprite(shotUp,10, 15);
        pew.setPosition(playerSprite.getX() + playerSprite.getWidth() / 2 - 4,50);
        playerShots.add(pew);
        photon.play(0.2f);
        lastShotTime = TimeUtils.millis();
    }

    /** When called create new laser at given enemy position and add to list  */
    private void enemyShoot(Sprite enemy) {
        Sprite pew = new Sprite(shotDown, 10, 15);
        pew.setPosition(enemy.getX(), enemy.getY() -5);
        enemyShots.add(pew);
        lastEnemyShot = TimeUtils.millis();
    }
    /** Handle play and enemy shot and wall collisions
     *  TODO: Search could be optimized
     */
    private void wallCollisions(Array<Sprite> spriteArray) {
        for (Sprite sprite : spriteArray) {
            for (Rectangle wall : wallList) {
                if (sprite.getBoundingRectangle().overlaps(wall)) {
                    for (int i = 0; i < wallList.size; i++)
                        // destroy wall pixels in area around hit, more in y direction for sense of upward or downward impact
                        // TODO: Could implement more sophisticated algorithm for blast radius
                        if (Math.abs(wallList.get(i).getX() - wall.getX()) < 6 && Math.abs(wallList.get(i).getY() - wall.getY()) < 8)
                            wallList.removeIndex(i);
                    explosionPartEffect.setPosition(wall.getX(), wall.getY());
                    explosionPartEffect.setDuration(1);
                    explosionPartEffect.start();
                    spriteArray.removeValue(sprite, true);
                    wallList.removeValue(wall, true);
                    wallhit.play(0.2f);
                }
            }
        }
    }
    /** graphics rendering and primary loop function */
    @Override
    public void render(float delta) {

        if (dead && lives < 1) {
            gameoverSound.play(0.2f);
            gameOver = true;
        }

        // recreate level if all enemies killed
        // TODO: Make this more interesting.  Maybe keep walls destroyed?  Faster enemies?
        if (enemies.size == 0 ) {
            ufoSound.stop(); //  stop ufo sound if playing
            game.setScreen(new GameScreen(game));
            gameWinSound.play(0.2f);
        }

        if (gameOver) {
            ufoSound.stop();
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if (dead && lives >= 0) {
            playerHitSound.play();
            dead = false;
            lives -= 1;
            makeInvincible();
        }

        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera matrices and effects
        camera.update();
        explosionPartEffect.update(delta);
        invaderDeathPartEffect.update(delta);
        ufoDeathPartEffect.update(delta);

        // set SpriteBatch render reference coordinate system
        game.batch.setProjectionMatrix(camera.combined);

        // send batch to GPU for rendering
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

        // user input processing
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

        // set user bounds
        if (playerSprite.getX() < 0) playerSprite.setX(0);
        if (playerSprite.getX() > SCREEN_WIDTH - playerSprite.getWidth()) playerSprite.setX(SCREEN_WIDTH - playerSprite.getWidth());

        // handle player shot movement
        wallCollisions(playerShots);
        for (Sprite shot : playerShots) {
            shot.setY(shot.getY() + 400 * Gdx.graphics.getDeltaTime());
            if (shot.getY() > SCREEN_HEIGHT)
                playerShots.removeValue(shot, true);

            // UFO hit
            if (shot.getBoundingRectangle().overlaps(ufo.getBoundingRectangle())) {
                score += 300;
                ufoTimer = TimeUtils.millis();
                explosion.play(0.2f);
                ufoDeathPartEffect.setPosition(ufo.getX(), ufo.getY());
                ufoDeathPartEffect.start();
                ufoSound.pause();
                ufoUP = false;
                ufo.setX(-50);
            }
        }

        // handle enemy shot movement
        wallCollisions(enemyShots);
        for (Sprite shot : enemyShots) {
            shot.setY(shot.getY() - 300 * Gdx.graphics.getDeltaTime());
            if (shot.getY() < 0)
                enemyShots.removeValue(shot, true);
            if (shot.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle()) && !invincible) {
                dead = true;
                enemyShots.removeValue(shot, true);
            }
        }

        // handle player invincibility after being shot
        if (invincible) {
            // expire invincibility after 3 seconds
            if (TimeUtils.millis() - invincibleTimer > 3000) {
                invincible = false;
                playerAlpha = 1;
            }
            // Flash player sprite every .1 seconds
            if (TimeUtils.millis() - flashtimer > 100) {
                if (playerAlpha == 0)   { playerAlpha = 1; }
                else                    { playerAlpha = 0; }
                flashtimer = TimeUtils.millis();
            }
        }
        // enemy animation handling
        int randint;
        for (Sprite enemy : enemies) {
            if (!invincible && (enemy.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle()) || enemy.getY() < 10)) {
                dead = true;
            }

            // randomly pick an enemy to shoot after 0.4 second has passed
            if (TimeUtils.millis() - lastEnemyShot > 400) {
                randint = rand.nextInt(10);
                if (randint == 1)
                    enemyShoot(enemy);
            }

            // check for player shot and enemy collisions
            for (Sprite shot: playerShots)
                if (shot.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                    explosion.play(0.2f);
                    enemies.removeValue(enemy,true);
                    playerShots.removeValue(shot, true);
                    enemyVelocity +=1;
                    killCounter +=1;
                    invaderDeathPartEffect.setPosition(enemy.getX(),enemy.getY());
                    invaderDeathPartEffect.setDuration(1);
                    invaderDeathPartEffect.start();
                    if (enemy.getTexture() == smallInvaderDown || enemy.getTexture() == smallInvaderUp) score += 40;
                    if (enemy.getTexture() == bigInvaderDown || enemy.getTexture() == bigInvaderUp)     score += 30;

            }
            // move enemies
            enemy.setX(enemy.getX() +(2.5f * enemyVelocity + 100) * Gdx.graphics.getDeltaTime() * direction);

            // set final enemy speeds
            if (enemies.size == 1) {
                killCounter = 63;
                enemies.get(0).setX(enemies.get(0).getX() + (340 * Gdx.graphics.getDeltaTime()) * direction);
            }
            else if (enemies.size == 5) {
                killCounter = 56;
                enemyVelocity = 100;
            }

            // reverse direction and set down flag to true if an enemy his the edge
            if (enemy.getX() < 0) {
                direction = 1;
                down = true;
            }
            if (enemy.getX() > SCREEN_WIDTH - enemy.getWidth()) {
                direction = -1;
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
                invaderSound1.play(0.2f);
            else if (invaderSoundTracker == 2)
                invaderSound2.play(0.2f);
            else if (invaderSoundTracker == 3)
                invaderSound3.play(0.2f);
            else if (invaderSoundTracker == 4)
                invaderSound4.play(0.2f);
            timeSinceChange = TimeUtils.millis();
            invaderSoundTracker++;
            if (invaderSoundTracker == 5) invaderSoundTracker = 1;
        }
        // spawn UFO quasi-randomly if 5 seconds has gone by and it is not up
        if (TimeUtils.millis() - ufoTimer > 5000 && !ufoUP) {
            randint = rand.nextInt(500);

            // choose UFO starting side
            if (randint == 1) {
                if (rand.nextInt(2) == 0) {
                    ufo.setPosition(SCREEN_WIDTH - 15, SCREEN_HEIGHT - 40);
                    ufoDirection = -1;
                }
                else {
                    ufo.setPosition(0, SCREEN_HEIGHT - 40);
                    ufoDirection = 1;
                }
                ufoUP = true;
                ufoChangeTime = TimeUtils.millis();
                ufoSound.loop(0.05f);
            }
        }
        // move UFO if up
        if (ufoUP) {
            ufo.setX(ufo.getX() + (ufoDirection * 200) * Gdx.graphics.getDeltaTime());
            if (ufo.getX() + 50 < 0 || ufo.getX() > SCREEN_WIDTH) {
                ufoUP = false;
                ufoTimer = TimeUtils.millis();
                ufo.setX(-50);
                ufoSound.pause();
            }
            // move ufo light every 0.5 seconds
            if (TimeUtils.millis() - ufoChangeTime > 500) {
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
                ufoChangeTime = TimeUtils.millis();     // reset change timer
            }
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
        explosion.dispose();
        photon.dispose();
        shotDown.dispose();
        shotUp.dispose();
        green.dispose();
        wallhit.dispose();
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