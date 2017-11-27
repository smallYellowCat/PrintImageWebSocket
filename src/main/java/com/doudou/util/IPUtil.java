package com.doudou.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
*网络工具类
*@author 豆豆
*时间:
*/
public class IPUtil {
    public static String getMacAddress(){
        String macAddress = "";
        InetAddress ia;
        try {
            ia = InetAddress.getLocalHost();
            //获取网卡，获取地址
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++){
                if (i != 0){
                    sb.append("-");
                }
                //字节转换为整数
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                System.out.println("每八位：" + str);
                if (str.length() == 1){
                    sb.append("0" + str);
                }else {
                    sb.append(str);
                }
            }
            macAddress = sb.toString().toUpperCase();
            System.out.println("MacAddress is : " + macAddress);

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        return macAddress;
    }
}
