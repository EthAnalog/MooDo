package bitcfull.moodo_spring.model;

public class MoodoCalendar {
    private int todayTd;
    private String todayMd;
    private String isHoliday;

    public int getTodayTd() {
        return todayTd;
    }

    public void setTodayTd(int todayTd) {
        this.todayTd = todayTd;
    }

    public String getTodayMd() {
        return todayMd;
    }

    public void setTodayMd(String todayMd) {
        this.todayMd = todayMd;
    }

    public String getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(String isHoliday) {
        this.isHoliday = isHoliday;
    }
}
