package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.jsonwebtoken.*;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 参考https://blog.csdn.net/wpf199402076118/article/details/99677412?tdsourcetag=s_pctim_aiomsg
 */
public class AppleLoginService extends AbsLoginService {

    private static final String KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String TOKEN_URL = "https://appleid.apple.com/auth/token";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        Constants.PlatformOption.AppleLogin config = getConfig();
        if (parameters.length == 7) {
            String channel = parameters[0];
            String userId = parameters[2];//授权的用户唯一标识
            String email = parameters[3];//授权的用户资料
            String fullName = parameters[4];
            String authorizationCode = parameters[5];//授权code
            String identityToken = parameters[6];//授权用户的JWT凭证
            try {
                //构建公钥
                String result= RequestUtil.request(KEYS_URL);
                logger.info("auth/keys返回:"+result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONArray jsonArray = JSONObject.parseArray(jsonObject.getString("keys"));
                jsonObject = (JSONObject) jsonArray.get(0);
                String n = jsonObject.getString("n");
                String e = jsonObject.getString("e");
                PublicKey publicKey = getPublicKey(n, e);
//              {iss=https://appleid.apple.com, aud=com.share.account, exp=1574503407, iat=1574502807, sub=000894.c4f995e136a24910922a5c26055cd2e2.0311,
//               c_hash=voq9GQUa9BtDOc-pb22MMA, email=prit8mmwnz@privaterelay.appleid.com, email_verified=true, is_private_email=true, auth_time=1574502807}
                Claims claims = verify(publicKey, identityToken, jsonObject.getString("aud"), jsonObject.getString("sub"));
                if (claims!=null) {
                    String aud = claims.getAudience();//aud是接收者的APPID
                    String sub = claims.getSubject();//sub就是用户的唯一标识
                    if (config.apps.get(channel).appId.equals(aud)&&userId.equals(sub)) {
                        return openUserBase(userId, getPlatformName(), channel);
                    }
                }
            } catch (Exception ex) {
                logger.info(ex.getMessage());
            }
        }
        return null;
    }

    /**
     * 首先通过identityToken中的header中的kid，然后结合苹果获取公钥的接口，拿到相应的n和e的值，然后通过下面这个方法构建RSA公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(String modulus, String publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(1, Base64.decodeBase64(modulus));
        BigInteger bigIntPrivateExponent = new BigInteger(1, Base64.decodeBase64(publicExponent));
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
       String  n = "1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw";
       String  e = "AQAB";
       String identityToken  = "eyJraWQiOiJXNldjT0tCIiwiYWxnIpublicKey = {RSAPublicKeyImpl@671} \"Sun RSA public key, 2048 bits\\n  modulus: 26837761433711014721832376041491173543198014482902155322125124132477822116019369065009328031568542846344527404015338748512130702442103466578780112808745243839507606664533355887978327226009424856671183388073910629664508637011436835446595578427107808884459021514040948108194644738451737374678543193913652158943168543900623570598772691813365156974731694769809518729168211127564130998297312394062002945816964628014619231444061471271169115279029120029561991306302611167415358427242594480022929511108427322287410797997256678386266551956177049770986789443740946228306642909604702116867234020749463750394134669409699009885211\\n  public exponent: 65537\"... ViewjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnN1bmljZS5zYW11cmFpaW9zIiwiZXhwIjoxNjgzNDUzMjY5LCJpYXQiOjE2ODMzNjY4NjksInN1YiI6IjAwMDY2Mi5hNTYyN2U3NTJjNmQ0MmY1YThiMjVmNTYxNTlmMjI2Mi4wOTU1IiwiY19oYXNoIjoibzNhbVhwN3ZhblZ4Y3JrOGtXV1BZZyIsImVtYWlsIjoicHJ5aGo3dDZnZEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTY4MzM2Njg2OSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.G1pKWQObLVDidl0iq6DLt7koEeRJ0NGUDaSceOOcvizqD5PFjl8x60sh4x8NFYYhT8PjEqujmCUdl88UteDnxHPrIOMKJwnMZZz_XNVpffvyesHU2NhzV9BZ5l82SIHXQgDS14f4sAzlITdpuw6C4h9NLwNI4Au9KxxagV6H08axuIT_TOqTeQk04dmTgzxBTf55AlHpCDpXuSsZCENa4qmKGpAQmcx1qis3wj3gIloIvOVzH-paAu-871omon8C7MOafrWF9-VpZyGWHIdTd6S3wNzIC7z2IXVA0I_u1uTCyHhnVmh9_PdTrXRm8qCrBrVwBQsR9EUW4XM9PaVxKg";
        PublicKey publicKey = getPublicKey(n, e);
//              {iss=https://appleid.apple.com, aud=com.share.account, exp=1574503407, iat=1574502807, sub=000894.c4f995e136a24910922a5c26055cd2e2.0311,
//               c_hash=voq9GQUa9BtDOc-pb22MMA, email=prit8mmwnz@privaterelay.appleid.com, email_verified=true, is_private_email=true, auth_time=1574502807}
        Claims claims = verify(publicKey, identityToken, "com.sunice.samuraiios", "000662.a5627e752c6d42f5a8b25f56159f2262.0955");

        System.out.println("sub:" + claims.getSubject() + ",aud:" + claims.getAudience());
    }

    /**
     * 通过下面这个方法验证JWT的有效性
     *
     * @param key
     * @param jwt
     * @param audience
     * @param subject
     * @return
     */
    public static Claims verify(PublicKey key, String jwt, String audience, String subject) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(key);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(audience);
        jwtParser.requireSubject(subject);
        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                return claim.getBody();
            }
            return null;
        } catch (ExpiredJwtException e) {
            logger.info("apple identityToken expired", e);
            return null;
        } catch (Exception e) {
            logger.info("apple identityToken illegal", e);
            return null;
        }
    }


    /**
     * 构建client_secret
     *
     * @return
     */
    public String getClientSecret(String clientId, String kid, String teamId, String privatekey) {
        Map<String, Object> header = new HashMap<>();
        header.put("kid", kid); //参考后台配置
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", teamId); //iss标识是苹果签发的
        long now = System.currentTimeMillis() / 1000;
        claims.put("iat", now);
        claims.put("exp", now + 86400 * 30); // 最长半年，单位秒
        claims.put("aud", "https://appleid.apple.com");
        claims.put("sub", clientId);
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(readKey(privatekey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        return Jwts.builder().setHeader(header).setClaims(claims).signWith(SignatureAlgorithm.ES256, privateKey).compact();
    }

    public byte[] readKey(String privateKey) {
        return Base64.decodeBase64(privateKey);
    }


}
