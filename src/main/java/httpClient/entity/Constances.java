package httpClient.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
final public class Constances {

    @Value("#{thirdInfo.kdniao}")
    public    String jiShiChaXun;

    @Value("#{thirdInfo.kdniaoUserId}")
    public  String kdniaoUserId;

    @Value("#{thirdInfo.kdniaoAPIKey}")
    public  String kdniaoAPIKey;

    public String getJiShiChaXun() {
        return jiShiChaXun;
    }

    public void setJiShiChaXun(String jiShiChaXun) {
        this.jiShiChaXun = jiShiChaXun;
    }

    public String getKdniaoUserId() {
        return kdniaoUserId;
    }

    public void setKdniaoUserId(String kdniaoUserId) {
        this.kdniaoUserId = kdniaoUserId;
    }

    public String getKdniaoAPIKey() {
        return kdniaoAPIKey;
    }

    public void setKdniaoAPIKey(String kdniaoAPIKey) {
        this.kdniaoAPIKey = kdniaoAPIKey;
    }
}
