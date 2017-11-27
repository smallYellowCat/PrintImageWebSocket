package com.doudou;

import com.doudou.util.IPUtil;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

/**
*获取macAddress的服务
*@author 豆豆
*时间:
*/
@ServerEndpoint("/net")
public class MacAddress {

    @OnOpen
    public boolean onOpen(){
       boolean result = false;
       return result;
    }

    @OnClose
    public void onClose(){
        System.out.println("disConnection");
    }

    @OnMessage
    public String onMessage(String msg){
        System.out.println("received message :" + msg);
        return IPUtil.getMacAddress();
    }

    @OnError
    public void onError(Throwable throwable){
        System.out.println("happen some error!");
    }
}
