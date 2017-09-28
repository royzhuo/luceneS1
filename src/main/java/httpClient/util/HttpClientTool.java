package httpClient.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientTool {

    public static final String CHARSET="UTF-8";

    private HttpClientTool(){};

    private static CloseableHttpClient httpClient;

    public static CloseableHttpClient getHttpClient(){
        if (httpClient==null){
            synchronized (HttpClientTool.class){
                if (httpClient==null){
                    httpClient= HttpClients.createDefault();
                }
            }
        }
        return httpClient;
    }


    public static Object doGet(String url){

        HttpGet httpGet=new HttpGet(url);
        return loadData(httpGet);
    }

    public static Object doGet(String url, Map<String,Object> params){
        List<NameValuePair> entityParams=new ArrayList<NameValuePair>(params.size());
        if (params!=null&&params.size()>0){
            for(String key:params.keySet()){
                System.out.println("key:"+key+" value:"+params.get(key));
                entityParams.add(new BasicNameValuePair(key,params.get(key).toString()));
            }
        }
        try {
            url+="?"+EntityUtils.toString(new UrlEncodedFormEntity(entityParams,CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpGet httpGet=new HttpGet(url);
        httpGet.setConfig(getRequestConfig());
        return loadData(httpGet);
    }

    public static Object doPost(String url,Map<String,String> params){
        HttpPost httpPost=new HttpPost();
        httpPost.setConfig(getRequestConfig());
        List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        if (params.size()>0){
            for(String key:params.keySet()){
                nameValuePairs.add(new BasicNameValuePair(key,params.get(key)));
            }
            try {
                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(nameValuePairs,CHARSET);
                httpPost.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        httpPost.setURI(URI.create(url));
        return loadData(httpPost);
    }

    private static RequestConfig getRequestConfig(){
        return RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).build();
    }

    private static Object loadData(HttpUriRequest httpUriRequest){
        httpClient=getHttpClient();
        try {
            HttpResponse httpResponse=httpClient.execute(httpUriRequest);
            System.out.println(httpResponse.getStatusLine());
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            System.out.println(httpResponse.getLocale());
            System.out.println(httpResponse.getLocale().getCountry());
            System.out.println(httpResponse.getLocale().getDisplayName());

            if (httpResponse.getStatusLine().getStatusCode()==200){
                HttpEntity entity=httpResponse.getEntity();
                String result=EntityUtils.toString(entity);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * 文件上传
    * */
    public void upload() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost("http://localhost:8080/myDemo/Ajax/serivceFile.action");

            FileBody bin = new FileBody(new File("F:\\image\\sendpix0.jpg"));
            StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment).build();

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

}
