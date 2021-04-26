package src;


import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;

/**
 * 
 * @author 'Caine'/Joe Benson
 * This is the gameObject type class.
 * Used for all onscreen objects that move.
 *
 */
public class GameObj {
	
	GraphicsController graphicsContr;
	GameController gameContr;
	CoyFunctions coyFunctions;
	CoyDebug debug;
	
	Point pos,startVelocity,velocity;
	Color colour;
	int width,height,x,y,startX,startY,health,brickArrayX,brickArrayY,angle;
	String name,debugClass,type;
	boolean circular,visible;
	Translate translate;
	
	
	/**
	 * 
	 * @param x int coordinate of the object
	 * @param y int coordinate of the object
	 * @param w int width of the object
	 * @param h int height of the object
	 * @param velocity point velocity of the object
	 * @param c colour of the object
	 * @param debug instance to be passed from the parent object
	 * @param isCircular Boolean. is the object going to be drawn as a circle
	 * @param type String what type of object is this? Mostly for naming purposes in the log.
	 */
	public GameObj( int x, int y, int w, int h,Point velocity, Color c,CoyDebug debug,boolean isCircular,String type)
    {
		this.velocity = new Point();
		this.startVelocity = new Point();
        this.x = x;       
        this.y = y;
        startX = x;
        startY = y;
        pos = new Point();
        this.pos.x=x;
        this.pos.y=y;
        width  = w; 
        height = h; 
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
        this.startVelocity.x= velocity.x;
        this.startVelocity.y = velocity.y;
        
        //invinsible objects have -1 health. bricks get health set later.
        health = -1;
        
        //Passing instances of coydebug and coyfunction
        this.debug = debug;
        coyFunctions = debug.coyFunctions;
        
        this.type = type;
        
        angle = 1;
        
        colour = c;
        this.circular = isCircular;
        
        translate = new Translate();
        visible = true;
    }
	
	/**
	 * Starts the gameobj and sets up its name.
	 * @param graphicsContr instance for the game
	 */
	public void initialize(GraphicsController graphicsContr) {
		this.graphicsContr = graphicsContr;
		name = ""+this;
		if (type == "brick") {
			debugClass = "GameObj: " + type + "@ " +brickArrayX + " : " + brickArrayY;
			name = type + "@ " +brickArrayX + " : " + brickArrayY;
		}
		else {
			debugClass = "GameObj: " + type;
			name = type;
		}
		debugClass = "GameObj: " + type + "";
	}
	
	/**
	 * 
	 * @return Color of the object
	 */
	public Color getColour() {
		return (colour);
	} 
	
	/**
	 * 
	 * @param colour sets the colour of the object
	 */
	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	/**
	 * 
	 * @param pos a point in which to translate the object by
	 */
	public void movePos(Point pos){
		moveX(pos.x);
		moveY(pos.y);
		this.pos.x=x;
        this.pos.y=y;
	}
	
	/**
	 * 
	 * @param x int x to translate by
	 * @return new x
	 */
	public double moveX(int x) {
		this.x += x;
		graphicsContr.moveObj(this,velocity);
		return this.x;
	}
	/**
	 * 
	 * @param y int y to translate by
	 * @return new y
	 */
	public double moveY(int y) {
		this.y += y;
		graphicsContr.moveObj(this,velocity);
		return this.y;
	}
	
	/**
	 * Inverts the X velocity
	 * @return new X
	 */
	public double bounceX() {
		velocity.x = -velocity.x;
		graphicsContr.moveObj(this,velocity);
		return this.x;
	}	
	
	/**
	 * Inverts the Y velocity
	 * @return new Y
	 */
	public double bounceY() {
		velocity.y = -velocity.y;
		graphicsContr.moveObj(this,velocity);
		return this.y;
	}
	
	/**
	 * resets the position and velocity to the when the object started.
	 */
	public void resetPos() {
		x = startX;
		y = startY;
		velocity.x = startVelocity.x;
		velocity.y = startVelocity.y;
		debug.addToDebug(debugClass,this+" Reset");
	}
	/**
	 * 
	 * @return Point Position
	 */
	public Point getPos() {
		return pos;	
	}
	
	/**
	 * 
	 * @return int x
	 */
	public int getPosX() {	
		return x;
	}
	
	/**
	 * 
	 * @param x sets int x
	 */
	public void setPosX(int x) {
		this.x = x;
	}
	
	/**
	 * 
	 * @return int y
	 */
	public int getPosY() {
		return y;
	}
	
	/**
	 * 
	 * @param y int y
	 */
	public void setPosY(int y) {
		this.y = y;
	}
	
	/**
	 * 
	 * @param point position to move object to
	 */
	public void setPos(Point point) {
		setPosX(point.x);
		setPosY(point.y);
	}
	/**
	 * 
	 * @return the translate of this object
	 */
	public Translate getTranslate() {
		return translate;
	}
	
	/**
	 * 
	 * @return point velocity of object
	 */
	public Point getVelocity(){
		return velocity;
	}
	/**
	 * Checks if an object is overlapping this object
	 * @param obj GameObj to check if overlapping
	 * @return boolean
	 */
	public boolean overlapping(GameObj obj) {
		
		boolean seperate =
				x >= obj.x+obj.width ||    
	            x+width <= obj.x     ||
	            y >= obj.y+obj.height||
			    y+height <= obj.y ;
	            
		return !(seperate);
	}
	
	/**
	 * 
	 * @param health int
	 */
	public void setHealth(int health) {
		this.health = health;
		debug.addToDebug(debugClass,"Setting health of "+getName()+" to "+ health);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	/**
	 * Allows you to change the health of an object. accepts negatives too
	 * Adjusts the colour according to health. Changes score.
	 * Works for all game objs but is only used for bricks.
	 * @param change int
	 */
	public synchronized void changeHealth(int change) {
		health = coyFunctions.clamp(health+change,0,health);
		debug.addToDebug(debugClass,"health reduced to "+ health);
		switch(health) {
		case 3: setColour(Color.RED);
		break;
		case 2: setColour(Color.ORANGE);
		break;
		case 1: setColour(Color.GREEN);
		break;
		}
		if(health == 0) {
			this.setVisible(false);
			gameContr.playMusic(gameContr.brickBreak);
			debug.addToDebug(debugClass,"Destroyed!");
			gameContr.addScore(1);
		}
		
	}
	
	public int getHealth() {
		return health;
	}
	/**
	 * Runs through the given bricks array, finding the x and y asked for.
	 * @param x int of the brick array X
	 * @param y int of the brick array y
	 * @param bricks ArrayList&lt;GameObj&gt; array of the bricks.
	 * @return the brick at that brick array coordinate.
	 */
	public GameObj getFromBrickArray(int x,int y,ArrayList<GameObj> bricks) {
		for (GameObj b:bricks) {
			if (b.brickArrayX == x) {
				if (b.brickArrayY == y) {
					return b;
				}
			}
	}
		return null;
	}
	public String getName() {
		return name;
	}
}

