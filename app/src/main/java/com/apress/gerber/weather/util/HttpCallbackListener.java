package com.apress.gerber.weather.util;

/**
 * Created by Administrator on 2016/9/18.
 */
public interface HttpCallbackListener {
    void onFinish(String responer);
    void onError(Exception e);
}
