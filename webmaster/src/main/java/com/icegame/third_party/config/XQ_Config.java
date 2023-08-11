package com.icegame.third_party.config;

import java.util.HashMap;
import java.util.Map;

public class XQ_Config {

    // 游戏方的密钥
    public static final String GAME_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALxbvY7Dq4oUUrg9mYzTlMel9xBI479fQQF84BGZMri70NedLKG4cDG8pqsJmvQGxOwi7J2/QbMiRUBd6yuTc1qVqCZ92owft+hhraLafOF8T6Du4i2KkvhVJCdAY34kZuz/lOU5y58XCUUVGDzNApqGtklYXTKMY2jhk2/60ITTAgMBAAECgYB6KaH6muuBpYa02bbiAEPpbLmdhTi44MWfemc04sBj0eQ0Q0s0JBQYHfuWyKcIB+/mGwyNjwLEdMCna4JgA8T26Wvtzip4R3Wlv6ugE0nEeW+N2ipvEYbGrKhXz4WTAJ1LElL3tFRtt4qrFCX1tuVjmPV1utnfOQFUJBvhJfrn6QJBANwLp921Kkp06gLgO3I0lWvoBjMoODKsNfc5kDW0IvK59ACfl091QK965LlyO+fKY8w7R4PvtPIqk1Fh5jksGH8CQQDbIqIvCSmd0Oh5t1HdPx0e+UbTNrOd1t6tcpaoNy+NMlmS/KPVzHtmB7JE+/38u2fqKzmG54M13c96Qh4NGImtAkEAgAqMQJdlp3PGo6vFC5yLggG+cdAqe1n4AQbO4mESoPkRgbdbvZG19SZmp35QW31KexHXeG9odC3QEWANLiF5kwJBALYncuNgXWym9CgBH5am2QEfOyVlSidBLjbFksBfpzJakSTixxl4cXbdnO1E/tqvuXBo39fo6CPFrqq0/R/rrr0CQHXepBqsFEzEpO/YS3MR/7uSTlwe/BQH/fvC6cQPt7UUgIG9ud77iiBoZCodIb62ceCOACQBAGjFViP1EYgpiW4=";
    public static final String GAME_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8W72Ow6uKFFK4PZmM05THpfcQSOO/X0EBfOARmTK4u9DXnSyhuHAxvKarCZr0BsTsIuydv0GzIkVAXesrk3NalagmfdqMH7foYa2i2nzhfE+g7uItipL4VSQnQGN+JGbs/5TlOcufFwlFFRg8zQKahrZJWF0yjGNo4ZNv+tCE0wIDAQAB";

    // 小七的密钥
    public static final Map<String,String> XQ_SECRET = new HashMap<>();
    static {
        /*
         * 小7相关文档：https://alidocs.dingtalk.com/i/team/pyjzZV4xqk2oLzwx/docs/pyjzZavgrZDvpGwx# 「小7游戏密钥相关参数」
         * key: 小7提供的富豪模拟器项目的 AppKey
         * value: 小7提供的富豪模拟器项目的 RSA公钥
         */
        XQ_SECRET.put("等待费扬提供", "等待费扬提供");
    }
}
