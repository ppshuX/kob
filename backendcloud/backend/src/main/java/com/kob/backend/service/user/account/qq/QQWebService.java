package com.kob.backend.service.user.account.qq;

import com.alibaba.fastjson.JSONObject;

public interface QQWebService {
    JSONObject applyCode();
    JSONObject receiveCode(String code,String state);
}