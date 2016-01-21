package org.masonapps.robogui;

import android.support.annotation.NonNull;

/**
 * Created by Bob on 12/30/2015.
 */
public class RobotState implements Comparable<RobotState>{
    
    // movement in cm since last state, positive forward, negative backward, 0 stopped or turned in place
    private float movement;
    // compass heading in degrees relative to +Y axis of compass module
    private float heading;
    // IR proximity distance, -1 for nothing in range of sensor
    private float sensorReading;
    // sensor servo angle -90 to 90 degrees
    private float sensorAngle;
    // millis() from arduino, for sorting states to build map
    private long timestamp;
    
    // ID from storing in database
    public long id = -1;

    public RobotState() {
        this.movement = 0f;
        this.heading = 0f;
        this.sensorReading = -1f;
        this.sensorAngle = 0f;
        this.timestamp = -1;
    }
    
    public RobotState(float movement, float heading, float sensorReading, float sensorAngle, long timestamp){
        this.movement = movement;
        this.heading = heading;
        this.sensorReading = sensorReading;
        this.sensorAngle = sensorAngle;
        this.timestamp = timestamp;
    }

    public float getMovement() {
        return movement;
    }

    public void setMovement(float movement) {
        this.movement = movement;
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public float getSensorReading() {
        return sensorReading;
    }

    public void setSensorReading(float sensorReading) {
        this.sensorReading = sensorReading;
    }

    public float getSensorAngle() {
        return sensorAngle;
    }

    public void setSensorAngle(float sensorAngle) {
        this.sensorAngle = sensorAngle;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public int compareTo(@NonNull RobotState other) {
        if(this.timestamp == other.getTimestamp()) return 0;
        return this.timestamp > other.getTimestamp() ? 1 : -1;
    }
}
