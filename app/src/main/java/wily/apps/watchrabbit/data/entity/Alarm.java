package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int hid;
    private String title;
    private long time;
    private long range;
    private int cost;

    @Ignore
    private boolean check;

    public Alarm(int hid, String title, long time, long range, int cost) {
        this.hid = hid;
        this.title = title;
        this.time = time;
        this.range = range;
        this.cost = cost;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getRange() {
        return range;
    }

    public void setRange(long range) {
        this.range = range;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", hid=" + hid +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", range=" + range +
                ", cost=" + cost +
                ", check=" + check +
                '}';
    }
}
