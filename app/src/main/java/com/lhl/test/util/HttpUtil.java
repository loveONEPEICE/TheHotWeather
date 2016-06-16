package com.lhl.test.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
//        开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    Reader readerIn = new InputStreamReader(in);
                    BufferedReader reader = new BufferedReader(readerIn);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        /**原方法为
                         * response.append(line);
                         * */
                        response.append(line);
                    }
                    if (listener != null){
//                        设置回调方法onFinish()
                        /**原方法为
                         * listener.onFinish(response.toString());
                         * */
                        listener.onFinish(response.toString());

                    }
                } catch (Exception e) {
//                   设置回调方法onError()
                    listener.onError(e);
                }
                finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
