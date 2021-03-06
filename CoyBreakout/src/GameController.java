package src;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
/**
 * The Model part of the MVC pattern.
 * In charge of backend game mechanics once the game has started.
 * @author 'Caine' / Joe Benson
 */
public class GameController {
    //Variables
    InputController inputContr;
    GraphicsController graphicsContr;
    CoyDebug debug;
    CoyFunctions coyFunctions;
	/**
	 * An Audio Clip made in audacity by me
	 */
    AudioClip batPing,brickBreak,brickPing,wallPing;
    Thread t;
    
    /**
     * What this class calls itself in debug logs.
     */
    String debugClass = "Game Controller";
    
    //created the game object variables.
    GameObj bat,ball,brick;
    ArrayList<GameObj> bricks;
    
    /**
     *A double controlling game sound volume. 
     */
    double gameVolume = 0.1;
    boolean musicLoaded;
    
    //Game Status Booleans
    boolean gameRunning = true;
    boolean gamePaused = true;
    boolean gameEnded = false;
    
    //defines screen size and game score
    int width,height,score;
    
    /**
     * Used to stop a ball hitting an object more than once a tick.
     */
    int flipCooldown,flipCooldownLeft,flipCooldownRight,flipCooldownTop,flipCooldownBottom;
    /**
     * how many ticks it waits until it is allowed to flip again.
     */
    int flipCooldownMax = 2;
    
    //bat parameters
    int batWidth;
    int batHeight = 10;
    
    //ball parameters
    int ballDiameter = 20;
    Point ballStartingVelocity;
    int ballSpeed = 3;
    
    //Fun end game stuff
    int endBalls = 18;
    ArrayList<GameObj> balls;
    
    //brick parameters
    int brickWidth,brickHeight;
    int brickColumns = 5;
    int brickRows = 4;
    /**
     * controls how much of the screen the bricks take up. the lower the less.
     */
    int brickOffset = 20;
    
    /**
     * when generating bricks, the highest health allowed is this number.
     */
    int brickMaxHealth = 3;
    
    //Frame Time Calculations
    long lastTime;
    /**
     * how many times the game updates per second.
     */
    int gameTicksPerSecond = 60;
    /**
     * finds out how many nanoseconds have passed per tick, 
     */
    double nanoSecondPerTick = 1000000000 / gameTicksPerSecond;
    /**
     * is used to calculate game time.
     */
    double delta = 0;
    
    /**
     * The Model part of the MVC pattern.
     * In charge of backend game mechanics once the game has started.
     * @param width of the screen in int
     * @param height of the screen in int
     * @param debug the debug instance created by main
     */
    public GameController(int width, int height,CoyDebug debug) {
        this.debug = debug;
        this.width = width;
        this.height = height;
        coyFunctions = debug.coyFunctions;
        batWidth = width/4;
        brickWidth = width/brickColumns;
        brickHeight = (height/2)/brickRows-brickOffset;
        debug.addToDebug(debugClass,"Brick size set at W: "+brickWidth+" H: "+brickHeight);
    }
    /**
     * Starts game operations, creating the gameloop thread.
     */
     public void startGame()
        {    
             //sets the cooldown to 0, so it can be ticked up when collisions happen
             
             
             //tries to loads all the audiofiles into memory
         try {
             batPing = new AudioClip(getClass().getResource("/resources/batPing.mp3").toString());
             brickPing = new AudioClip(getClass().getResource("/resources/brickPing.mp3").toString());
             brickBreak = new AudioClip(getClass().getResource("/resources/brickBreak.mp3").toString());
             wallPing = new AudioClip(getClass().getResource("/resources/wallPing.mp3").toString());
             musicLoaded = true;
         }
         catch(Exception e) {
             debug.error(debugClass, e);
             musicLoaded = false;
         }
            initialiseGame();
            //graphicsContr.update();
            t = new Thread( this::gameLoop );     //Starts the gameLoop thread
            t.setDaemon(true);                          // Tell system this thread can die when it finishes
            t.start();                               // Start the thread
            
        }   
        
