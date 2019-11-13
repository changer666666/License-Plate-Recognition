package com.example.a123.lpr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2017/7/16.
 */

public class MainActivity extends Activity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO = 3;
    public static final int RENEWINFO = 1;
    public static final int TOASTUPLOAD = 2;
    public static final int TOASTUPSUCC = 3;
    public static final int TOASTUPFAIL = 4;
    private static final int REQUEST_CAPTURE = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int SHOWCAR = 5;
    //public static final int TOASTUPLOAD = 2;

    private Button TakePhoto;
    private Button ChoosePhoto;
    private Button upload;
    private ImageView picture;
    private Uri imageUri;
    private TextView TextInfo;
    private String actionUrl = "http://123.206.16.144";
    //private String actionUrl = "http://10.151.4.205:10007";
    //private String actionUrl = "http://192.168.1.111:12001";
    private Map<String, String> userInfo = new HashMap<String, String>();

    //Baidu地图的组件
    public LocationClient mLocationClient;
    private Uri uritempFile = null;
    public static String carNumber;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        ChoosePhoto = (Button) findViewById(R.id.btnChoosePhoto);
        upload = (Button) findViewById(R.id.upload);
        picture = (ImageView) findViewById(R.id.picture);
        TextInfo = (TextView) findViewById(R.id.textView);

        TakePhoto.setOnClickListener(new MainActivity.MyClickListener());
        ChoosePhoto.setOnClickListener(new MainActivity.MyClickListener());
        upload.setOnClickListener(new MainActivity.MyClickListener());

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

        TelephonyManager telemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //userInfo.put("CurrentTime", getDateTime());
        userInfo.put("Username",telemanager.getDeviceId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    showPicture(imageUri);
//                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(imageUri, "image/*");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    Log.d("imageurl:",imageUri.toString());
                    cropPhoto(imageUri);

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    showPicture(imageUri);
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(imageUri, "image/*");
                    cropPhoto(imageUri);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK)
                    imageUri = uritempFile;
                    showPicture(imageUri);
                break;
            default:
                break;
        }
    }


    class MyClickListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnTakePhoto: {
                    imageUri = checkPremission();
                    break;
                }
                case R.id.btnChoosePhoto: {
                    choosePhoto();
                    break;
                }
                case R.id.upload: {
                    //更新用户信息：时间，位置
//                    mLocationClient = new LocationClient(getApplicationContext());
//                    mLocationClient.registerLocationListener(new MyLocationListener());
                    userInfo.put("CurrentTime", getDateTime());
                    new Thread() {
                        public void run() {
                            Message messageUpload = new Message();
                            messageUpload.what = TOASTUPLOAD;
                            handerUI.sendMessage(messageUpload);
                            try {
                                //上传图片
                                //Upload upload = new Upload();
                                int UpIndex = Upload.post(imageUri, actionUrl, getUserInfo());
                                //Upload.socketand(imageUri,actionUrl,getUserInfo());

                                if (UpIndex == 0) {
                                    Message messageSucc = new Message();
                                    messageSucc.what = TOASTUPSUCC;
                                    handerUI.sendMessage(messageSucc);
                                    Message messageRenew = new Message();
                                    messageRenew.what = RENEWINFO;
                                    handerUI.sendMessage(messageRenew);
                                } else {
                                    Message messageSucc = new Message();
                                    messageSucc.what = TOASTUPFAIL;
                                    handerUI.sendMessage(messageSucc);
                                }
                            } catch (Exception e) {
                                Log.i("uploadException",e.toString());
                            }
                        }
                    }.start();
                    break;
                }
            }
        }
    }

    private Uri checkPremission() {
        final String permission = Manifest.permission.CAMERA;  //相机权限
        final String permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE; //写入数据权限
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission1) != PackageManager.PERMISSION_GRANTED) {  //先判断是否被赋予权限，没有则申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {  //给出权限申请说明
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            } else { //直接申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE); //申请权限，可同时申请多个权限，并根据用户是否赋予权限进行判断
            }
        } else {  //赋予过权限，则直接调用相机拍照
            return takePhoto();
        }
        return null;
    }

    public Uri takePhoto() {
        // 创建File对象，用于存储拍照后的图片
        Log.i("TAKE_PHOTO", "Start");
        File imageFile = createFile("Tempimage.jpg");
        //imageUri = Uri.fromFile(imageFile);

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,"com.example.a123.lpr.FileProvider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定Uri
        startActivityForResult(intent, TAKE_PHOTO);//启动拍照

        return imageUri;
    }

    public void cropPhoto(Uri imageUri) {
        Log.i("crop photo", "Start");

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        //是否可裁剪
        intent.putExtra("crop", "true");
//        //裁剪器高宽比
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("aspectX", 1);
//        //设置裁剪框高宽
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
        //返回数据
        //intent.putExtra("return-data", true);
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "Tempimage.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent,CROP_PHOTO);

    }

    public Uri choosePhoto(){
        Log.i("CHOOSE_PHOTO", "Start");
        imageUri = Uri.fromFile(createFile("Tempimage.jpg"));//imageFile：/storage/emulated/0/Tempimage.jpg
        File imageFile = createFile("Tempimage.jpg");//imageUri：file:///storage/emulated/0/Tempimage.jpg
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,"com.example.a123.lpr.FileProvider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "Tempimage.jpg");//uritempFile：file:////storage/emulated/0/Tempimage.jpg
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        //intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setDataAndType(imageUri, "image/*");//imageUri：content://com.example.a123.lpr.FileProvider/external_storage_root/Tempimage.jpg
//        Uri chooseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        mBitmap = getBitmapFromUri(chooseUri);
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(imageFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        try {
//            out.flush();
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        startActivityForResult(intent, CHOOSE_PHOTO);
        return imageUri;
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int picWidth = options.outWidth;
            int picHeight = options.outHeight;
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            options.inSampleSize = 1;
            if (picWidth > picHeight) {
                if (picWidth > screenWidth)
                    options.inSampleSize = picWidth / screenWidth;
            } else {
                if (picHeight > screenHeight)
                    options.inSampleSize = picHeight / screenHeight;
            }
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public void showPicture(Uri picUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picUri));
            picture.setImageBitmap(bitmap); // 将照片显示出来
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File createFile(String picturename) {
        File outputImage = new File(Environment.getExternalStorageDirectory(), picturename);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "创建图片文件时出错", Toast.LENGTH_SHORT).show();
        }
        return outputImage;
    }

