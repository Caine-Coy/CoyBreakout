package src;

//importing JavaFX required some edits to the java classpath in eclipse's setting.
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * 
 * @author 'Caine' / Joe Benson
 *
 */
public class Main extends Application{
	//the start method for java
	String debugClass = "Main";
	CoyDebug debug;
	
	GraphicsController graphicsContr;
	GameController gameContr;
	InputController inputContr;
	
	public static void main(String args[])
    {
        launch();
    }
	/**
	 * The start method for the program. called by Javafx
	 */
	public void start(Stage window){
		//Setting window size
		 int width = 500;         
	     int height = 500;
	     
	     CoyDebug debug = new CoyDebug(true,true);
	     //Passing current events to the debugger
	     debug.addToDebug(debugClass,"Set Width to "+width);
	     debug.addToDebug(debugClass,"Set Height to "+height);
	     
	     //Defining all the objects, and giving them the debug instance.
	     graphicsContr = new GraphicsController(width,height,debug);
	     gameContr = new GameController(width,height,debug);
	     
	     debug.addToDebug(debugClass,"Game Controller Debug Started");
	     	     
	     graphicsContr.gameContr = gameContr;
	     gameContr.graphicsContr = graphicsContr;
	     
	     debug.addToDebug(debugClass,"Graphics Controller Debug Started");
	     window.setResizable(false);
	     graphicsContr.start(window);
	     gameContr.startGame();
	     
	     debug.addToDebug(debugClass,"Graphics Controller Window and Game Controller started");
	     
	     inputContr = new InputController(debug);
	     graphicsContr.inputContr = inputContr;
	     gameContr.inputContr = inputContr;
	     inputContr.gameContr = gameContr;
	     inputContr.graphicsContr = graphicsContr;
	     inputContr.initialize();
	     debug.addToDebug(debugClass,"Input Controller Debug Started");
	     
	     //This Section makes sure all the child objects of the Main class have a way to access each others instance, as well as adds to the
	     //Debugger that they have all been successfully reached in code, and therefore started.
	     	     	     
	     
	     
	     	     
	     
	     
	     	     
	     
	}
}
