package com.xuegao.LoginServer.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
public class TapTapUtils {



    public static void main(String[] args) throws IOException {
        String client_id = "0RiAlMny7jiz086FaU";
        String kid = "1/hC0vtMo7ke0Hkd-iI8-zcAwy7vKds9si93l7qBmNFxJkylWEOYEzGqa7k_9iw_bb3vizf-3CHc6U8hs-5a74bMFzkkz7qC2HdifBEHsW9wxOBn4OsF9vz4Cc6CWijkomnOHdwt8Km6TywOX5cxyQv0fnQQ9fEHbptkIJagCd33eBXg76grKmKsIR-YUZd1oVHu0aZ6BR7tpYYsCLl-LM6ilf8LZpahxQ28n2c-y33d-20YRY5NW1SnR7BorFbd00ZP97N9kwDncoM1GvSZ7n90_0ZWj4a12x1rfAWLuKEimw1oMGl574L0wE5mGoshPa-CYASaQmBDo3Q69XbjTsKQ"; // kid
        String mac_key = "mSUQNYUGRBPXyRyW"; // mac_key
        String method = "GET";
        String request_url = "https://openapi.taptap.com/account/profile/v1?client_id=" + client_id; //
        String authorization = getAuthorization(request_url, method, kid, mac_key);
        System.out.println(authorization);
        URL url = new URL(request_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Http
        conn.setRequestProperty("Authorization", authorization);
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        System.out.println(result.toString());
    }
    /**
     * @param request_url
     * @param method "GET" or "POST"
     * @param key_id key id by OAuth 2.0
     * @param mac_key mac key by OAuth 2.0
     * @return authorization string
     */
    public static String getAuthorization(String request_url, String method, String key_id, String
            mac_key) {
        try {
            URL url = new URL(request_url);
            String time = String.format(Locale.US, "%010d", System.currentTimeMillis() / 1000);
            String randomStr = getRandomString(16);
            String host = url.getHost();
            String uri = request_url.substring(request_url.lastIndexOf(host) + host.length());
            String port = "80";
            if (request_url.startsWith("https")) {
                port = "443";
            }
            String other = "";
            String sign = sign(mergeSign(time, randomStr, method, uri, host, port, other), mac_key);
            return "MAC " + getAuthorizationParam("id", key_id) + "," + getAuthorizationParam("ts", time)
                    + "," + getAuthorizationParam("nonce", randomStr) + "," + getAuthorizationParam("mac",
                    sign);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String getRandomString(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        String base64String = Base64.getEncoder().encodeToString(bytes);
        return base64String;
    }
    private static String mergeSign(String time, String randomCode, String httpType, String uri,
                                    String domain, String port, String other) {
        if (time.isEmpty() || randomCode.isEmpty() || httpType.isEmpty() || domain.isEmpty() || port.isEmpty())
        {
            return null;
        }
        String prefix =
                time + "\n" + randomCode + "\n" + httpType + "\n" + uri + "\n" + domain + "\n" + port
                        + "\n";
        if (other.isEmpty()) {
            prefix += "\n";
        } else {
            prefix += (other + "\n");
        }
        return prefix;
    }
    private static String sign(String signatureBaseString, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] text = signatureBaseString.getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = mac.doFinal(text);
            signatureBytes = Base64.getEncoder().encode(signatureBytes);
            return new String(signatureBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }
    private static String getAuthorizationParam(String key, String value) {
        if (key.isEmpty() || value.isEmpty()) {
            return null;
        }
        return key + "=" + "\"" + value + "\"";
    }
}