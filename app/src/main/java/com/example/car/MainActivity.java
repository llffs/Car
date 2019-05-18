package com.example.car;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.bmob.v3.Bmob;

import static com.example.car.BuildConfig.DEBUG;


public class MainActivity extends AppCompatActivity {

    private ImageButton middle;                          //喇叭
    private ImageButton left;                            //左转
    private ImageButton above;                           //前进
    private ImageButton right;                           //右转
    private ImageButton below;                           //后退
    private ImageView free;                            //自动行驶
    private ImageButton lanya;
    private ImageButton stop;

    boolean hex = false;

    private static final String TAG = "MainActivity";


    OutputStream outputStream = null;


    public static final int REC_DATA = 2;

    private TextView RecDataView;

    public static final int CONNECTED_DEVICE_NAME = 4;
    public static final int BT_TOAST = 5;
    public static final int MAIN_TOAST = 6;

    // 标志字符串常量
    public static final String DEVICE_NAME = "device name";
    public static final String TOAST = "toast";

    // 意图请求码
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int ENABLE_BLUETOOTH = 2;               //作为requestCode返回参数

    //蓝牙连接服务对象
    private BluetoothAdapter bluetoothAdapter;

    // 已连接设备的名字
    private String mConnectedDeviceName = null;

    BluetoothService mConnectService = null;
    BluetoothDevice bluetoothDevice;                //蓝牙设备
    BluetoothSocket bluetoothSocket = null;
    ;       //蓝牙接口和其他设备交换数据
    private String blueAddress = "98:D3:31:70:9B:CA";//蓝牙模块的MAC地址
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // UUID


