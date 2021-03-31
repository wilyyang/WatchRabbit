package wily.apps.watchrabbit.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int hid;
    private int type;
    private long time;
    private long term;

    @Ignore
    private boolean check;

    public Record(int hid, int type, long time, long term) {
        this.hid = hid;
        this.type = type;
        this.time = time;
        this.term = term;
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

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", hid=" + hid +
                ", type=" + type +
                ", time=" + time +
                ", term=" + term +
                ", check=" + check +
                '}';
    }
}
