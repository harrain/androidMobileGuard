package com.phonesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by stephen on 2017/2/17.
 */

public class HttpUtil {

    public static HttpURLConnection httpConnect(String strUrl) {
        URL url;
        String result = "";
        HttpURLConnection urlconn = null;
        try {
            url = new URL(strUrl);
            urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setRequestMethod("GET");// 链接服务器并发送消息
            urlconn.setConnectTimeout(10000);
            urlconn.setReadTimeout(15000);
            urlconn.connect();
            if (urlconn.getResponseCode() == HttpURLConnection.HTTP_OK){
                return urlconn;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlconn;
    }

    public static String RequestHttpStr(String strUrl) {
        URL url;
        String result = "";
        try {
            url = new URL(strUrl);
            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setRequestMethod("GET");// 链接服务器并发送消息
            urlconn.setConnectTimeout(10000);
            urlconn.setReadTimeout(15000);

            // 开始接收返回的数据
            InputStreamReader is = new InputStreamReader(urlconn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(is);
            String readLine ;
            while ((readLine = bufferedReader.readLine()) != null) {
                result += readLine;
            }
            bufferedReader.close();
            is.close();
            urlconn.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
