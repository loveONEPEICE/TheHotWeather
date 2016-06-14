package com.lhl.test.util;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
