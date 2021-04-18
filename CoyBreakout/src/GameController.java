package src;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

public class GameController {
	//defining the other objects this class needs an instance of
	InputController inputContr;
	GraphicsController graphicsContr;
	CoyDebug debug;
	CoyFunctions coyFunctions;
	AudioClip batPing,brickBreak,brickPing,wallPing;
	
	double gameVolume = 0.1;
	
	String debugClass = "Game Controller";
	
	GameObj bat,ball,brick;
	ArrayList<GameObj> bricks;
	
	Thread t;
	boolean gameRunning = true;
	boolean gamePaused = false;
	boolean gameEnded = false;
	
	int width,height,flipCooldown,score;
	int flipCooldownMax = 2;
	
	//bat parameters
	int batWidth;
	int batHeight = 10;
	
	//ball parameters
	int ballDiameter = 20;
	Point ballStartingVelocity;
	int ballSpeed;
	
	//brick parameters
	int brickWidth,brickHeight;
	int brickColumns = 6;
	int brickRows = 4;
	int brickOffset = 20;
	int brickMaxHealth = 3;
	
	//Frame Time Calculations
	long lastTime;
	int gameTicksPerSecond = 60;
	//finds out how many nanoseconds have passed per tick
	double nanoSecondPerTick = 1000000000 / gameTicksPerSecond;
	double delta = 0;
	boolean pauseDisplayed = false;
	
	public GameController(int width, int height,CoyDebug debug) {
		this.debug = debug;
		this.width = width;
		this.height = height;
		coyFunctions = debug.coyFunctions;
		batWidth = width/4;
		ballSpeed = 3;
		brickWidth = width/brickColumns;
		brickHeight = (height/2)/brickRows-brickOffset;
		debug.addToDebug(debugClass,"Brick size set at W: "+brickWidth+" H: "+brickHeight);
	}
	
	 public void startGame()
	    {
		 	flipCooldown = 0;
		 	
		 	//loads all the audiofiles into memory
		 	batPing = new AudioClip(getClass().getResource("/resources/batPing.mp3").toString());
		 	brickPing = new AudioClip(getClass().getResource("/resources/brickPing.mp3").toString());
		 	brickBreak = new AudioClip(getClass().getResource("/resources/brickBreak.mp3").toString());
		 	wallPing = new AudioClip(getClass().getResource("/resources/wallPing.mp3").toString());
	        initialiseGame();//Set up all the variables for a new game.
	        graphicsContr.update();
	        t = new Thread( this::gameLoop );     //Starts the gameLoop thread
	        t.setDaemon(true);                          // Tell system this thread can die when it finishes
	        t.start();                               // Start the thread
	        
	    }   
	    
	    // Initialise the game - reset the score and start the movement of the ball
	    public void initialiseGame()
	    {       
	    	score = 0;
	    }
	    
	    public void gameLoop() {
	    	debug.addToDebug(debugClass,"Game Loop Started");
	    	lastTime = System.nanoTime();
	    	while (gameRunning) {
	    		if (!gamePaused) {
	    			long now = System.nanoTime();
		    		delta += (now-lastTime) / nanoSecondPerTick;
		    		lastTime = now;
		    		
		    		while(delta >= 1 ) {
				    	ballUpdate();
				    	graphicsContr.update();
				    	delta--;
		    		}
	    		}
	    		else {
	    			/*keeps the delta ticking over even if nothing is updating.
	    			makes sure all the game operations continue as normal on unpause*/
	    			long now = System.nanoTime();
	    			lastTime = now;
	    			//Display
	    			if (pauseDisplayed && !gameEnded) {
	    				graphicsContr.displayText(width/2,height/2,"PAUSED",50,Color.WHITE);
	    				pauseDisplayed = true;
	    			}
	    			else if (gameEnded) {
	    				graphicsContr.displayText(width/2,height/2,"GAME FINISHED",50,Color.WHITE);
	    			}
	    			
	    		}
	    		
		    	
	    	}
	    	if(!gameRunning) {
	    		System.exit(0);
	    		
	    	}
	    		
	    }
	    
