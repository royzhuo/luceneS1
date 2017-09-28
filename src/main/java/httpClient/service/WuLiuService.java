package httpClient.service;

import httpClient.entity.Constances;
import httpClient.util.HttpClientTool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WuLiuService {

    @Autowired
    private Constances constances;



    /*
    * 快递即时查询
    * shipperCode:快递gong si bian ma
    * logisticCode:公司编码物流单号
    * */
    public Object queryKDNow(String shipperCode,String logisticCode) throws UnsupportedEncodingException {
        //数据处理
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("ShipperCode",shipperCode);
        jsonObject.put("LogisticCode",logisticCode);
        String requestData= jsonObject.toString();
        //系统参数
        Map<String,String> params=new HashMap<String, String>();
        params.put("RequestData",encodUrl(requestData,"UTF-8"));
        params.put("EBusinessID", constances.getKdniaoUserId());
        params.put("RequestType","1002");
        //数据内容签名：把(请求内容(未编码)+AppKey)进行MD5加密，然后Base64编码，最后 进行URL(utf-8)编码
        params.put("DataSign",encodUrl(encrypt(requestData,constances.getKdniaoAPIKey(),"UTF-8"),"UTF-8"));
        params.put("DataType","2");
        return sendPost1(constances.getJiShiChaXun(),params);
    }

    //请求内容进行URL(utf-8)编码
    public String encodUrl(String param,String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(param,charset);
    }

    /**
     * 电商Sign签名生成
     * @param content 内容
     * @param keyValue Appkey
     * @param charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    private String encrypt(String content,String keyValue,String charset) throws UnsupportedEncodingException {
        if (keyValue!=null){
           return base64(MD5(content+keyValue,charset),charset);
        }
        return base64(MD5(content,charset),charset);
    }
    /**
     * MD5加密
     * @param str 内容
     * @param charset 编码方式
     * @throws Exception
     */
    private String MD5(String str,String charset){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            try {
                //）传入需要计算的字符串
                messageDigest.update(str.getBytes(charset));
//               ）计算消息摘要
                byte[] result=messageDigest.digest();
//                //处理计算结果
                StringBuffer stringBuffer=new StringBuffer(32);
                for (int i=0;i<result.length;i++){
                    int val=result[i]& 0xff;
                    if (val<0xf){
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(Integer.toHexString(val));
                }
                return stringBuffer.toString().toLowerCase();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * base64编码
     * @param str 内容
     * @param charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str,String charset) throws UnsupportedEncodingException {
        String encoded = base64Encode(str.getBytes(charset));
        return encoded;
    }

    private static char[] base64EncodeChars = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    public static String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    public WuLiuService() {
        super();
    }

    private Object sendPost1(String url,Map<String,String> params){
        HttpPost httpPost=new HttpPost(url);
        List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        if (params.size()>0){
            for(String key:params.keySet()){
                nameValuePairs.add(new BasicNameValuePair(key,params.get(key)));
            }
            try {
                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(nameValuePairs,"utf-8");
                httpPost.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        CloseableHttpClient httpClient= HttpClientTool.getHttpClient();
        try {
            HttpResponse response=httpClient.execute(httpPost);
            response.setHeader("Content-Type","application/x-www-form-urlencoded");
            if (response.getStatusLine().getStatusCode()==200){
                HttpEntity entity=response.getEntity();
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                }
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**********************************************测试***********************************************************/
    private static CloseableHttpClient httpClient;

    static {
        httpClient=HttpClients.createDefault();
    }
    public Object doPost(String url, Map<String,Object> params){
        return null;
    }

    public Object doGetGuanFang1(){
        final CloseableHttpClient httpClient= HttpClients.createDefault();
        try {
            final HttpGet httpGet=new HttpGet("http://192.168.9.57:9293/lampapi/api/checkNet");
            System.out.println("excute request: "+httpGet.getRequestLine());
            ResponseHandler<String> responseHandler=new ResponseHandler<String>() {

                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    int status=httpResponse.getStatusLine().getStatusCode();
                    if (status>=200&&status<300){
                        HttpEntity entity=httpResponse.getEntity();
                        return entity!=null ? EntityUtils.toString(entity):null;
                    }else{
                        throw new ClientProtocolException("Unexpected response  status: "+status);
                    }
                }
            };
            String responseBody=httpClient.execute(httpGet,responseHandler);
            System.out.println("-----------------");
            System.out.println(responseBody);

        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());

        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String  doGet1(String url,Map<String,Object> params){

        return null;
    }
}
