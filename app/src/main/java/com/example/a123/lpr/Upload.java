package com.example.a123.lpr;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.input;

/**
 * Created by LC on 2017/9/1.
 */

//HttpURLConnection发送
public class Upload extends MainActivity {

    public static final int SUCCESSUPLOAD = 0;
    public static final int FAILUPLOAD = 1;
    private static final int SHOWCAR = 5;

    public static int post(Uri uploaduri, String uploadurl, Map userInfo) {
        Log.i("ImagePath", uploaduri.getPath().toString());
        Map<String, String> uploadInfo = userInfo;

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(uploadurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
          /* 设置传送的method=POST */
            con.setRequestMethod("POST");
          /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setConnectTimeout(1000000);
            con.setReadTimeout(1000000);
          /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());


//          //上传用户信息userInfo
//            for (Map.Entry<String, String> entry : uploadInfo.entrySet()) {
//                ds.writeBytes(twoHyphens + boundary + end);
//                ds.writeBytes("Content-Disposition: form-data; " + "name=");
//                ds.writeBytes("\"");
//                ds.writeBytes(entry.getKey());
//                ds.writeBytes("\"\n\n");
//                ds.writeBytes(URLEncoder.encode(entry.getValue()) + "\n");
//            }
           // ds.writeBytes(userInfoBuffer.toString());
            //ds.writeBytes(end);//写入文件的
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\"; filename=\"" + "Tempimage.jpg" + "\"" + end + end);
            File file = new File(uploaduri.getPath().replace("/raw",""));// 指定要读取的文件uploaduri：content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2Ftencent%2Fqqfile_recv%2F1.jpg
            //file:///storage/emulated/0/Tempimage.jpg
            //BufferedReader bufferedReader = new BufferedReader(new FileReader(file)); // 获得该文件的缓冲输入流Reader
            FileInputStream in = new FileInputStream(file);//file：/raw/storage/emulated/0/tencent/qqfile_recv/1.jpg
            byte buffer[] = new byte[1024];
            int count,i;
            while((count=in.read(buffer))!=-1){
                // for循环保证只写入count个byte, 否则会写入1024个byte
                for(i=0; i<count; i++) {
                    ds.write(buffer[i]);
                }
            }
            in.close();
            ds.writeBytes(end);

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
          /* 取得Response内容 */
            int response = con.getResponseCode();            //获得服务器的响应码
            StringBuffer b = new StringBuffer();

            Log.i("if",String.valueOf(response));
            if (response == HttpURLConnection.HTTP_OK) {
                Log.i("Response", "SuccessGetResponse");
                InputStream is = con.getInputStream();
//                int ch;
//                Log.i("if","1");
//                while ((ch = is.read()) != -1) {
//                    b.append((char) ch);
//                }
                carNumber = InputStreamTOString(is);

//                String str = c;
//                char[] strChar=str.toCharArray();
//                String result="";
//                for(int j=0;j<strChar.length;j++){
//                    result +=Integer.toBinaryString(strChar[j])+ " ";
//                }
//                carNumber = URLEncoder.encode(result,"utf-8");


          /* 将Response显示于Dialog */
                Log.i("上传成功", carNumber);

          /* 关闭DataOutputStream */
                ds.close();


                return SUCCESSUPLOAD;
            } else {
                Log.i("if","2");
                Log.i("上传失败", carNumber);
                return FAILUPLOAD;
            }
        } catch (Exception e) {
            Log.i("上传失败", e.toString());
        }
        Log.i("if","3");
        return FAILUPLOAD;
    }
    public static String InputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while((count = in.read(data,0,4096)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),"UTF-8");
    }


    //socket发送
    public static void socketand(Uri uploaduri, String uploadurl,Map userInfo) throws Exception{
        final int PORT= 12001;
        final String HOME = uploadurl;
        String BUFF = "--";

        Socket socket = null;
        DataOutputStream output = null;
        DataInputStream input = null;
        File file  = new File(uploaduri.getPath());
        Map<String, String> uploadInfo = userInfo;

            // 如果本系统为4.0以上（Build.VERSION_CODES.ICE_CREAM_SANDWICH为android4.0）
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // 详见StrictMode文档
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads().detectDiskWrites().detectNetwork()
                        .penaltyLog().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                        .penaltyLog().penaltyDeath().build());
            }

            try {
                // 连接服务器
                socket = new Socket(HOME, PORT);
                // 得到输出流
                output = new DataOutputStream(socket.getOutputStream());
                // 得到如入流
                input = new DataInputStream(socket.getInputStream());

            /* 取得文件的FileInputStream */
                FileInputStream fStream = new FileInputStream(uploaduri.getPath());

                String[] fileEnd = file.getName().split("\\.");

                output.writeUTF(BUFF + fileEnd[fileEnd.length - 1].toString());
                output.writeBytes(BUFF);
                for (Map.Entry<String, String> entry : uploadInfo.entrySet()) {
                    output.writeBytes(entry.getKey());
                    output.writeBytes("=");
                    output.writeBytes(URLEncoder.encode(entry.getValue()) );
                    output.writeBytes(",");
                }
                output.writeBytes(BUFF);
                System.out.println("buffer------------------" + BUFF
                        + fileEnd[fileEnd.length - 1].toString());

                //设置每次写入102400bytes
                int bufferSize = 102400;
                byte[] buffer = new byte[bufferSize];
                int length = 0;
                // 从文件读取数据至缓冲区（值为-1说明已经读完）
                while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                    output.write(buffer, 0, length);
                }

                // 一定要加上这句，否则收不到来自服务器端的消息返回
                socket.shutdownOutput();

            /* close streams */
                fStream.close();
                output.flush();

            /* 取得input内容 */
                String msg = input.readUTF();
                System.out.println("上传成功  文件位置为：" + msg);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
}

