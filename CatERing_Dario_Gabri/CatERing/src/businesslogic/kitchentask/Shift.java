package businesslogic.kitchentask;

import java.sql.Time;

public abstract class Shift {
    protected int id;
    protected Time startTime;
    protected Time endTime;

    // GETTER METHODS

    public int getId() {
        return id;
    }
    public Time getStartTime() {
        return startTime;
    }
    public Time getEndTime() {
        return endTime;
    }

    // SETTER METHODS

    public void setId(int id) {
        this.id = id;
    }
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