        /**
         *  Initialise the game - reset the score and start the movement of the ball
         */
        public void initialiseGame()
        {       
            score = 0;
            flipCooldown = 0;
            flipCooldownLeft = 0;
            flipCooldownRight = 0;
            flipCooldownTop = 0;
            flipCooldownBottom = 0;
        }
        
        /**
         * The Main gameloop, runs constantly in a seperate thread
         * 
         */
        
        public void gameLoop(){
            debug.addToDebug(debugClass,"Game Loop Started");
            //gets the time
            lastTime = System.nanoTime();
            while (gameRunning) {
                long now = System.nanoTime();
                delta += (now-lastTime) / nanoSecondPerTick;
                //compares last time and time now and adds to delta. 
                lastTime = now;
            
                /*This runs every game update. Currently set at 60 ticks a second.
                When delta reaches 1 its been one tick and delta gets reset down 1 to theoretically 0*/
                while(delta >= 1 ) {
                    if (!gamePaused) {
                        ballUpdate();
                    }
                       updateGraphics();
                       delta--;
                }
            }
            /*if the game isnt running anymore (you press ESC) the game shuts.
            Different than gameEnded*/
            if(!gameRunning) {
                System.exit(0);
                
            }
                
        }
        
        /**
         * updates the ball position and handles its collision.
         */
        void ballUpdate() {
            ball = getBall();
            int x = ball.getPosX();
            int y = ball.getPosY();
            int endX = x+ball.width;
            int bottomY = y+ball.height;
            int velocityX = ball.getVelocity().x;
            int velocityY = ball.getVelocity().y;
            
            //Fun game ended stuff
            if (gameEnded) {
                for (GameObj b:balls) {
                    b.setPosX((int)((width/2)-10+width/3*Math.cos(Math.toRadians(b.angle))));
                    b.setPosY((int)((height/2)-30+height/3*Math.sin(Math.toRadians(b.angle))));
                    b.setColour(Color.rgb(b.angle/2, b.x/2, b.y/2));
                    if (b.angle < 359) {
                        b.angle+= 5;
                    }
                    else {
                        b.angle = 0;
                    }
                }
                graphicsContr.finishingTextColor= Color.rgb(ball.x/2, ball.y/2, ball.x/2);
                bat.colour = Color.rgb(ball.x/2, ball.y/2, ball.x/2);
                bat.setPosX((int)((width/2)-bat.width/2+width/3*Math.cos(Math.toRadians(ball.angle))));
                bat.setPosY((int)((height/2)-30+height/3*Math.sin(Math.toRadians(ball.angle))));
                
            }
            else {
                //If you hit the wall this makes it bounce
                if (endX >= width || x <=0) {
                    if (flipCooldown <= 0) {
                        if (y <= 0) {
                            //This stops a bug where it would get stuck in the corner hitting both at once.
                            ball.bounceY();
                        }
                        ball.bounceX();
                        playMusic(wallPing);
                        debug.addToDebug(debugClass,"Ball Hit Wall");
                        flipCooldown = flipCooldownMax;
                    }                
                }
                //Controls the bat collisions
                if (ball.overlapping(bat)) {
                    int batX = bat.x;
                    int batY = bat.y;
                    int batEndX = bat.x+bat.width;
                    int batBottomY = bat.y+bat.height;
                    
                    //Left Side
                    if (coyFunctions.inBounds(x, y, batX, batX+bat.width/5, batY, batBottomY)) {
                        ball.bounceX();
                        ball.bounceY();
                        debug.addToDebug(debugClass,"Ball Hit Bat On The Left Side");
                        playMusic(batPing);
                        
                        flipCooldown = flipCooldownMax;
                    }
                    //Right Side
                    else if (coyFunctions.inBounds(endX, y, batEndX-bat.width/5, batBottomY, batY, batBottomY)) {
                        ball.bounceX();
                        ball.bounceY();
                        debug.addToDebug(debugClass,"Ball Hit Bat On The right Side");
                        playMusic(batPing);
                        flipCooldown = flipCooldownMax;
                    }
                    //Top
                    else if (coyFunctions.inBounds(x, bottomY, batX, batEndX, batY, batBottomY-bat.height/5)) {
                        ball.bounceY();
                        debug.addToDebug(debugClass,"Ball Hit Bat On The Top");
                        playMusic(batPing);
                        flipCooldown = flipCooldownMax;
                    }
                    //Bottom
                    else if (coyFunctions.inBounds(x, y, batX, batEndX, batBottomY-bat.height, batBottomY)) {
                        debug.addToDebug(debugClass,"Ball Hit Bat On The Bottom");
                        ball.resetPos();
                        addScore(-10);
                        flipCooldown = flipCooldownMax;
                    }
                    
                    
                }

                for (GameObj b : bricks) {
                    if(b.getVisible()) {
                        int bX = b.x;
                        int bY = b.y;
                        int bEndX = b.x+b.width;
                        int bBottomY = b.y+b.height;
                        
                        if (ball.overlapping(b)) {
                            //Left Side
                            if (coyFunctions.inBounds(endX, y,bX,bX+(b.width/10), bY, bBottomY)&&flipCooldownLeft <= 0 && velocityX > 0) {
                                playMusic(brickPing);
                                b.changeHealth(-1);
                                if (flipCooldownLeft <= 0) {
                                    ball.bounceX();
                                    
                                    flipCooldown = flipCooldownMax;
                                    flipCooldownLeft = flipCooldownMax;
                                    debug.addToDebug(debugClass,"Hit left of "+b.getName());
                                }
                            }
                            //Right Side
                            else if (coyFunctions.inBounds(x, y, bEndX-b.width/10, bEndX, bY, bBottomY)&&flipCooldownRight <= 0 && velocityX < 0) {
                                playMusic(brickPing);
                                b.changeHealth(-1);
                                if (flipCooldownRight <= 0) {
                                    ball.bounceX();
                                    debug.addToDebug(debugClass,"Hit right of "+b.getName());
                                    flipCooldown = flipCooldownMax;
                                    flipCooldownRight = flipCooldownMax;
                                }
                                
                                
                                
                            }
                            //Bottom
                            else if (coyFunctions.inBounds(x,y,bX,bEndX,bBottomY-(b.height/5),bBottomY)&&flipCooldownBottom <= 0 && velocityY < 0) {
                                playMusic(brickPing);
                                b.changeHealth(-1);
                                if (flipCooldownBottom <= 0) {
                                    ball.bounceY();
                                    debug.addToDebug(debugClass,"Hit bottom of "+b.getName());
                                    flipCooldown = flipCooldownMax;
                                    flipCooldownBottom = flipCooldownMax;
                                }         
                            }
                            
                            //top
                            else if (coyFunctions.inBounds(x, bottomY, bX,bEndX,bY,bY+b.height/10) && flipCooldownTop <= 0 && velocityY > 0) {
                                playMusic(brickPing);
                                b.changeHealth(-1);
                                if (flipCooldownTop <= 0) {
                                    ball.bounceY();
                                    debug.addToDebug(debugClass,"Hit top of "+b.getName());
                                    flipCooldown = flipCooldownMax;
                                    flipCooldownTop = flipCooldownMax;
                                }                                           
                            }
                        }
                            
                    }        
                    
                }
                if (y >= height-(bat.height/2) && flipCooldown <=0) {
                    ball.resetPos();
                    debug.addToDebug(debugClass,"Ball Hit Bottom Of Map");

                    addScore(-5);
                    
                    flipCooldown = flipCooldownMax;
                }
                if (y <= 0 && flipCooldown <= 0) {
                    ball.bounceY();
                    playMusic(wallPing);
                    debug.addToDebug(debugClass,"Ball Hit Top "+ ball.getVelocity());
                    flipCooldown = flipCooldownMax;
                    
                }
                else {
                    ball.movePos(ball.getVelocity());
                    
                    flipCooldown--;
                     flipCooldownLeft--;
                    flipCooldownRight--;
                    flipCooldownTop--;
                    flipCooldownBottom--;
                    flipCooldownLeft = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
                    flipCooldownRight = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
                    flipCooldownTop = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
                    flipCooldownBottom = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
                    flipCooldown = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
                }
                
                if (!anyBricksInPlay()) {
                    if (!gameEnded) {
                        ball.velocity = coyFunctions.makePoint(0, 0);
                        ball.setPos(coyFunctions.makePoint(width/2, height/2));
                        gameEnded = true;
                        balls = new ArrayList<GameObj>();
                        balls.add(ball);
                        for (int i = 1;i <= endBalls;i++) {
                            GameObj b = makeBall();
                            b.angle = i*19;
                            balls.add(b);
                            b.velocity = coyFunctions.makePoint(0, 0);
                            b.setPos(coyFunctions.makePoint(width/2, height/2));
                        }
                        
                    }
                }
            }
            
        }
        /**
         * Checks if the music is actually loaded and then plays the clip
         * @param clip Audioclip you want to play.
         */
        public void playMusic(AudioClip clip) {
            if (musicLoaded) {
                clip.play(gameVolume);
            }
        }
        /**
         * Used to end the game.
         * @return if any bricks are visible on the screen
         */
        public boolean anyBricksInPlay() {
            for (GameObj b : bricks) {
                if (b.getVisible()) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Makes a bat in the middle of the screen
         * @return the gameObj of the bat
         */
        public GameObj makeBat() {
            debug.addToDebug(debugClass,"Attempting to make bat");
            this.bat = new GameObj((width/2)-(batWidth/2), height-20, batWidth, batHeight,coyFunctions.makePoint(0, 0), Color.RED, debug,false,"bat");  
            bat.initialize(graphicsContr);
            debug.addToDebug(debugClass,"Object Bat added");
            
            return(bat);
        }
        /**
         * Makes the ball in a random location on the screen.
         * @return the gameobject of the ball
         */
        public GameObj makeBall() {
            debug.addToDebug(debugClass,"Attempting to make ball");
            Random random = new Random();
            ballStartingVelocity = coyFunctions.makePoint(ballSpeed,ballSpeed);
            this.ball = new GameObj(coyFunctions.clamp(random.nextInt(width),20,width-20),height/2,ballDiameter,ballDiameter,ballStartingVelocity,Color.WHITE,debug,true,"ball");
            ball.initialize(graphicsContr);
            debug.addToDebug(debugClass,"Object Ball added with velocity "+ball.getVelocity());
            return ball;
            }
        
        /**
         * Makes a brick at the requested coordinates with the given health
         * @param x int coordinate of the brick
         * @param y int coordinate of the brick
         * @param health int health of the brick
         * @return the brick game object
         */
        public GameObj makeBrick(int x,int y,int health) {
            debug.addToDebug(debugClass,"Attempting to make brick");
            brick = new GameObj(x, y, brickWidth, brickHeight, coyFunctions.makePoint(0, 0), Color.RED, debug, false,"brick");
            brick.initialize(graphicsContr);
            brick.gameContr = this;
            brick.setHealth(health);
            brick.changeHealth(0);
        
            
            return brick;
        }
        
        /**
         * 
         * @return current bat in use by the game
         */
        public GameObj getBat() {
            
            return (bat);
        }
        /**
         * 
         * @return current ball in use by the game
         */
        public GameObj getBall() {
            
            return (ball);
        }
        
        /**
         * 
         * @return the arraylist of bricks.
         */
        public ArrayList<GameObj> getBricks() {
            
            return (bricks);
        }
        
        /**
         * 
         * @param toAdd int score to be added (can be negative for taking away)
         */
        public void addScore(int toAdd) {
            score += toAdd;
            debug.addToDebug(debugClass,"Total score: " + score);
        }
        /**
         * 
         * @return the max health of bricks in the game. set in GameController
         */
        public int getBrickMaxHealth() {
            return brickMaxHealth;
        }
        /**
         * runs the graphics updater on the same thread as the gameloop method.
         */
        public synchronized void updateGraphics() {
            Platform.runLater(graphicsContr::update);
        }
        
        
        
        
        
        
}
