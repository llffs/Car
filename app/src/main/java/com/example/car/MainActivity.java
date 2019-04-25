package com.example.car;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

    private ToggleButton mToggleButton;             //连接小车
    private TextView tvSound;                       //显示连接信息
    private Button middle;                          //喇叭
    private Button left;                            //左转
    private Button above;                           //前进
    private Button right;                           //右转
    private Button below;                           //后退
    private Button stop;                            //停止
    public byte[] message = new byte[1];
    private Vibrator vibrator;
    private int ENABLE_BLUETOOTH = 2;               //作为requestCode返回参数
    private BluetoothAdapter bluetoothAdapter;
    BluetoothCarService carService;
    BluetoothDevice bluetoothDevice;                //蓝牙设备
    BluetoothSocket bluetoothSocket = null;  ;       //蓝牙接口和其他设备交换数据
    OutputStream outputStream = null;
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String blueAddress = "98:D3:31:70:9B:CA";//蓝牙模块的MAC地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "3238c409b127a913b011c099243c8769");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mToggleButton = (ToggleButton) findViewById(R.id.tglSound);
        tvSound = (TextView) findViewById(R.id.tvSound);
        middle = (Button) findViewById(R.id.middle);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        above = (Button) findViewById(R.id.above);
        below = (Button) findViewById(R.id.below);
        stop = (Button) findViewById(R.id.stop);

        ButtonListener bt = new ButtonListener();
        left.setOnTouchListener(bt);
        right.setOnTouchListener(bt);
        above.setOnTouchListener(bt);
        below.setOnTouchListener(bt);
        stop.setOnTouchListener(bt);
        middle.setOnTouchListener(bt);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        assert toolbar != null;
        toolbar.setTitle("我的智能小车");
        toolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(toolbar);

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

        //连接小车
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bluetoothAdapter != null) {                     //是否支持蓝牙
                        Log.d("true", "已连接");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, ENABLE_BLUETOOTH);
                        run();
                        tvSound.setText("已连接");
                    } else {
                        tvSound.setText("不支持蓝牙");
                    }
                } else {
                    bluetoothAdapter.disable();
                    Toast.makeText(MainActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                    tvSound.setText("已断开连接");
                }
            }
        });


        //喇叭按钮
//        middle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        if (requestCode == ENABLE_BLUETOOTH) {
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: {
                    // TODO 用户选择开启 Bluetooth，Bluetooth 会被开启

                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED: {
                    // TODO 用户拒绝打开 Bluetooth, Bluetooth 不会被开启
                    bluetoothAdapter.disable();
                    Toast.makeText(MainActivity.this, "用户拒绝开启蓝牙", Toast.LENGTH_SHORT).show();
                    mToggleButton.setChecked(false);
                    tvSound.setText("已断开连接");
                }
                break;
                default:

                    break;
            }
        }
    }


    //控制小车
    class ButtonListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            if (bluetoothAdapter.isEnabled()) {
                switch (v.getId()) {
                    case R.id.above:
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下事件
                            message[0] = (byte) 0x41;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        break;
                    case R.id.left:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//按下事件
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//放开事件
                            message[0] = (byte) 0x44;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        break;
                    case R.id.right:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//按下事件
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//放开事件
                            message[0] = (byte) 0x43;//设置要发送的数值
                            bluesend(message);//发送数值
                        }
                        break;
                    case R.id.below:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//按下事件
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值

                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//放开事件
                            message[0] = (byte) 0x42;//设置要发送的数值
                            bluesend(message);//发送数值
                        }
                        break;
                    case R.id.stop:
                        if (event.getAction() == MotionEvent.ACTION_UP) {//按下事件
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值
                        }
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//放开事件
                            message[0] = (byte) 0x40;//设置要发送的数值
                            bluesend(message);//发送数值
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


    //蓝牙发送数据
    public void bluesend(byte[] message) {
        try {
            outputStream = bluetoothSocket.getOutputStream();
            Log.d("send", Arrays.toString(message));
            outputStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter.isEnabled()) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
    }

    protected void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(blueAddress);
                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                    Log.d("true", "开始连接");
                    bluetoothSocket.connect();
                    Log.d("true", "完成连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}