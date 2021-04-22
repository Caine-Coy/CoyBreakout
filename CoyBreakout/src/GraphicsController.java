package src;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class GraphicsController implements EventHandler<KeyEvent> {
	
	int width,height;
	//defining the other objects this class needs an instance of
	InputController inputContr;
	GameController gameContr;
	CoyDebug debug;
	CoyFunctions coyFunctions;
	String debugClass = "Graphics Controller";
	Stage window;
	
	GraphicsContext gc;
	Canvas canvas;
	
	GameObj bat,ball,brick;
	ArrayList<GameObj> bricks;
	int brickHealth;
	long tick;
	
	
	
	public GraphicsController(int w,int h,CoyDebug debug) {
		this.width = w;
		this.height = h;
		this.debug = debug;
		this.coyFunctions = debug.coyFunctions;
	}
	
	public void start(Stage window) {
		
		//set the window title
	     window.setTitle("Breakout");
	     this.window = window;
	     //this creates a canvas for pixels to be drawn on.  
	     Canvas canvas = new Canvas (height,width);
	     gc = canvas.getGraphicsContext2D();
	     //tells the game controller it is ready to make a bat.
	     bat = gameContr.makeBat();
	     ball = gameContr.makeBall();
	     
	     //makes bricks
	     brickHealth = gameContr.getBrickMaxHealth();
	     bricks = new ArrayList<GameObj>();
	     Random rand = new Random();
	     for (int x = 0; x < gameContr.brickColumns;x++) {
	    	 for (int y= 0; y <gameContr.brickRows;y++) {
	    		 brick = gameContr.makeBrick((x)+x*gameContr.brickWidth+x,y*gameContr.brickHeight+y,coyFunctions.clamp(rand.nextInt(brickHealth+1), 1, brickHealth));
	    		 bricks.add(brick);
	    	 }
	     }
	     
	     gameContr.bricks = bricks;   
	     Group root = new Group(canvas);
	     Scene gameScene = new Scene (root);
	     window.setScene(gameScene);
	     
	     //display
	     window.show();
	     gameScene.setOnKeyPressed(this);
	     debug.addToDebug(debugClass,"Window Initialized");
	}
	
	 public void handle(KeyEvent event)
	    {
	        // send the event to the controller
	        inputContr.userKeyInteraction( event );
	    }
	
	public void update() {
		synchronized(gameContr) {
			
			updateScreen();
			updateUI();
			tick++;
		}
		
	}
	
	public void updateUI() {
		
			String scoreText = "Score: "+gameContr.score;
			displayText(width/2,height/2,scoreText,15,Color.WHITE);
			
		}
	
	public synchronized void updateScreen() {

			//set the background black from 0,0 to max 
		gc.setFill(Color.BLACK);
		gc.fillRect(0,0,width,height);
		
		displayRectObj(bat);
	    displayRoundObj(ball);
	   
	    for(GameObj b : bricks) {
	    	if (b.getVisible()) {
	    		displayRectObj(b);
	    	}
	    	
	    }
	    
	}
	
	public void displayText(int x,int y, String text,double fontSize, Color colour) {
		gc.setFont(new Font(fontSize));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFill(colour);
		gc.fillText(text,x,y);
		
	}

	public void displayRectObj(GameObj go) {
		gc.setFill(go.colour);
		gc.fillRect(go.x,go.y,go.width,go.height);
	}
	public void displayRoundObj(GameObj go) {
		gc.setFill(go.colour);
		gc.fillOval(go.x,go.y,go.width,go.height);
	}

	public Point moveObj(GameObj go,Point point){
		//gc.setFill(go.colour);
		Translate translate = go.getTranslate(); 
		translate.translate(point.x,point.y);
		
		return point;
	}
	
	

}
