package com.example.a123.lpr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lc.photoupload.R;
import java.io.File;

/**
 * Created by LC on 2017/8/26.
 */

        public class PhotoUpload extends Activity {
        private String newName ="image.jpg";
        private String uploadFile ="/storage/emulated/0/1/pic.jpg";
        private String actionUrl ="http://123.206.16.144";
        private TextView mText1;
        private TextView mText2;
        private Button mButton;
        private ImageView picture;
        public void onCreate(Bundle savedInstanceState)
      {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_main);

              File imafile = new File(actionUrl);
              picture = (ImageView) findViewById(R.id.picture);
              if(imafile.exists())
              {
                      Bitmap imabit = BitmapFactory.decodeFile(actionUrl);
                      picture.setImageBitmap(imabit);
              }
              else
              {
                      Log.i("image","cannot find");
              }
              mText1 = (TextView) findViewById(R.id.myText2);
              //"文件路径：\n"+
              mText1.setText(uploadFile);
              mText2 = (TextView) findViewById(R.id.myText3);
              ///"上传网址：\n"+
              mText2.setText(actionUrl);
        /* 设置mButton的onClick事件处理 */
              mButton = (Button) findViewById(R.id.myButton);
              mButton.setOnClickListener(new View.OnClickListener()
              {
                        public void onClick(View v)
                        {
                                //uploadFile();
                        }
              });
      }
//
//    private void uploadString()
//    {
//        String TAG = "UPload";
//        private void requestPost(HashMap<String, String> paramsMap) {
//        try {
//            String actionUrl = "https://xxx.com/getUsers";
//            //合成参数
//            StringBuilder tempParams = new StringBuilder();
//            int pos = 0;
//            for (String key : paramsMap.keySet()) {
//                if (pos > 0) {
//                    tempParams.append("&");
//                }
//                tempParams.append(String.format("%s=%s", key,  URLEncoder.encode(paramsMap.get(key),"utf-8")));
//                pos++;
//            }
//            String params =tempParams.toString();
//            // 请求的参数转换为byte数组
//            byte[] postData = params.getBytes();
//            // 新建一个URL对象
//            URL url = new URL(actionUrl);
//            // 打开一个HttpURLConnection连接
//            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//            // 设置连接超时时间
//            urlConn.setConnectTimeout(5 * 1000);
//            //设置从主机读取数据超时
//            urlConn.setReadTimeout(5 * 1000);
//            // Post请求必须设置允许输出 默认false
//            urlConn.setDoOutput(true);
//            //设置请求允许输入 默认是true
//            urlConn.setDoInput(true);
//            // Post请求不能使用缓存
//            urlConn.setUseCaches(false);
//            // 设置为Post请求
//            urlConn.setRequestMethod("POST");
//            //设置本次连接是否自动处理重定向
//            urlConn.setInstanceFollowRedirects(true);
//            // 配置请求Content-Type
//            urlConn.setRequestProperty("Content-Type", "application/json");
//            // 开始连接
//            urlConn.connect();
//            // 发送请求参数
//            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
//            dos.write(postData);
//            dos.flush();
//            dos.close();
//            // 判断请求是否成功
//            if (urlConn.getResponseCode() == 200) {
//                // 获取返回的数据
//                String result = streamToString(urlConn.getInputStream());
//                Log.e(TAG, "Post方式请求成功，result--->" + result);
//            } else {
//                Log.e(TAG, "Post方式请求失败");
//            }
//            // 关闭连接
//            urlConn.disconnect();
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//    }
//    }
      /* 上传文件至Server的方法 */
//      private void uploadFile()
//      {
//              Log.i("uploadfile","UploadStart");
//              String end ="\r\n";
//              String twoHyphens ="--";
//              String boundary ="*****";
//              try
//              {
//              URL url =new URL(actionUrl);
//              HttpURLConnection con=(HttpURLConnection)url.openConnection();
//          /* 允许Input、Output，不使用Cache */
//              con.setDoInput(true);
//              con.setDoOutput(true);
//              con.setUseCaches(false);
//          /* 设置传送的method=POST */
//              con.setRequestMethod("POST");
//          /* setRequestProperty */
//              con.setRequestProperty("Connection", "Keep-Alive");
//              con.setRequestProperty("Charset", "UTF-8");
//              con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
//          /* 设置DataOutputStream */
//              DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//              ds.writeBytes(twoHyphens + boundary + end);
//              ds.writeBytes("Content-Disposition: form-data; "+ "name=\"file1\";filename=\""+ newName +"\""+ end);
//              ds.writeBytes(end);
//          /* 取得文件的FileInputStream */
//              FileInputStream fStream =new FileInputStream(uploadFile);
//                      Log.i("fStream",fStream.toString());
//          /* 设置每次写入1024bytes */
//              int bufferSize =1024;
//              byte[] buffer =new byte[bufferSize];
//              int length =-1;
//          /* 从文件读取数据至缓冲区 */
//              while((length = fStream.read(buffer)) !=-1)
//              {
//            /* 将资料写入DataOutputStream中 */
//                ds.write(buffer, 0, length);
//              }
//              ds.writeBytes(end);
//              ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//          /* close streams */
//              fStream.close();
//              ds.flush();
//          /* 取得Response内容 */
//              InputStream is = con.getInputStream();
//              int ch;
//              StringBuffer b =new StringBuffer();
//              while( ( ch = is.read() ) !=-1 )
//              {
//                b.append( (char)ch );
//              }
//          /* 将Response显示于Dialog */
//              showDialog("上传成功"+b.toString().trim());
//          /* 关闭DataOutputStream */
//              ds.close();
//              }
//              catch(Exception e)
//              {
//              showDialog("上传失败"+e);
//              }
//      }
      /* 显示Dialog的method */
              private void showDialog(String mess)
              {
              new AlertDialog.Builder(PhotoUpload.this).setTitle("Message")
              .setMessage(mess)
              .setNegativeButton("确定",new DialogInterface.OnClickListener()
              {
              public void onClick(DialogInterface dialog, int which)
              {
              }
              })
              .show();
              }
              }