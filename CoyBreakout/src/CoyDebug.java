package src;
/*
 * @author Joe Benson
 * @version 1.0
 */


import java.util.Date;
import java.util.Stack;

public class CoyDebug {
	
	boolean printDebug;
	boolean logDebug;
	Stack<String> debugLog;
	Date date;
	String debugClass = "Debug";
	public CoyFunctions coyFunctions;
	
	public CoyDebug(boolean print, boolean log){
		printDebug = print;
		logDebug = log;
		debugLog = new Stack<String>();
		addToDebug(debugClass,"Debug Successfully Started");
		coyFunctions = new CoyFunctions();
		coyFunctions.debug = this;
		addToDebug(debugClass,"Coyfunctions Successfully Started");
	}


	public void addToDebug(String callingObject, String string) {
		date = java.util.Calendar.getInstance().getTime();
		String currDebugLog = (date+" : " +callingObject+ " : " + string);
		debugLog.push(currDebugLog);
		System.out.println(currDebugLog);
	}
	
	
}
