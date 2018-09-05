/* @蓝牙智能锁柜子手机端APP
 * @name:BluetoothCabinet V2.0
 * @author:于宙
 * @功能改进:
 *          增加了登录注册的功能
 *          添加了用户说明的界面
 *          实现了记住密码的功能
 * @data:2018/4/8
 * */
package com.xiaoyuzhou.bluetoothcabinetv10;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

//这是上传到github上的版本
public class MainActivity extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1;               //打开系统蓝牙的请求码，自己设定
    private static Boolean isConnect = false;             //判断是否连接成功的标志

    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private MyHandler handler = new MyHandler();
    private SharedPreferences pref;
    private String UserPermssion;

    // 蓝牙BLE列表适配器
    private LeDeviceListAdapter mLeDeviceListAdapter_paired;
    private LeDeviceListAdapter mLeDeviceListAdapter_isConnect;

    private TextView tv_showpaired;
    private TextView tv_showconnect;
    private Button btn_openbluetooth;
    private Button btn_paireddevices;
    private Button btn_searchdevices;

    /***蓝牙发送数据的按钮***/                      //发送的数据
    private Button btn_senddataOneUp;             //
    private Button btn_senddataTwoUp;             //
    private Button btn_senddataThreeUp;
    private Button btn_senddataFourUp;
    private Button btn_senddataFiveUp;
    private Button btn_senddataOneDown;
    private Button btn_senddataTwoDown;
    private Button btn_senddataThreeDown;
    private Button btn_senddataFourDown;
    private Button btn_senddataFiveDown;
    private Button btn_senddataSixUp;
    private Button btn_senddataRightOne;
    private Button btn_senddataRightTwo;
    private Button btn_senddataRightThree;

    private ListView lv_paired;
    private ListView lv_connect;

    public final String s = "00001101-0000-1000-8000-00805F9B34FB";  //蓝牙建立链接需要的 UUID
    private BluetoothSocket mmSocket;  //获取到选中设备的客户端串口，全局变量，否则连接在方法执行完就结束了
    private BluetoothDevice mmdevice;   // 选中发送数据的蓝牙设备，全局变量，否则连接在方法执行完就结束了

    //接收连接子线程发送过来的数据，提示用户连接已经完成，可以进行操作
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String flag = (String) msg.obj;

            if (flag.equals("正在连接...")) {
                btn_openbluetooth.setText("蓝牙连接中...");
                btn_openbluetooth.setTextColor(Color.GREEN);
            } else if (flag.equals("连接成功")) {
                btn_openbluetooth.setText("蓝牙连接成功");
                btn_openbluetooth.setTextColor(Color.RED);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("data", MODE_PRIVATE);
        UserPermssion = pref.getString("permission", "没有获取到");

        btn_openbluetooth = findViewById(R.id.button1);                    //实例化打开蓝牙的按钮
        btn_paireddevices = findViewById(R.id.button2);                    //实例化刷新已配对蓝牙设备的按钮，并显示到listView
        btn_searchdevices = findViewById(R.id.button3);                    //实例化扫描蓝牙设备的按钮，并显示到listView
        btn_senddataOneUp = findViewById(R.id.button4);                    //实例化发送一上柜子数据的按钮
        btn_senddataTwoUp = findViewById(R.id.button5);                    //实例化发送二上柜子数据的按钮
        btn_senddataThreeUp = findViewById(R.id.button6);                  //实例化发送三上柜子数据的按钮
        btn_senddataFourUp = findViewById(R.id.button7);                   //实例化发送四上柜子数据的按钮
        btn_senddataFiveUp = findViewById(R.id.button8);                   //实例化发送五上柜子数据的按钮
        btn_senddataOneDown = findViewById(R.id.button10);                 //实例化发送一下柜子数据的按钮
        btn_senddataTwoDown = findViewById(R.id.button11);                 //实例化发送二下柜子数据的按钮
        btn_senddataThreeDown = findViewById(R.id.button12);               //实例化发送三下柜子数据的按钮
        btn_senddataFourDown = findViewById(R.id.button13);                //实例化发送四下柜子数据的按钮
        btn_senddataFiveDown = findViewById(R.id.button14);                  //实例化发送五下柜子数据的按钮
        btn_senddataSixUp = findViewById(R.id.button9);                      //实例化发送六上柜子数据的按钮
        btn_senddataRightOne = findViewById(R.id.button15);                  //实例化发送右一柜子数据的按钮
        btn_senddataRightTwo = findViewById(R.id.button16);                  //实例化发送右二柜子数据的按钮
        btn_senddataRightThree = findViewById(R.id.button17);                //实例化发送右三柜子数据的按钮


        //蓝牙适配器及ListView显示的初始化；
        mLeDeviceListAdapter_paired = new LeDeviceListAdapter(this);     //实例化已配对的蓝牙设备的适配器对象
        mLeDeviceListAdapter_isConnect = new LeDeviceListAdapter(this);  //实例化扫描到的所有蓝牙设备的适配器对象
        lv_paired = findViewById(R.id.lv_show1);                            //实例化已配对的蓝牙设备的listView对象
        lv_paired.setAdapter(mLeDeviceListAdapter_paired);                  //加载已配对的蓝牙设备listView自定义的适配器
        lv_connect = findViewById(R.id.lv_show2);                           //实例化扫描到的所有蓝牙设备的listView对象
        lv_connect.setAdapter(mLeDeviceListAdapter_isConnect);              //加载扫描到的所有蓝牙设备listView自定义的适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();           //实例化蓝牙操作的适配器对象

        /**********************第一个按钮点击事件打开本机蓝牙**********************/
        btn_openbluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认设备是否支持蓝牙
                if (mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
                } else if (mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
                }
                //判断蓝牙是否开启，如果没有开启，调用系统方法发出请求
                else if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    Toast.makeText(MainActivity.this, "正在打开蓝牙...", Toast.LENGTH_LONG).show();

                }
            }
        });

        //第二个按钮点击事件刷新已经绑定的蓝牙设备
        btn_paireddevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /********************获取所有已经绑定的蓝牙设备****************/
                //先清空一次列表
                mLeDeviceListAdapter_paired.clear();
                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                if (devices.size() > 0) {
                    for (BluetoothDevice bluetoothDevice : devices) {
                        // 扫描到设备则添加到适配器
                        mLeDeviceListAdapter_paired.addDevice(bluetoothDevice);
                        // 数据改变并更新已配对蓝牙设备的列表
                        mLeDeviceListAdapter_paired.notifyDataSetChanged();
                    }
                }
            }
        });

        /*******第三个按钮点击事件开始扫描附近的蓝牙设备，在广播接收器里实现获取蓝牙设备数据***********/
        btn_searchdevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果正在搜索，就先取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
                mBluetoothAdapter.startDiscovery();
            }
        });

        /************上排第一个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataOneUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("001\r\n".getBytes());
                btn_senddataOneUp.setText("打开");
                btn_senddataOneUp.setTextColor(Color.BLUE);
            }
        });

        /************上排第二个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataTwoUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("002\r\n".getBytes());
                btn_senddataTwoUp.setText("打开");
                btn_senddataTwoUp.setTextColor(Color.BLUE);
            }
        });

        /************上排第三个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataThreeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("003\r\n".getBytes());
                btn_senddataThreeUp.setText("打开");
                btn_senddataThreeUp.setTextColor(Color.BLUE);
            }
        });

        /************上排第四个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataFourUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("004\r\n".getBytes());
                btn_senddataFourUp.setText("打开");
                btn_senddataFourUp.setTextColor(Color.BLUE);
            }
        });

        /************上排第五个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataFiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("005\r\n".getBytes());
                btn_senddataFiveUp.setText("打开");
                btn_senddataFiveUp.setTextColor(Color.BLUE);
            }
        });

        /************下排第一个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataOneDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("006\r\n".getBytes());
                btn_senddataOneDown.setText("打开");
                btn_senddataOneDown.setTextColor(Color.BLUE);
            }
        });

        /************下排第二个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataTwoDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("007\r\n".getBytes());
                btn_senddataTwoDown.setText("打开");
                btn_senddataTwoDown.setTextColor(Color.BLUE);
            }
        });

        /************下排第三个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataThreeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("008\r\n".getBytes());
                btn_senddataThreeDown.setText("打开");
                btn_senddataThreeDown.setTextColor(Color.BLUE);
            }
        });

        /************下排第四个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataFourDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("009\r\n".getBytes());
                btn_senddataFourDown.setText("打开");
                btn_senddataFourDown.setTextColor(Color.BLUE);
            }
        });

        /************下排第五个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataFiveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("010\r\n".getBytes());
                btn_senddataFiveDown.setText("打开");
                btn_senddataFiveDown.setTextColor(Color.BLUE);
            }
        });

        /************上排第六个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataSixUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("011\r\n".getBytes());
                btn_senddataSixUp.setText("打开");
                btn_senddataSixUp.setTextColor(Color.BLUE);
            }
        });

        /************右排第一个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataRightOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("012\r\n".getBytes());
                btn_senddataRightOne.setText("打开");
                btn_senddataRightOne.setTextColor(Color.BLUE);
            }
        });

        /************右排第二个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataRightTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("013\r\n".getBytes());
                btn_senddataRightTwo.setText("打开");
                btn_senddataRightTwo.setTextColor(Color.BLUE);
            }
        });

        /************右排第三个按钮点击事件开始向蓝牙模块发送数据****************/
        btn_senddataRightThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("014\r\n".getBytes());
                btn_senddataRightThree.setText("打开");
                btn_senddataRightThree.setTextColor(Color.BLUE);
            }
        });


        /*****************注册用以接收到已搜索到的蓝牙设备的广播接收器*****************/
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        /***************************已配对的蓝牙设备的listView的点击事件**************************/
        lv_paired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = mLeDeviceListAdapter_paired.getDevice(position);
                connectThread = new ConnectThread(device);
                Toast.makeText(MainActivity.this, "连接中...", Toast.LENGTH_SHORT).show();
                connectThread.start();
                //Toast.makeText(MainActivity.this, "点击的设备是" + device.getName() + "   " + device.getAddress(), Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, "蓝牙设备连接中...", Toast.LENGTH_SHORT).show();
            }
        });

        /**************************搜索到的所有的蓝牙设备的listView的点击事件***************************/
        lv_connect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 如果正在搜索，就先取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                try {
                    BluetoothDevice device = mLeDeviceListAdapter_isConnect.getDevice(position);
                    Boolean returnValue = false;
                    Method createBondMethod;
                    //这里判断如果蓝牙设备没有配对，就利用反射的方式进行配对
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        // 反射方法调用；
                        createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        Toast.makeText(MainActivity.this, "开始配对", Toast.LENGTH_LONG).show();
                        try {
                            returnValue = (Boolean) createBondMethod.invoke(device);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        // 数据改变并更新扫描到的蓝牙设备的列表
                        mLeDeviceListAdapter_isConnect.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "点击设备是" + device.getName() + "   " + device.getAddress(), Toast.LENGTH_LONG).show();
                    }
                    //这里判断蓝牙设备已经配对，就开启连接的子线程进行连接
                    else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(MainActivity.this, "正在连接...", Toast.LENGTH_LONG).show();
                        connectThread = new ConnectThread(device);
                        connectThread.start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //当Activity可见的时候调用的方法
    //在按下返回键或者home键重新进入的时候判断是否连接
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume ()" + isConnect);
        if (isConnect == true) {
            btn_openbluetooth.setText("蓝牙连接成功");
            btn_openbluetooth.setTextColor(Color.RED);

        }
    }

    /******************广播接收器获取蓝牙设备信息******************/
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 搜索到的不是已经绑定的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 扫描到设备则添加到扫描所有设备的ListView适配器
                    mLeDeviceListAdapter_isConnect.addDevice(device);
                    // 数据改变并更新扫描所有蓝牙设备的ListView列表
                    mLeDeviceListAdapter_isConnect.notifyDataSetChanged();
                }
                // 搜索完成
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(MainActivity.this, "搜索完成", Toast.LENGTH_LONG).show();
            }
        }
    };

    //注销广播接收器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /***********************连接蓝牙设备的子线程内部类***************/
    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            //赋值给设备
            mmdevice = device;
            try {
                //根据UUID创建并返回一个BluetoothSocket
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(s));
            } catch (IOException e) {
            }
            //赋值给BluetoothSocket
            mmSocket = tmp;
        }

        public void run() {
            try {

                //开始利用Handler机制向主线程发送消息，提示用户连接完成
                Message msg1 = handler.obtainMessage();
                msg1.obj = "正在连接...";
                handler.sendMessage(msg1);

                mmSocket.connect();
                isConnect = true;
                if (isConnect == true) {
                    Message msg2 = handler.obtainMessage();
                    msg2.obj = "连接成功";
                    handler.sendMessage(msg2);
                }

            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return;
                }
            }
            //在线程中管理连接；
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();

        }

        public void cancel() {

            try {
                mmSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /*******************************蓝牙发送数据的子线程内部类***************************/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
        }

        /**********************从MainActivity调用此函数将数据发送到远程设备***************/
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /**********************从MainActivity调用此关闭连接*****************************/
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
