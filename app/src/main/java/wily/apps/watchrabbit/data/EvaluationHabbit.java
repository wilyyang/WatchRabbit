package wily.apps.watchrabbit.data;


import wily.apps.watchrabbit.data.entity.Habbit;

public class EvaluationHabbit {

    private Habbit habbit;

    private int day30Result;
    private int day30Achive;

    private int day7Result;
    private int day7Achive;

    private int todayResult;
    private int todayAchive;

    private boolean check;

    public EvaluationHabbit(Habbit habbit, int day30Result, int day30Achive, int day7Result, int day7Achive, int todayResult, int todayAchive) {
        this.habbit = habbit;
        this.day30Result = day30Result;
        this.day30Achive = day30Achive;
        this.day7Result = day7Result;
        this.day7Achive = day7Achive;
        this.todayResult = todayResult;
        this.todayAchive = todayAchive;
        this.check = false;
    }

    public Habbit getHabbit() {
        return habbit;
    }

    public void setHabbit(Habbit habbit) {
        this.habbit = habbit;
    }

    public int getDay30Result() {
        return day30Result;
    }

    public void setDay30Result(int day30Result) {
        this.day30Result = day30Result;
    }

    public int getDay30Achive() {
        return day30Achive;
    }

    public void setDay30Achive(int day30Achive) {
        this.day30Achive = day30Achive;
    }

    public int getDay7Result() {
        return day7Result;
    }

    public void setDay7Result(int day7Result) {
        this.day7Result = day7Result;
    }

    public int getDay7Achive() {
        return day7Achive;
    }

    public void setDay7Achive(int day7Achive) {
        this.day7Achive = day7Achive;
    }

    public int getTodayResult() {
        return todayResult;
    }

    public void setTodayResult(int todayResult) {
        this.todayResult = todayResult;
    }

    public int getTodayAchive() {
        return todayAchive;
    }

    public void setTodayAchive(int todayAchive) {
        this.todayAchive = todayAchive;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "EvaluationHabbit{" +
                "habbit=" + habbit +
                ", day30Result=" + day30Result +
                ", day30Achive=" + day30Achive +
                ", day7Result=" + day7Result +
                ", day7Achive=" + day7Achive +
                ", todayResult=" + todayResult +
                ", todayAchive=" + todayAchive +
                ", check=" + check +
                '}';
    }
}