    static boolean isHEXsend = false, isHEXrec = false;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "3238c409b127a913b011c099243c8769");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        init_hex_string_table();
        verifyStoragePermissions(this);

        middle = (ImageButton) findViewById(R.id.middle);
        left = (ImageButton) findViewById(R.id.left);
        right = (ImageButton) findViewById(R.id.right);
        above = (ImageButton) findViewById(R.id.above);
        below = (ImageButton) findViewById(R.id.below);
        free = (ImageView) findViewById(R.id.free);
        lanya = (ImageButton) findViewById(R.id.png2);
        stop = (ImageButton)findViewById(R.id.stop);

        ButtonListener bt = new ButtonListener();
        left.setOnTouchListener(bt);
        right.setOnTouchListener(bt);
        above.setOnTouchListener(bt);
        below.setOnTouchListener(bt);
        free.setOnTouchListener(bt);
        middle.setOnTouchListener(bt);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        assert toolbar != null;
        toolbar.setTitle("我的智能小车");
        toolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(toolbar);

        ((TextView) toolbar.findViewById(R.id.search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);

                }else {
                    Toast.makeText(MainActivity.this,"请连接蓝牙之后操作", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()){
                    switch (v.getId()) {
                        case R.id.png2: {
                            //Toast.makeText(MainActivity.this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    MainActivity.this);
                            builder.setMessage("蓝牙已开启，确认关闭吗？")
                                    .setTitle("提示");
                            //单击确认后触发事件
                            builder.setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (bluetoothSocket != null) {
                                                mConnectService.cancelAllBtThread();

                                            }

                                            dialog.dismiss();
                                            bluetoothAdapter.disable();
                                            Toast.makeText(MainActivity.this, "蓝牙已关闭！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            builder.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                            break;
                        }
                        default:
                            break;
                    }
                }else {
                        Toast.makeText(MainActivity.this, "蓝牙启动", Toast.LENGTH_SHORT).show();
                        new Thread() {
                            public void run() {
                                if (!bluetoothAdapter.isEnabled()) {
                                    bluetoothAdapter.enable();
                                }
                            }
                        }.start();
                    }
                    if (!bluetoothAdapter.isEnabled()) {
                        Toast.makeText(MainActivity.this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!bluetoothAdapter.isEnabled()) {
                                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enabler, ENABLE_BLUETOOTH);
                                }
                            }
                        }, 5000);
                }
            }
        });

        //退出登录
        ((TextView) toolbar.findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.exit: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this);
                        builder.setMessage("确认要退出吗？")
                                .setTitle("提示");
                        //单击确认后触发事件
                        builder.setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (bluetoothSocket != null) {
                                            mConnectService.cancelAllBtThread();
                                        }
                                        dialog.dismiss();
                                        MainActivity.this.finish();
                                        bluetoothAdapter.disable();
                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        builder.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                        break;
                    }
                    default:
                        break;
                }
                //MainActivity.this.finish();
            }
        });

        //自动行驶按钮
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                sendString("5");
            }
        });
    }



    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mConnectService != null) {
            if (mConnectService.getState() == BluetoothService.IDLE) {
                //监听其他蓝牙主设备
                mConnectService.acceptWait();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if(DEBUG) Log.i(TAG, "++ ON START ++");
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,ENABLE_BLUETOOTH);
        } //否则创建蓝牙连接服务对象
        else if (mConnectService == null){
            mConnectService = new BluetoothService(mHandler);
        }
}


    timeThread timeTask=null;
    private class timeThread extends Thread{
        private int sleeptime;
        timeThread(int militime){
            super();
            sleeptime=militime;
        }

        @Override
        public void run(){
            while(!isInterrupted()){
                if(DEBUG)Log.i("myDebug", "timeThread start");
//                sendMessage(null,sendContent.getText().toString());
                //mHandler.obtainMessage(MainActivity.REC_DATA,buffer.length,-1,buffer).sendToTarget();
                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            if(DEBUG)Log.i("myDebug", "timeThread end");
        }
    }

    String[] hex_string_table=new String[256];
    private void init_hex_string_table(){
        for(int i=0;i<256;i++){
            if(i<16){
                hex_string_table[i]=" 0"+Integer.toHexString(i).toUpperCase();
            }else{
                hex_string_table[i]=" "+Integer.toHexString(i).toUpperCase();
            }
        }
    }
    private int align_num=0;//对齐字节数


    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            StringBuffer sb=new StringBuffer();
            byte[] bs;
            float sWidth;
            int b,i,lineWidth=0,align_i=0;
            switch (msg.what) {
                case REC_DATA:
                    sb.setLength(0);
                    if(isHEXrec){
                        bs=(byte[])msg.obj;
                        for(i=0;i<msg.arg1;i++){
                            b=(bs[i]&0xff);
                            sb.append(hex_string_table[b]);
                            sWidth=RecDataView.getPaint().measureText(hex_string_table[b]);
                            lineWidth+=sWidth;
                            if(lineWidth>RecDataView.getWidth()||(align_num!=0&&align_num==align_i)){
                                lineWidth=(int)sWidth;align_i=0;
                                sb.insert(sb.length()-3, '\n');
                            }
                            align_i++;
                        }
                    }else {
                        bs=(byte[])msg.obj;
                        char[] c=new char[msg.arg1];
                        for(i=0;i<msg.arg1;i++){
                            c[i]=(char)(bs[i]&0xff);
                            sWidth=RecDataView.getPaint().measureText(c,i,1);
                            lineWidth+=sWidth;
                            if(lineWidth>RecDataView.getWidth()){
                                lineWidth=(int)sWidth;
                                sb.append('\n');
                            }
                            if(c[i]=='\n')lineWidth=0;
                            sb.append(c[i]);
                        }
                    }
                    RecDataView.append(sb);
                    break;
                case CONNECTED_DEVICE_NAME:
                    // 提示已连接设备名
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "已连接到"
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    MainActivity.this.setTitle("已连接");
                    break;
                case BT_TOAST:
                    if(mConnectedDeviceName!=null)
                        Toast.makeText(getApplicationContext(), "与"+mConnectedDeviceName+
                                msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getApplicationContext(), "与"+target_device_name+
                            msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    MainActivity.this.setTitle("未连接");
                    mConnectedDeviceName=null;
                    break;
                case MAIN_TOAST:
                    Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private String target_device_name=null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
                    // 获取设备
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID
                                .fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast.makeText(this, "Get socket err" + e, Toast.LENGTH_SHORT).show();
                    }
                    target_device_name=device.getName();
                    if(target_device_name.equals(mConnectedDeviceName)){
                        Toast.makeText(this, "已连接"+mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 提示正在连接设备
                    Toast.makeText(this, "正在连接"+target_device_name, Toast.LENGTH_SHORT).show();
                    // 连接设备
                    mConnectService.connect(device);
                }
                break;
                // 点击取消按钮或点击返回键
            case ENABLE_BLUETOOTH:
                // 请求打开蓝牙被用户拒绝时提示
                if (resultCode == Activity.RESULT_OK) {
                    mConnectService = new BluetoothService(mHandler);
                } else {
                    Toast.makeText(this,"拒绝打开蓝牙", Toast.LENGTH_SHORT).show();
                }
            }
        }



    //控制小车
    class ButtonListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            if (bluetoothAdapter.isEnabled()) {
                switch (v.getId()) {
                    case R.id.above:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//放开事件
                            sendString("5");

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("8");

                        }
                        break;
                    case R.id.left:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//放开事件
                            sendString("5");

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("4");
                        }
                        break;
                    case R.id.right:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//放开事件
                            sendString("5");

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("6");
                        }
                        break;
                    case R.id.below:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//放开事件
                            sendString("5");
                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("2");
                        }
                        break;
                    case R.id.middle:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("1");
                        }
                        break;
                    case R.id.free:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            sendString("7");
                        }
                        break;

                    default:
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "请连接蓝牙之后操作", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        if ((hexString == null) || (hexString.equals(""))) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; ++i) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //蓝牙发送数据
    public boolean sendString(String str) {
        if (bluetoothSocket == null) {
            Toast.makeText(this, "蓝牙连接已断开，请重新连接！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (str == null) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            OutputStream os = bluetoothSocket.getOutputStream();
            if (true) {
//                byte[] bos_hex = hexStringToBytes(str);
//                mConnectService.write(bos_hex);
//                Log.d("bluetooth","发送16进制数据:" + bos_hex);
                String[] ss=str.split(" ");
                byte[] bs;
                bs=new byte[1];
                for(String s:ss) {
                    if (s.length() != 0) {
                        bs[0] = (byte) (int) Integer.valueOf(s, 16);
                        mConnectService.write(bs);
                        Log.d("bluetooth", "16进制" + bs);
                    }
                }
            } else {
                byte[] bos = str.getBytes("GB2312");
                mConnectService.write(bos);
                Log.d("bluetooth","发送普通数据" + bos);
            }

        } catch (IOException e) {
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.e(TAG, "onDestroy");
        // Stop the Bluetooth connection
        if (mConnectService != null) mConnectService.cancelAllBtThread();
        if (timeTask != null) timeTask.interrupt();
    }

}