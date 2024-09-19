package bitcfull.moodo_spring.dto;

public class Holiday {
    private String dateName;
    private String isHoliday;
    private String locdate;

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(String isHoliday) {
        this.isHoliday = isHoliday;
    }

    public String getLocdate() {
        return locdate;
    }

    public void setLocdate(String locdate) {
        this.locdate = locdate;
    }
}
