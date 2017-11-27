package com.doudou;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.awt.print.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
*打印类
*@author 豆豆
*时间:
*/
@ServerEndpoint(value = "/print/{orientation}")
public class MyPrint {

    private int orientation = 0;
    private PageFormat pf = new PageFormat();

    /**
     * 有连接时触发的函数
     * @param session
     */
    @OnOpen
    public boolean onOpen(Session session, @PathParam("orientation") String param){
        orientation = Integer.valueOf(param);
        System.out.println("the webSocket is get connection");
        return true;
    }

    /**
     * 连接关闭时调用的方法
     */
    @OnClose
    public void onClose(){
        System.out.println("disconnect webSocket ");

    }

    /**
     * 收到消息时调用的函数
     * @param msg
     * @param session
     */
    @OnMessage
    public void onMessage(String msg, Session session){
        System.out.println("received message: " + msg);
        drawImageByUrl(msg, orientation);
        //drawImage(msg, orientation);
    }

    /***
     * 发生意外错误时调用的方法
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("happen some error");
    }


    public static void drawImage(String fileName, int orientation){
        try {
            DocFlavor dof = null;
            //根据用户选择不同的图片格式获得不同的打印设备
            if(fileName.endsWith(".gif")){
                //gif
                dof = DocFlavor.INPUT_STREAM.GIF;
            }else if(fileName.endsWith(".jpg")){
                //jpg
                dof = DocFlavor.INPUT_STREAM.JPEG;
            }else if(fileName.endsWith(".png")){
                //png
                dof = DocFlavor.INPUT_STREAM.PNG;
            }
            //字节流获取图片信息

            FileInputStream fin = new FileInputStream(fileName);
            //获得打印属性
            AttributeSet attr = new HashAttributeSet();
            attr.add(ColorSupported.SUPPORTED);

            //设置打印属性
            DocAttributeSet docAttribute = new HashDocAttributeSet();
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            //每一次默认打印一页
            pras.add(new Copies(1));
            //MediaSize ms = new MediaSize(102, 152, Size2DSyntax.MM, MediaSizeName.INVOICE);
            //pras.add(ms.getMediaSizeName());


            //pras.add(MediaSizeName.ISO_A5);
            //设置横向打印
            if (orientation == 0){
                pras.add(OrientationRequested.LANDSCAPE);
                docAttribute.add(new MediaPrintableArea(1000, 1000, 97, 147, MediaPrintableArea.MM));
            }else {
                docAttribute.add(new MediaPrintableArea(0, 0, 97, 147, MediaPrintableArea.MM));
            }

            //获得打印设备 ，字节流方式，图片格式
            PrintService pss[] = PrintServiceLookup.lookupPrintServices(dof,attr);
            //如果没有获取打印机
            if (pss.length == 0){
                //终止程序
                return;
            }
            //获取第一个打印机
            PrintService ps = null;
            for (PrintService p : pss){
                if (p.getName().contains("EPSON L805")){
                    ps = p;
                }
            }
            System.out.println("Printing image..........." + ps);
            //获得打印工作
            DocPrintJob job = ps.createPrintJob();

            //设置打印内容
            Doc doc = new SimpleDoc(fin,dof, docAttribute);

            //出现设置对话框
            //PrintService service =ServiceUI .printDialog(null, 50, 50, pss, ps,dof, attr);
            //if(service!=null){
                //开始打印
                job.print(doc,  pras);
                fin.close();
           // }
        }
        catch (IOException | PrintException ie) {
            //捕获io异常
            ie.printStackTrace();
        }
    }


    /***
     * 通过图片的url来打印图片
     * @param imageUrl
     */
    private static void drawImageByUrl(String imageUrl, int orientation){
        try {
            DocFlavor dof = null;
            //根据用户选择不同的图片格式获得不同的打印设备
            if(imageUrl.endsWith(".gif")){
                //gif
                dof = DocFlavor.INPUT_STREAM.GIF;
            }else if(imageUrl.endsWith(".jpg")){
                //jpg
                dof = DocFlavor.INPUT_STREAM.JPEG;
            }else if(imageUrl.endsWith(".png")){
                //png
                dof = DocFlavor.INPUT_STREAM.PNG;
            }
            //获得互联网上的
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //设置超时时间
            connection.setConnectTimeout(1000 * 5);
            //字节流获取图片信息
            InputStream is = connection.getInputStream();
            //获得打印属性
            AttributeSet attr = new HashAttributeSet();
            attr.add(ColorSupported.SUPPORTED);

            //设置打印属性
            DocAttributeSet docAttribute = new HashDocAttributeSet();
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            //每一次默认打印一页
            pras.add(new Copies(1));
            //pras.add(MediaSizeName.ISO_A6);
            //设置横向打印
            if (orientation == 0){
                pras.add(OrientationRequested.LANDSCAPE);
                docAttribute.add(new MediaPrintableArea(1000, 1000, 97, 147, MediaPrintableArea.MM));
            }else {
                docAttribute.add(new MediaPrintableArea(0, 0, 97, 147, MediaPrintableArea.MM));
            }



            //获得打印设备 ，字节流方式，图片格式
            PrintService pss[] = PrintServiceLookup.lookupPrintServices(dof,pras);
            //如果没有获取打印机
            if (pss.length == 0){
                //终止程序
                return;
            }

            //获取可用的打印机
            PrintService ps = null;
            for (PrintService p : pss){
                if (p.getName().contains("EPSON L805")){
                    ps = p;
                }
            }
            System.out.println("Printing image..........." + ps);
            //获得打印工作
            DocPrintJob job = ps.createPrintJob();


            //设置打印内容
            Doc doc = new SimpleDoc(is,dof, docAttribute);
            //开始打印
            job.print(doc, pras);
            is.close();
            // }
        }
        catch (IOException | PrintException ie) {
            //捕获io异常
            ie.printStackTrace();
        }
    }

}
