package src;


import javafx.scene.input.KeyEvent;

public class InputController {
	
	GameController gameContr;
	GraphicsController graphicsContr;
	CoyDebug debug;
	CoyFunctions coyFunctions;
	int previousGameTicks;
	boolean paused;
	
	static String debugClass = "Input Controller";
	
	GameObj bat,ball;
	
	int width,height;
	int batSpeed = 10;
	
	public InputController(CoyDebug debug) {
		this.debug = debug;
		coyFunctions = debug.coyFunctions;
		
	}
	
	public void initialize() {
		bat = gameContr.getBat();
		ball = gameContr.getBall();
		width = gameContr.width;
		height = gameContr.height;
		paused = false;
		
	}
	//This works, not sure what its complaining about. Warning suppressed.
	void userKeyInteraction(KeyEvent event ) {
		switch ( event.getCode() )             
	    {
	      case LEFT:
	      case A:
	    	  if(bat.getPosX()>=batSpeed&&!paused) {
	    		  bat.movePos(coyFunctions.makePoint(-batSpeed, 0));
	    	  }
	        break;
	      case RIGHT:
	      case D:
	       	  if(bat.getPosX()<=width-bat.width-batSpeed&&!paused) {
	       		  bat.movePos(coyFunctions.makePoint(batSpeed, 0));
	    	  }
	        break;
	      case ESCAPE:
	    	  	gameContr.gameRunning = false;
	    	  break;
	      case SPACE:
	    	  	if (!paused) {
	    	  		paused = true;
	    	  		gameContr.gamePaused = true;
	    	  		debug.addToDebug(debugClass,"Game Paused");
	    	  	}
	    	  	else {
	    	  		paused = false;
	    	  		gameContr.gamePaused = false;
	    	  		gameContr.pauseDisplayed = false;
	    	  		debug.addToDebug(debugClass,"Game Restarted");
	    	  	}
	    	  break;
	    }
	}

	
}
