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
    private int type;
    private long time;
    private String title;
    private int priority;
    private boolean active;

    private int goalCost;
    private int initCost;
    private int perCost;

    private int state;
    private int curRecordId;

    @Ignore
    private boolean check;

    @Ignore
    public static final int TYPE_HABBIT_CHECK = 1;
    @Ignore
    public static final int TYPE_HABBIT_TIMER = 2;

    @Ignore
    public static final int STATE_CHECK = 1001;
    @Ignore
    public static final int STATE_TIMER_WAIT = 2001;
    @Ignore
    public static final int STATE_TIMER_INPROGRESS = 2002;

    public Habbit(int type, long time, String title, int priority, boolean active, int goalCost, int initCost, int perCost, int state) {
        this.type = type;
        this.time = time;
        this.title = title;
        this.priority = priority;
        this.active = active;
        this.goalCost = goalCost;
        this.initCost = initCost;
        this.perCost = perCost;
        this.state = state;
        this.curRecordId = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getCurRecordId() {
        return curRecordId;
    }

    public void setCurRecordId(int curRecordId) {
        this.curRecordId = curRecordId;
    }

    @Override
    public String toString() {
        return "Habbit{" +
                "id=" + id +
                ", type=" + type +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", active=" + active +
                ", goalCost=" + goalCost +
                ", initCost=" + initCost +
                ", perCost=" + perCost +
                ", state=" + state +
                ", curRecordId=" + curRecordId +
                ", check=" + check +
                '}';
    }
}
