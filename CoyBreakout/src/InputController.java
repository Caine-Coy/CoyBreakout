package src;


import javafx.scene.input.KeyEvent;

public class InputController {
	
	GameController gameContr;
	GraphicsController graphicsContr;
	CoyDebug debug;
	CoyFunctions coyFunctions;
	int previousGameTicks;
	
	static String debugClass = "Input Controller";
	
	GameObj bat,ball;
	
	int width,height;
	int batSpeed = 10;
	
	public InputController(CoyDebug debug) {
		this.debug = debug;
		coyFunctions = debug.coyFunctions;
		
	}
	
	public void initialize() {
		
		ball = gameContr.getBall();
		width = gameContr.width;
		height = gameContr.height;
		
	}
	//This works, not sure what its complaining about. Warning suppressed.
	void userKeyInteraction(KeyEvent event ) {
		bat = gameContr.getBat();
		switch ( event.getCode() )             
	    {
	      case LEFT:
	      case A:
	    	  if(bat.getPosX()>=batSpeed&&!gameContr.gamePaused) {
	    		  bat.movePos(coyFunctions.makePoint(-batSpeed, 0));
	    	  }
	        break;
	      case RIGHT:
	      case D:
	       	  if(bat.getPosX()<=width-bat.width-batSpeed&&!gameContr.gamePaused) {
	       		  bat.movePos(coyFunctions.makePoint(batSpeed, 0));
	    	  }
	        break;
	      case ESCAPE:
	    	  	gameContr.gameRunning = false;
	    	  break;
	      case ENTER:
	    	  if (gameContr.gameEnded) {
	    		  initialize();
	    		  graphicsContr.startGame();
	    		  gameContr.initialiseGame();
	    		  gameContr.gameEnded = false;
	    		  gameContr.gamePaused = true;
	    	  }
	    	  break;
	      case SPACE:
	    	  	if (!gameContr.gamePaused) {
	    	  		gameContr.gamePaused = true;
	    	  		debug.addToDebug(debugClass,"Game Paused");
	    	  	}
	    	  	else {
	    	  		gameContr.gamePaused = false;
	    	  		debug.addToDebug(debugClass,"Game Restarted");
	    	  	}
	    	  break;
	    }
	}

	
}
