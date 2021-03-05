package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Habbit {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long time;

    private int type;
    private String title;
    private boolean active;

    private int goalCost;
    private int initCost;
    private int perCost;

    private int priority;


    @Ignore
    private boolean check;

    public Habbit(int type, long time, int priority, String title, boolean active, int goalCost, int initCost, int perCost) {
        this.type = type;
        this.time = time;
        this.priority = priority;
        this.title = title;
        this.active = active;
        this.goalCost = goalCost;
        this.initCost = initCost;
        this.perCost = perCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() { return priority; }

    public void setPriority(int priority) { this.priority = priority; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGoalCost() {
        return goalCost;
    }

    public void setGoalCost(int goalCost) {
        this.goalCost = goalCost;
    }

    public int getInitCost() {
        return initCost;
    }

    public void setInitCost(int initCost) {
        this.initCost = initCost;
    }

    public int getPerCost() {
        return perCost;
    }

    public void setPerCost(int perCost) {
        this.perCost = perCost;
    }

    public boolean isCheck() {
        return check;
    }
    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "id= " + this.id + ", time= "+format.format(new Date(time))+" , type= " + this.type + " , priority= " + this.priority + " , title= " + this.title+ " , active= " + this.active+
                " , goalCost= " + this.goalCost+ " , initCost= " + this.initCost+ " , perCost= " + this.perCost;
    }
}
