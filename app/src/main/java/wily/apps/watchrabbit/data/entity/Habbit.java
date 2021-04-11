package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import wily.apps.watchrabbit.util.DateUtil;

@Entity
public class Habbit implements Serializable {
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
    private long curRecordId;

    private int currentResultCost;
    private int currentAchiveRate;
    private int day7ResultCost;
    private int day7AchiveRate;
    private int day30ResultCost;
    private int day30AchiveRate;

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

    public Habbit(int type, long time, String title, int priority, boolean active, int goalCost, int initCost, int perCost, int state, long curRecordId) {
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
        this.curRecordId = curRecordId;

        this.currentResultCost = 1;
        this.currentAchiveRate = 2;
        this.day7ResultCost = 3;
        this.day7AchiveRate = 4;
        this.day30ResultCost = 5;
        this.day30AchiveRate = 6;
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

    public long getCurRecordId() {
        return curRecordId;
    }

    public void setCurRecordId(long curRecordId) {
        this.curRecordId = curRecordId;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getCurrentResultCost() {
        return currentResultCost;
    }

    public void setCurrentResultCost(int currentResultCost) {
        this.currentResultCost = currentResultCost;
    }

    public int getCurrentAchiveRate() {
        return currentAchiveRate;
    }

    public void setCurrentAchiveRate(int currentAchiveRate) {
        this.currentAchiveRate = currentAchiveRate;
    }

    public int getDay7ResultCost() {
        return day7ResultCost;
    }

    public void setDay7ResultCost(int day7ResultCost) {
        this.day7ResultCost = day7ResultCost;
    }

    public int getDay7AchiveRate() {
        return day7AchiveRate;
    }

    public void setDay7AchiveRate(int day7AchiveRate) {
        this.day7AchiveRate = day7AchiveRate;
    }

    public int getDay30ResultCost() {
        return day30ResultCost;
    }

    public void setDay30ResultCost(int day30ResultCost) {
        this.day30ResultCost = day30ResultCost;
    }

    public int getDay30AchiveRate() {
        return day30AchiveRate;
    }

    public void setDay30AchiveRate(int day30AchiveRate) {
        this.day30AchiveRate = day30AchiveRate;
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
                ", currentResultCost=" + currentResultCost +
                ", currentAchiveRate=" + currentAchiveRate +
                ", day7ResultCost=" + day7ResultCost +
                ", day7AchiveRate=" + day7AchiveRate +
                ", day30ResultCost=" + day30ResultCost +
                ", day30AchiveRate=" + day30AchiveRate +
                ", check=" + check +
                '}';
    }
}
