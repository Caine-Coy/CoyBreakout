package src;


import java.awt.Point;

import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;

public class GameObj {
	
	GraphicsController graphicsContr;
	GameController gameContr;
	Point pos,startVelocity,velocity;
	Color colour;
	int width,height,x,y,startX,startY;
	String name,debugClass;
	CoyDebug debug;
	boolean circular,visible;
	Translate translate;
	CoyFunctions coyFunctions;
	int health;
	
	public GameObj( int x, int y, int w, int h,Point velocity, Color c,CoyDebug debug,boolean circular)
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
        this.debug = debug;
        coyFunctions = debug.coyFunctions;
        name = ""+this;
        debugClass = "GameObj: " + this + "";
        width  = w; 
        height = h; 
        colour = c;
        this.circular = circular;
        debug.addToDebug(debugClass,name+" created with colour "+colour);
        translate = new Translate();
        visible = true;
        
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
        this.startVelocity.x= velocity.x;
        this.startVelocity.y = velocity.y;
        health = -1;

    }
	
	public void initialize(GraphicsController graphicsContr) {
		this.graphicsContr = graphicsContr;
	}
	
	public Color getColour() {
		return (colour);
	} 
	
	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	public void movePos(Point pos){
		moveX(pos.x);
		moveY(pos.y);
		this.pos.x=x;
        this.pos.y=y;
		//debug.addToDebug(debugClass,this+" Moved at Velocity: "+ velocity +" To " + this.pos);
	}
	
	//moves on the X axis and returns the new x
	public double moveX(int x) {
		this.x += x;
		graphicsContr.moveObj(this,velocity);
		return this.x;
	}
	//moves on the Y axis and returns the new y
	public double moveY(int y) {
		this.y += y;
		graphicsContr.moveObj(this,velocity);
		return this.y;
	}
	public double bounceX() {
		velocity.x = -velocity.x;
		graphicsContr.moveObj(this,velocity);
		return this.x;
	}	
	public double bounceY() {
		velocity.y = -velocity.y;
		graphicsContr.moveObj(this,velocity);
		return this.y;
	}
	
	
	public void resetPos() {
		x = startX;
		y = startY;
		velocity.x = startVelocity.x;
		velocity.y = startVelocity.y;
		debug.addToDebug(debugClass,this+" Reset");
	}
	
	public Point getPos() {
		return pos;	
	}
	
	public int getPosX() {	
		return x;
	}
	public int getPosY() {
		return y;
	}
	
	public Translate getTranslate() {
		return translate;
	}
	
	public Point getVelocity(){
		return velocity;
	}
	
	public boolean overlapping(GameObj obj) {
		
		boolean seperate =
				x >= obj.x+obj.width ||    
	            x+width <= obj.x     ||
	            y >= obj.y+obj.height||
			    y+height <= obj.y ;
	            
		return !(seperate);
	}
	
	public void setHealth(int health) {
		this.health = health;
		debug.addToDebug(debugClass,"Setting health of "+this+" to "+ health);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean getVisible() {
		return visible;
	}
	
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
			gameContr.brickBreak.play(gameContr.gameVolume);
			debug.addToDebug(debugClass,"Destroyed!");
			gameContr.addScore(1);
		}
		
	}
	
	public int getHealth() {
		return health;
	}
	
}
