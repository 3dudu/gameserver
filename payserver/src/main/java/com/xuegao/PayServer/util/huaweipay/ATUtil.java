package com.xuegao.PayServer.util.huaweipay;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ATUtil {

    public static String httpPost(String httpUrl, String contentType, String data, int connectTimeout, int readTimeout,
                                  Map<String, String> headers) throws IOException {
        OutputStream output = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", contentType);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();

            // POST data
            output = urlConnection.getOutputStream();
            output.write(data.getBytes("UTF-8"));
            output.flush();

            // read response
            if (urlConnection.getResponseCode() < 400) {
                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }

            inputStreamReader = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder strBuf = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                strBuf.append(str);
            }
            return strBuf.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
