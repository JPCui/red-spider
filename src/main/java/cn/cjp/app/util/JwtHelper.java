package cn.cjp.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import cn.cjp.app.exception.ServiceException;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class JwtHelper {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);

    public static String encode(String secret, Map<String, Object> payloads) {
        String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, base64Secret).setClaims(payloads)
            .compressWith(CompressionCodecs.DEFLATE).compact();
    }

    /**
     * @return map
     * @throws SignatureException 签名失败（1. secret 错误；2. claimsJws 篡改；）
     */
    public static Map<String, Object> decode(String secret, String claimsJws) {
        try {
            String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());
            Map<String, Object> loginUserMap = Jwts.parser().setSigningKey(base64Secret).parseClaimsJws(claimsJws)
                .getBody();
            return loginUserMap;
        } catch (Exception e) {
            throw new ServiceException(String.format("验签失败: secret=%s, claims=%s", secret, claimsJws), e);
        }
    }

    /**
     * 验证签名
     */
    public static boolean signed(String secret, String claimsJws) {
        try {
            decode(secret, claimsJws);
            return true;
        } catch (SignatureException e) {
            LOGGER.warn("验签失败 {} {}", secret, claimsJws);
        } catch (Exception e) {
            LOGGER.error("验签失败", e);
        }
        return false;
    }

    public static void main(String[] args) {
        String secret = "secret01";
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", 1);
        String jwt = encode(secret, payloads);
        System.out.println(signed(secret, jwt));
        System.out.println(decode(secret, jwt));

        String secret2 = "fait_secret";
        System.out.println(signed(secret2, jwt));
        System.out.println(decode(secret2, jwt));

    }

}