	    void ballUpdate() {
	    	
	    	ball = getBall();
	    	int x = ball.getPosX();
			int y = ball.getPosY();
			int endX = x+ball.width;
			int bottomY = y+ball.height;
			
	    	if (endX >= width|| x <=0) {
	    		
	    		//if it hits the wall at y 0 it would get stuck on the wall. this should fix.
	    		if (y <= 0) {
	    			ball.bounceY();
	    		}
		    		ball.bounceX();
		    		wallPing.play(gameVolume);
		    		debug.addToDebug(debugClass,"Ball Hit Wall");
	    	}

	    	if (ball.overlapping(bat)&&flipCooldown <= 0) {
	    		int batX = bat.x;
    			int batY = bat.y;
    			int batEndX = bat.x+bat.width;
    			int batBottomY = bat.y+bat.height;
    			//Left Side
	    		if (endX >= batX && endX <= batX+(bat.width/10) && y >= batY && flipCooldown <= 0) {
	    			ball.bounceX();
	    			ball.bounceY();
	    			debug.addToDebug(debugClass,"Ball Hit Bat On The Left Side");
	    			batPing.play(gameVolume);
	    			flipCooldown = flipCooldownMax;
	    		}
	    		//Right Side
	    		if (x <= batEndX && x >= batX+(bat.width/10)&& y >= batY && flipCooldown <= 0) {
	    			ball.bounceX();
	    			ball.bounceY();
	    			debug.addToDebug(debugClass,"Ball Hit Bat On The right Side");
	    			batPing.play(gameVolume);
	    			flipCooldown = flipCooldownMax;
	    		}
	    		//Top
	    		if (bottomY >= batY && bottomY <= batBottomY && flipCooldown <= 0) {
	    			ball.bounceY();
	    			debug.addToDebug(debugClass,"Ball Hit Bat On The Top");
	    			batPing.play(gameVolume);
	    			flipCooldown = flipCooldownMax;
	    		}
	    		//Bottom
	    		if (y >= batBottomY && flipCooldown <= 0) {
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
	    				if (coyFunctions.inBounds(x, y,bX,bX+(b.width/10), bY+(b.height/10), bBottomY-(b.height/10))&& flipCooldown <= 0) {
	    					ball.bounceX();
	    					brickPing.play(gameVolume);
	    					b.changeHealth(-1);
	    					debug.addToDebug(debugClass,"Hit left of "+b);
	    					flipCooldown = flipCooldownMax;
	    				}
	    				//Right Side
	    				else if (coyFunctions.inBounds(x, y, bEndX-b.width/10, bEndX, bY+b.height/10, bBottomY-b.height/10) &&flipCooldown <= 0) {
	    					ball.bounceX();
	    					brickPing.play(gameVolume);
	    					b.changeHealth(-1);
	    					debug.addToDebug(debugClass,"Hit right of "+b);
	    					flipCooldown = flipCooldownMax;
	    				}
	    				//Bottom
	    				if (coyFunctions.inBounds(x,y,bX,bEndX,bBottomY-(b.height/10),bBottomY) && flipCooldown <= 0) {
	    					ball.bounceY();
	    					brickPing.play(gameVolume);
	    					b.changeHealth(-1);
	    					debug.addToDebug(debugClass,"Hit bottom of "+b);
	    					flipCooldown = flipCooldownMax;
	    				}
	    				
	    				//top
	    				else if (coyFunctions.inBounds(x, y, bX,bEndX,bY,bY+b.height/10) && flipCooldown <= 0) {
	    					ball.bounceY();
	    					brickPing.play(gameVolume);
	    					b.changeHealth(-1);
	    					debug.addToDebug(debugClass,"Hit bottom of "+b);
	    					flipCooldown = flipCooldownMax;
	    				}
	    			}
	    				
	    		}		
	    		
	    	}
	    	if (ball.getPosY() >= height-(bat.height/2) && flipCooldown <=0) {
	    		ball.resetPos();
	    		debug.addToDebug(debugClass,"Ball Hit Bottom Of Map");
	    		addScore(-10);
	    		flipCooldown = flipCooldownMax;
	    	}
	    	if (ball.getPosY() <= ball.height/2 && flipCooldown <= 0) {
	    		ball.bounceY();
	    		wallPing.play(gameVolume);
	    		debug.addToDebug(debugClass,"Ball Hit Top "+ ball.getVelocity() + " at " + ball.getPos());
	    		flipCooldown = flipCooldownMax;
	    		
	    	}
	    	else {
	    		ball.movePos(ball.getVelocity());
	    		
	    		flipCooldown--;
	    		flipCooldown = (int)coyFunctions.clamp(flipCooldown,0,flipCooldownMax);
	    	}
	    	
	    	if (!anyBricksInPlay()) {
	    		if (!gameEnded) {
	    			ball.velocity = coyFunctions.makePoint(0, 0);
		    		ball.pos = coyFunctions.makePoint(width/2, height/2);
		    		gameEnded = true;
	    		}
	    	}
	    }
	    
	    public boolean anyBricksInPlay() {
	    	for (GameObj b : bricks) {
	    		if (b.getVisible()) {
	    			return true;
	    		}
	    	}
	    	return false;
	    }
	    
	    public GameObj makeBat() {
	    	//makes a bat in the middle of the screen. Coords are from top left for some reason.
	    	debug.addToDebug(debugClass,"Attempting to make bat");
	    	bat = new GameObj((width/2)-(batWidth/2), height-20, batWidth, batHeight,coyFunctions.makePoint(0, 0), Color.RED, debug,false);  
	    	bat.initialize(graphicsContr);
	    	debug.addToDebug(debugClass,"Object Bat added as "+bat);
	    	
	    	return(bat);
	    }
	    
	    public GameObj makeBall() {
	    	debug.addToDebug(debugClass,"Attempting to make ball");
	    	Random random = new Random();
	    	ballStartingVelocity = coyFunctions.makePoint(ballSpeed,ballSpeed);
	    	ball = new GameObj(coyFunctions.clamp(random.nextInt(width),20,width-20),height/2,ballDiameter,ballDiameter,ballStartingVelocity,Color.WHITE,debug,true);
	    	ball.initialize(graphicsContr);
	    	debug.addToDebug(debugClass,"Object Ball added as "+ball+" with velocity "+ball.getVelocity());
			return ball;
	    	}
	    
	    public GameObj makeBrick(int x,int y,int health) {
	    	debug.addToDebug(debugClass,"Attempting to make brick");
	    	brick = new GameObj(x, y, brickWidth, brickHeight, coyFunctions.makePoint(0, 0), Color.RED, debug, false);
	    	brick.initialize(graphicsContr);
	    	brick.gameContr = this;
	    	brick.setHealth(health);
	    	brick.changeHealth(0);
	    
	    	
	    	return brick;
	    }
	    
	    
	    public GameObj getBat() {
	    	
	    	return (bat);
	    }
	    
	    public GameObj getBall() {
	    	
	    	return (ball);
	    }

	    public ArrayList<GameObj> getBricks() {
	    	
	    	return (bricks);
	    }
	    
	    public void addScore(int toAdd) {
	    	score += toAdd;
	    	debug.addToDebug(debugClass,"Total score: " + score);
	    }
	    
	    public int getBrickMaxHealth() {
	    	return brickMaxHealth;
	    }
	    
	    
	    
	    
	    
}
