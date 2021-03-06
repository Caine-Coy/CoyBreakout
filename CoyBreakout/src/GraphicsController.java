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
/**
 * The View part of the MVC
 * @author 'Caine'/ Joe Benson
 *
 */
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
    /**
     * The arraylist of all the brick GameObjs
     */
    ArrayList<GameObj> bricks;
    int brickHealth;
    long tick;
    
    //Fun End Game Stuff
    Color finishingTextColor = Color.PURPLE;
    
    /**
     * 
     * @param w int width of screen
     * @param h int height of screen
     * @param debug instance from the parent object.
     */
    public GraphicsController(int w,int h,CoyDebug debug) {
        this.width = w;
        this.height = h;
        this.debug = debug;
        this.coyFunctions = debug.coyFunctions;
    }
    
    /**
     * JavaFX start object
     * @param The stage you want to start the game rendering in.
     */
    public void start(Stage window) {
        
        //set the window title
         window.setTitle("Breakout");
         this.window = window;
         //this creates a canvas for pixels to be drawn on.  
         Canvas canvas = new Canvas (width,height);
         gc = canvas.getGraphicsContext2D();
         //tells the game controller it is ready to make a bat.
         startGame();
         Group root = new Group(canvas);
         Scene gameScene = new Scene (root);
         window.setScene(gameScene);
         
         //display
         window.show();
         gameScene.setOnKeyPressed(this);
         debug.addToDebug(debugClass,"Window Initialized");
    }
    /**
     * starts the game and clears up all the old instances of game objects. Called on first start and new game.
     */
    public void startGame() {
         bat = null;
         ball = null;
         bat = gameContr.makeBat();
         ball = gameContr.makeBall();
         
         //makes bricks
         brickHealth = gameContr.getBrickMaxHealth();
         bricks = null;
         bricks = new ArrayList<GameObj>();
         Random rand = new Random();
         for (int x = 0; x < gameContr.brickColumns;x++) {
             for (int y= 0; y <gameContr.brickRows;y++) {
                 brick = gameContr.makeBrick((x)+x*gameContr.brickWidth,y*gameContr.brickHeight+y,coyFunctions.clamp(rand.nextInt(brickHealth+1), 1, brickHealth));
                 brick.brickArrayX = x;
                 brick.brickArrayY = y;
                 brick.initialize(this);
                 bricks.add(brick);
             }
         }
         gameContr.bricks = bricks;   
    }
    
    /**
     * This is in control of the keyEvent, so whenever a key has been detected this fires.
     */
     public void handle(KeyEvent event)
        {
            // send the event to the controller
            inputContr.userKeyInteraction( event );
        }
    /**
     * called by the game controller on every frame after the game has run its code.
     */
    public void update() {
        synchronized(gameContr) {
            
            updateScreen();
            updateUI();
        }
        
    }
    /**
     * Is in control of on screen text.
     */
    public void updateUI() {
        
            String scoreText = "Score: "+gameContr.score;
            
            if (gameContr.gamePaused) {
                displayText(width/2,height/2,"PAUSED",50,Color.WHITE);
                displayText(width/2, height-100, "PRESS SPACE TO UNPAUSE!", 10, Color.WHITE);
            }
            else if (gameContr.gameEnded) {
                displayText(width/2, height/2, "!!!FINISHED!!!", 50, finishingTextColor);
                displayText(width/2, height/2+50, "FINAL SCORE : "+gameContr.score, 30, finishingTextColor);
                displayText(width/2, height-75, "PRESS ENTER TO RESTART!", 20, finishingTextColor);
            }
            else {
                displayText(width/2,height/2,"Score : " + scoreText +"/"+gameContr.brickColumns*gameContr.brickRows,15,Color.WHITE);
            }
            
        }
    /**
     * Renders all visible game objects as well as painting the background black.
     */
    public synchronized void updateScreen() {

            //set the background black from 0,0 to max 
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width,height);
        
        displayRectObj(bat);
        displayRoundObj(ball);
        if (gameContr.gameEnded) {
            for (GameObj b: gameContr.balls) {
                displayRoundObj(b);
            }
        }
        for(GameObj b : bricks) {
            if (b.getVisible()) {
                displayRectObj(b);
            }
            
        }
        
    }
    /**
     * Called to print text onto the gamescreen
     * @param x int of the text
     * @param y int of the text
     * @param text 
     * @param fontSize double of the text
     * @param colour of the text
     */
    public void displayText(int x,int y, String text,double fontSize, Color colour) {
        gc.setFont(new Font(fontSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(colour);
        gc.fillText(text,x,y);
        
    }
    /**
     * Renders objects as rectangles
     * @param go gameobject to display
     */
    public void displayRectObj(GameObj go) {
        gc.setFill(go.colour);
        gc.fillRect(go.x,go.y,go.width,go.height);
    }
    /**
     * Renders objects as circles
     * @param go gameobject to display
     */
    public void displayRoundObj(GameObj go) {
        gc.setFill(go.colour);
        gc.fillOval(go.x,go.y,go.width,go.height);
    }

    /**
     * Translates an object by a point to a new location
     * @param go the GameObj
     * @param point the required translation in point form
     */
    public void moveObj(GameObj go,Point point){
        Translate.translate(point.x,point.y);
    }
    
    

}
