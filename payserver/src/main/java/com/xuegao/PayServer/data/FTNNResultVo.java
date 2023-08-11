package com.xuegao.PayServer.data;

import com.alibaba.fastjson.JSON;

public class FTNNResultVo {

    public final int status;
    public final String code;
    public final String money;
    public final String gamemoney;
    public final String msg;

    public FTNNResultVo(Builder builder) {
        status = builder.status.getStatus();
        code = builder.code;
        money = String.valueOf(builder.money);
        gamemoney = String.valueOf(builder.gamemoney);
        msg = builder.msg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public enum Status {
        EXCEPTION(1), SUCCESS(2), FAILED(3);

        private final int status;

        Status(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    public static class Builder {
        private Status status;
        private String code;
        private int money;
        private int gamemoney;
        private String msg;

        public Builder status(Status val) {
            status = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder money(int val) {
            money = val;
            return this;
        }

        public Builder gamemoney(int val) {
            gamemoney = val;
            return this;
        }

        public Builder msg(String val) {
            msg = val;
            return this;
        }

        public FTNNResultVo build() {
            if (status == Status.SUCCESS || status == Status.FAILED) {
                code = null;
            }
            return new FTNNResultVo(this);
        }
    }
}
