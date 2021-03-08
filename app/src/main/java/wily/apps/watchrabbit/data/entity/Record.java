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
    private int state;

    private long pair;

    @Ignore
    private boolean check;

    public Record(int hid, int type, long time, int state, long pair) {
        this.hid = hid;
        this.type = type;
        this.time = time;
        this.state = state;
        this.pair = pair;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getPair() {
        return pair;
    }

    public void setPair(long pair) {
        this.pair = pair;
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
        return "id= " + this.id +" , hid= " + this.hid +" , type= " + this.type +
                ", time= "+format.format(new Date(time))+ "state= " + this.state + "pair id= " + this.pair;
    }
}
