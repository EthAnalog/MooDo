package bitcfull.moodo_spring.dto;

import jakarta.xml.bind.annotation.XmlElement;

public class DataItemDTO {
    private String dateKind; // 정보 분류
    private String dateName;
    private String isHoliday;
    private String locdate;
    private String seq; // 순번

    @XmlElement
    public String getDateKind() {
        return dateKind;
    }

    public void setDateKind(String dateKind) {
        this.dateKind = dateKind;
    }

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

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
