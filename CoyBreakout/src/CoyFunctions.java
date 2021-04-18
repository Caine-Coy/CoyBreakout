package src;


import java.awt.Point;

public class CoyFunctions {
	
	CoyDebug debug;
	String debugClass = "CoyFunctions";
	//Turns Precise coordinates into a Point for easy passing.
	public Point makePoint(int x,int y) {
		Point point = new Point();
		point.x = x;
		point.y = y;
		//debug.addToDebug(debugClass,"Added "+ x + " + " + y + " To Get " + point);
		return point;
		
	}
	
	 //for some reason java doesn't have a clamp method? Made my own. Clamps numbers to a range.
    public double clamp(double value, double min,double max) {

		if (value < min) {
			return min;
		}
		else if (value > max) {
			return max;
		}
		else {
			return value;
		}
		
    }
    
    //override with int values instead;
    public int clamp(int value, int min,int max) {

		if (value < min) {
			return min;
		}
		else if (value > max) {
			return max;
		}
		else {
			return value;
		}
		
    }
    
    public boolean inBounds(int x,int y,int minX,int maxX,int minY,int maxY) {
    	if (x >= minX) {
    		if (x <= maxX) {
    			if (y >= minY) {
    				if (y <= maxY) {
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    /*public boolean isEven(long num) {
    	//checks if the rightmost bit is 0, if its 0, its an even number. Else its odd.
		return ((num & 0x1)==0);
    	
    }*/
}