//    public String getDeviceID(TelephonyManager telemanager) {
//        //TelephonyManager telemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        return telemanager.getDeviceId();
//    }

    public static String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public Map getUserInfo() {
        Log.i("userinfo", userInfo.toString());
        return userInfo;
    }

//    public void showCarNumber(){
//        Message messageSucc = new Message();
//        messageSucc.what = SHOWCAR;
//        handerUI.sendMessage(messageSucc);
//    }

    public Handler handerUI = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RENEWINFO:
                    for (Map.Entry<String, String> entry : userInfo.entrySet()) {
                        TextInfo.append("\n" + entry.getKey() + " : ");
                        try   {
                            String enUft = URLEncoder.encode(entry.getValue());
                            java.net.URLDecoder urlDecoder   =   new   java.net.URLDecoder();
                            String   s   =     urlDecoder.decode(enUft,"UTF-8");
                            TextInfo.append(s);
                        }   catch   (Exception   e)   {
                            e.printStackTrace();
                        }
                    }
                    TextInfo.append("\n" + "识别车牌号 ： " + carNumber + "\n");
                    break;
                case TOASTUPLOAD:
                    Toast.makeText(getApplicationContext(), "Upload Start", Toast.LENGTH_SHORT).show();
                    break;
                case TOASTUPSUCC:
                    Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                    break;
                case TOASTUPFAIL:
                    Toast.makeText(getApplicationContext(), "Upload Fail.Please upload again.", Toast.LENGTH_SHORT).show();
                    break;
                case SHOWCAR:
                    // TODO: 2018/4/13
                    Bundle bundle = msg.getData();
                    String carNumber = bundle.getString("carNumber");
                    TextInfo.append("carNumber :" + carNumber + "\n");
                default:
                    break;
            }
        }

    };



//    public void showCarNumber(final String carNumber){
//        new Thread() {
//            public void run() {
//                Looper.prepare();
//                Message messageShowCar = new Message();
//                messageShowCar.what = SHOWCAR;
//                Bundle bundle = new Bundle();
//                bundle.putString("carNumber",carNumber);
//                messageShowCar.setData(bundle);
//                handerUI.sendMessage(messageShowCar);
//                Looper.loop();
//            }
//        }.start();
//    }
    public void showCarNumber(final String carNumber){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Looper.prepare();
//                Message messageShowCar = new Message();
//                messageShowCar.what = SHOWCAR;
//                Bundle bundle = new Bundle();
//                bundle.putString("carNumber",carNumber);
//                messageShowCar.setData(bundle);
//                handerUI.sendMessage(messageShowCar);
                TextInfo.append("carNumber :" + carNumber + "\n");
                Looper.loop();

            }
        }).start();
    }



    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        //mapView.onDestroy();
        //baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            //currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            //currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            currentPosition.append(location.getCountry());
            currentPosition.append(location.getProvince());
            currentPosition.append(location.getCity());
            currentPosition.append(location.getDistrict());
            currentPosition.append(location.getStreet());
            //currentPosition.append("定位方式：");

//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                currentPosition.append("GPS");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                currentPosition.append("网络");
//            }

            Map LocationInfo = new HashMap();
            LocationInfo.put("纬度",String.valueOf(location.getLatitude()));
            LocationInfo.put("经度",String.valueOf(location.getLongitude()));
            LocationInfo.put("地址",String.valueOf(currentPosition));

            System.out.print(currentPosition);
            //positionText.setText(currentPosition);
            userInfo.putAll(LocationInfo);

        }

    }

}


