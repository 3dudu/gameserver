package com.icegame.third_party.service;

import com.alibaba.fastjson.JSONObject;
import com.icegame.third_party.entity.XQRequest;
import com.icegame.third_party.entity.XQResponse;
import com.icegame.third_party.entity.XQShop;

/**
 * 小七业务处理
 */
public interface XQService {

    XQResponse dealArgs(XQRequest request);

    JSONObject xqShop(XQShop shop);
}
