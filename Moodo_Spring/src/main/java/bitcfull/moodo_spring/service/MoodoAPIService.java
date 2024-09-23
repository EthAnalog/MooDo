package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.dto.DataItemDTO;
import bitcfull.moodo_spring.dto.DataResponseDTO;
import bitcfull.moodo_spring.model.MoodoHoliday;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MoodoAPIService {
    public List<MoodoHoliday> getItemList(String url) throws Exception {
        List<DataItemDTO> itemList;

        URL server = null;
        HttpURLConnection urlConn = null;

        try {
            server = new URL(url);
            urlConn = (HttpURLConnection) server.openConnection();
            urlConn.setRequestMethod("GET");

            JAXBContext jc = JAXBContext.newInstance(DataResponseDTO.class);
            Unmarshaller um = jc.createUnmarshaller();

            DataResponseDTO fullData = (DataResponseDTO)um.unmarshal(server);

            itemList = fullData.getBody().getItems().getItemList();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to retrieve data from the service URL: " + url, e);
        }
        finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        List<MoodoHoliday> holidayList = new ArrayList<>();

        if (itemList == null) {
            throw new IllegalStateException("itemList is null");
        }
        else {
            for (DataItemDTO item : itemList) {
                SimpleDateFormat input = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                Date holidayLocdate = input.parse(item.getLocdate());

                String locdate = output.format(holidayLocdate);
                String isHoliday = item.getIsHoliday();
                String holidayName = item.getDateName();

                MoodoHoliday holidayItem = new MoodoHoliday();
                holidayItem.setLocdate(locdate);
                holidayItem.setIsHoliday(isHoliday);
                holidayItem.setDateName(holidayName);

                holidayList.add(holidayItem);
            }
        }

        return holidayList;
    }
}
