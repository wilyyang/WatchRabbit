package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Evaluation {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int hid;
    private int type;
    private long time;

    private int goalCost;
    private int initCost;

    private int sumCost;
    private int resultCost;
    private int achive;

    @Ignore
    private boolean check;

    public Evaluation(int hid, int type, long time, int goalCost, int initCost, int sumCost, int resultCost, int achive) {
        this.hid = hid;
        this.type = type;
        this.time = time;
        this.goalCost = goalCost;
        this.initCost = initCost;
        this.sumCost = sumCost;
        this.resultCost = resultCost;
        this.achive = achive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
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

    public int getSumCost() {
        return sumCost;
    }

    public void setSumCost(int sumCost) {
        this.sumCost = sumCost;
    }

    public int getResultCost() {
        return resultCost;
    }

    public void setResultCost(int resultCost) {
        this.resultCost = resultCost;
    }

    public int getAchive() {
        return achive;
    }

    public void setAchive(int achive) {
        this.achive = achive;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", hid=" + hid +
                ", type=" + type +
                ", time=" + time +
                ", goalCost=" + goalCost +
                ", initCost=" + initCost +
                ", sumCost=" + sumCost +
                ", resultCost=" + resultCost +
                ", achive=" + achive +
                ", check=" + check +
                '}';
    }
}
