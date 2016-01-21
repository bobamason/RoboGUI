package org.masonapps.robogui;

/**
 * Created by Bob on 12/28/2015.
 */
public class MoveCommand {
    
    public static int ACTION_WAIT = 0;
    public static int ACTION_MOVE_FORWARD = 1;
    public static int ACTION_MOVE_BACKWARD = 2;
    public static int ACTION_TURN_LEFT = 3;
    public static int ACTION_TURN_RIGHT = 4;
    public static final String[] ACTION_NAMES = {"Wait", "Move Forward", "Move Backward", "Turn Left", "Turn Right"};

    private int action = ACTION_WAIT;
    private int magnitude = 0;
    
    
    public MoveCommand() {
    }

    public MoveCommand(int action, int magnitude) {
        this.action = action;
        this.magnitude = magnitude;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public static String actionName(int action) {
        return ACTION_NAMES[action];
    }
}
