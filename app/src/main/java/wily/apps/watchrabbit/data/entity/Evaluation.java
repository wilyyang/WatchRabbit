package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import wily.apps.watchrabbit.util.DateUtil;

@Entity
public class Evaluation {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int hid;
    private long time;

    private int resultCost;
    private int achiveRate;

    @Ignore
    private boolean check;

    public Evaluation(int hid, long time, int resultCost, int achiveRate) {
        this.hid = hid;
        this.time = time;
        this.resultCost = resultCost;
        this.achiveRate = achiveRate;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getResultCost() {
        return resultCost;
    }

    public void setResultCost(int resultCost) {
        this.resultCost = resultCost;
    }

    public int getAchiveRate() {
        return achiveRate;
    }

    public void setAchiveRate(int achiveRate) {
        this.achiveRate = achiveRate;
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
                ", time=" + DateUtil.getDateString(time) +
                ", resultCost=" + resultCost +
                ", achiveRate=" + achiveRate +
                ", check=" + check +
                '}';
    }
}
