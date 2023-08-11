package com.xuegao.PayServer.data;

public class ANHMResultVo {
    public  String message;
    public int statusCode;
    public String data;


    public ANHMResultVo(String message, String data, int statusCode) {
        super();
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    public ANHMResultVo(String message, int statusCode) {
        super();
        this.message = message;
        this.statusCode = statusCode;
    }
}
