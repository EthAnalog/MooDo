package bitcfull.moodo_spring.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class DataResponseDTO {
    private DataHeaderDTO header;
    private DataBodyDTO body;

    @XmlElement(name = "header")
    public DataHeaderDTO getHeader() {
        return header;
    }

    public void setHeader(DataHeaderDTO header) {
        this.header = header;
    }

    @XmlElement(name = "body")
    public DataBodyDTO getBody() {
        return body;
    }

    public void setBody(DataBodyDTO body) {
        this.body = body;
    }
}
