
import httpClient.entity.Constances;
import httpClient.service.WuLiuService;
import httpClient.util.HttpClientTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:spring.xml"})
public class TestWuLiu {

    @Resource
    private WuLiuService wuLiuService;
    @Autowired
    private Constances constances;



    @Test
    public void  test1(){
//        wuLiuService.doPost(null,null);
        wuLiuService.doGetGuanFang1();
    }

    @Test
    public void testGet2(){
       String result=(String ) HttpClientTool.doGet("http://192.168.9.57:9293/lampapi/api/checkNet");
        System.out.println("result:"+result);
    }

    @Test
    public void testGet3(){
        HashMap<String,Object> params=new HashMap<String, Object>();
        params.put("isViewLight",true);
        params.put("toKen","275e66d1490048e693aacfd61941dca2");
        params.put("inccode","1c34faaf3f0649c38417ba8a166c349e");
        String result=(String) HttpClientTool.doGet("http://192.168.9.57:9293/lampapi/api/get/region",params);
        System.out.println("result:"+result);

    }

    @Test
    public void testPost1(){
        HashMap<String,String> params=new HashMap<String, String>();
        params.put("deviceCode","12345678");
        params.put("lampNum","66666601");
        String result=(String) HttpClientTool.doPost("http://192.168.9.71:9006/api/query",params);
        System.out.println("result:"+result);
    }

    @Test
    public void testReadAllInfoProperties(){
        System.out.println("快递鸟即时查询接口:"+constances.getJiShiChaXun());
        try {
         String result=(String)   wuLiuService.queryKDNow("HTKY","71207223960993");
            System.out.println("result:"+result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
