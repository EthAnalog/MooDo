package bitcfull.moodo_spring.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name="items")
public class DataItemsDTO {
    private List<DataItemDTO> itemList;

    @XmlElement(name="item")
    public List<DataItemDTO> getItemList() {
        return itemList;
    }

    public void setItemList(List<DataItemDTO> itemList) {
        this.itemList = itemList;
    }
}
