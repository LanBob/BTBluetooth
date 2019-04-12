package com.lusr.app;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lusr.app.commonAdapter.Com_Adapter;
import com.lusr.app.commonAdapter.Com_ViewHolder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends Activity implements View.OnClickListener {
//    public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final Context context = this;

    Button closeLink;
    Button openBlue;
    Button search;
    LinearLayout showList;
    LinearLayout showData;
    LinearLayout basicData;
    RecyclerView blueList;

    private TextView basicTime;
    private TextView basicV;
    private TextView basicjingduAndWeidu;


    RecyclerView.Adapter adapter;
    //    TextView textData;
    List<BlueBean> blueBeanList;

    RecyclerView dataList;
    RecyclerView.Adapter dataAdapter;

    List<DataBean> dataBeanList;

    SearchServer searchServer;

    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice cchDev;
    private String devName1 = "CCHX001";
    private String devName2 = "CCHX002";
    private String devName3 = "CCHX003";
    private String devName4 = "CCHX004";
    private String devName5 = "CCHX005";
    private boolean canUseable;
    private String fileName = "";
    private String devName = "";
    int[][] a = new int[11][11];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        

        canUseable = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        blueBeanList = new ArrayList<>();
        dataBeanList = new ArrayList<>();

        initView();

        handler = new Handler() {
            private Bundle mData;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                mData = msg.getData();
                byte[] data = mData.getByteArray("data");
                String str = "";

//              数据处理
                try {
//                    这个是源数据，没有进行过处理
                    str = new String(data, "utf-8");

                    DataBean dataBean = FileStringHelper.getSubShow(str);
                    basicV.setText("电压： " + dataBean.getV() + "      " + "设备名称：" + devName);

                    int len = dataBeanList.size();

                    if(len == 0){
                        dataBeanList.add(dataBean);
                    }else {
                        dataBeanList.add(dataBean);
                        for(int i = len-1; i>=0;--i){
                            dataBeanList.set(i+1,dataBeanList.get(i));
                        }
                        dataBeanList.set(0,dataBean);
                    }

                    int newLen = dataBeanList.size();
//                    显示数据条数设置：15
                    for(int i = newLen-1;i>15;--i){
                        dataBeanList.remove(i);
                    }

                    dataAdapter.notifyDataSetChanged();


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        };

        //        注册
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void initView() {
        openBlue = (Button) findViewById(R.id.openBlue);
        search = (Button) findViewById(R.id.search);
        closeLink = (Button) findViewById(R.id.closeLink);

        showList = (LinearLayout) findViewById(R.id.showList);
        showData = (LinearLayout) findViewById(R.id.showData);
        basicData = (LinearLayout) findViewById(R.id.basicData);

        basicTime = findViewById(R.id.basicTime);
        basicV = findViewById(R.id.basicV);
        basicjingduAndWeidu = findViewById(R.id.basicjingduAndWeidu);

        openBlue.setOnClickListener(this);
        search.setOnClickListener(this);
        closeLink.setOnClickListener(this);

        blueList = (RecyclerView) findViewById(R.id.blueList);
        dataList = findViewById(R.id.dataList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        blueList.setLayoutManager(linearLayoutManager);
        dataList.setLayoutManager(linearLayoutManager1);

        adapter = new Com_Adapter<BlueBean>(MainActivity.this, R.layout.item, blueBeanList) {

            @Override
            public void convert(final Com_ViewHolder holder, final BlueBean blueBean) {
                if (blueBean != null) {
                    holder.setText(R.id.blueName, blueBean.getName() + "");
                    holder.setText(R.id.macid, blueBean.getMacId() + "");

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String name = blueBean.getName();
//                        保证是BT设备
                            if (devName1.equals(name) || devName2.equals(name)
                                    || devName3.equals(name) ||
                                    devName4.equals(name) || devName5.equals(name)) {

                                devName = name;

//                        保证已经进行搜索
                                if (canUseable) {
                                    if (searchServer != null) {
                                        searchServer.cancle();
                                    }

                                    showList.setVisibility(View.GONE);
                                    blueList.setVisibility(View.GONE);
                                    showData.setVisibility(View.VISIBLE);
                                    basicData.setVisibility(View.VISIBLE);
                                    dataList.setVisibility(View.VISIBLE);

                                    searchServer = new SearchServer(MainActivity.this, bluetoothAdapter, blueBean.getBluetoothDevice(), handler, fileName);
                                    searchServer.start();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "此设备非BT无线设备", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
        blueList.setAdapter(adapter);
        blueList.setNestedScrollingEnabled(false);

        dataAdapter = new Com_Adapter<DataBean>(MainActivity.this, R.layout.dataitem, dataBeanList) {

            @Override
            public void convert(Com_ViewHolder holder, DataBean dataBean) {
                if (dataBean != null) {
                    if (dataBean.getTemperature() != null) {
                        holder.setText(R.id.time, dataBean.getTime()+"    ");
                        holder.setText(R.id.temperature, dataBean.getTemperature()+"      ");
                        holder.setText(R.id.humidity, dataBean.getHumidity()+"     ");
                        holder.setText(R.id.airPressure, dataBean.getAirPressure()+"     ");
                        holder.setText(R.id.windDirection, dataBean.getWindDirection()+"     ");
                        holder.setText(R.id.windSpeed, dataBean.getWindSpeed()+"     ");
                        holder.setText(R.id.windDirection2, dataBean.getWindDirection2()+"     ");
                        holder.setText(R.id.windSpeed2, dataBean.getWindSpeed2());
                    }
                }
            }
        };
        dataList.setAdapter(dataAdapter);
        dataList.setNestedScrollingEnabled(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//              打开蓝牙

            case R.id.openBlue:
                //                                            打开蓝牙
                enable_bluetooth();
                break;

//                搜索
            case R.id.search:
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                } else {
                    //                ======================================

                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    alertDialogBuilder.setView(promptsView);

                    final EditText inputTime = (EditText) promptsView.findViewById(R.id.inputTime);
                    final EditText inputJingdu = (EditText) promptsView.findViewById(R.id.inputJingdu);
                    final EditText inputWeidu = (EditText) promptsView.findViewById(R.id.inputWeidu);
                    final EditText inputTestPerson = (EditText) promptsView.findViewById(R.id.inputTestPerson);
                    final EditText inputZhuchi = (EditText) promptsView.findViewById(R.id.inputZhuchi);

                    inputTime.setText(FileStringHelper.getTime());
                    LocationUtil locationUtil = new LocationUtil(MainActivity.this);
                    String jindu = locationUtil.getJingDu();
                    String weidu = locationUtil.getWeidu();
                    inputJingdu.setText(jindu);
                    inputWeidu.setText(weidu);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setTitle("参数设置")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            String time = inputTime.getText().toString();
                                            String jingdu = inputJingdu.getText().toString();
                                            String weidu = inputWeidu.getText().toString();
                                            String textMan = inputTestPerson.getText().toString();
                                            String zhuchi = inputZhuchi.getText().toString();

                                            if (FileStringHelper.isEmpry(time)
                                                    || FileStringHelper.isEmpry(jingdu)
                                                    || FileStringHelper.isEmpry(weidu)
                                                    || FileStringHelper.isEmpry(textMan)
                                                    || FileStringHelper.isEmpry(zhuchi)) {

                                                Toast.makeText(MainActivity.this, "请先填写完整信息", Toast.LENGTH_SHORT).show();

                                            } else {
                                                dataBeanList.clear();
                                                blueBeanList.clear();

                                                showList.setVisibility(View.VISIBLE);
                                                blueList.setVisibility(View.VISIBLE);
                                                showData.setVisibility(View.GONE);
                                                basicData.setVisibility(View.VISIBLE);
                                                dataList.setVisibility(View.GONE);

                                                basicTime.setText("日期：" + FileStringHelper.getDay());
                                                basicV.setText("电压： 0V");
                                                basicjingduAndWeidu.setText("经度:" + jingdu + "      纬度：" + weidu);

                                                fileName = zhuchi + "_" + FileStringHelper.getDay() + "_" + textMan + ".txt";

                                                if (FileStringHelper.fileIsExists(fileName)) {

                                                } else {
                                                    try {
//                                                        如果还没有这个文件，就将空串写进文件，相当于去创建这个文件的表头标签
                                                        FileStringHelper.save(fileName, "时间                气温   湿度   气压   风向   风速   风向2   风速2   风向10   风速10   经度   纬度\n".getBytes());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                canUseable = true;
                                                search_bluetooth();
                                            }
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
//                ======================================
                }
                break;

//            case R.id.link:
//                if (!bluetoothAdapter.isEnabled()) {
//                    Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
//                } else {
//                    search_bluetooth();
//
//                    if (cchDev != null) {
//                        searchServer = new SearchServer(MainActivity.this, bluetoothAdapter, cchDev, handler);
//                        searchServer.start();
//                    } else {
//                        Toast.makeText(MainActivity.this, "未搜索到" + myName, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;

//关闭蓝牙
            case R.id.closeLink:
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                }
                if (searchServer != null)
                    searchServer.cancle();

                canUseable = false;

                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                } else {
                    if (FileStringHelper.fileIsExists(fileName)) {
                        Toast.makeText(MainActivity.this, "已经保存数据", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "未读取到数据", Toast.LENGTH_SHORT).show();
                    }
                    setTitle("已关闭");
                }
                break;

        }
    }

    /**
     * 打开蓝牙设备,若已经打开，将不操作
     */
    public void enable_bluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(MainActivity.this, "您已经进行了蓝牙连接", Toast.LENGTH_SHORT).show();
        } else {
            // 判断是否支持蓝牙设备
            if (bluetoothAdapter != null) {
                // 蓝牙设备是否有效
                if (!bluetoothAdapter.isEnabled()) {
                    // 打开蓝牙，是异步的，所以不会马上显示
                    bluetoothAdapter.enable();
                }
            }
        }
    }


    /**
     * 搜索蓝牙设备
     */
    public void search_bluetooth() {

        if (blueBeanList.size() != 0) {
            blueBeanList.clear();
        }

        // 判断是否支持蓝牙设备
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled() && !bluetoothAdapter.isDiscovering()) {
            setTitle("正在搜索蓝牙设备...");

            try {
                Thread.sleep(2800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice dev :
                    bluetoothDeviceSet) {
                if (devName1.equals(dev.getName()) || devName2.equals(dev.getName())
                        || devName3.equals(dev.getName()) ||
                        devName4.equals(dev.getName()) || devName5.equals(dev.getName())) {

                    BlueBean blueBean = new BlueBean();
                    blueBean.setName("" + dev.getName());
                    blueBean.setMacId("" + dev.getAddress());
                    blueBean.setBluetoothDevice(dev);
                    blueBeanList.add(blueBean);
                    Toast.makeText(MainActivity.this, "提示：搜索到可用设备" + blueBean.getName(), Toast.LENGTH_SHORT).show();

                } else {
                    BlueBean blueBean = new BlueBean();
                    blueBean.setName("" + dev.getName());
                    blueBean.setMacId("" + dev.getAddress());
                    blueBean.setBluetoothDevice(dev);
                    blueBeanList.add(blueBean);
                }
            }
            // 发现当前范围的蓝牙设备，异步方法
            bluetoothAdapter.startDiscovery();
        } else {
            canUseable = false;
            bluetoothAdapter.cancelDiscovery();
            setTitle("蓝牙设备列表");
        }

    }


    //    接收器
    private BluetoothDevice device;

    //    /**
//     * 广播接收器接收所有索索过程中的消息
//     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // 当某一个蓝牙设备被找到，则会收到此消息
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                // 获取搜索到的蓝牙设备
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 搜索的设备在之前没有配对过，是一个新的设备
                BlueBean blueBean = new BlueBean();
                blueBean.setName("" + device.getName());
                blueBean.setMacId("" + device.getAddress());
                blueBean.setBluetoothDevice(device);
                blueBeanList.add(blueBean);
                adapter.notifyDataSetChanged();

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                adapter.notifyDataSetChanged();
//                搜索完成
                Toast.makeText(MainActivity.this, "已经搜索完毕", Toast.LENGTH_SHORT).show();
                setTitle("蓝牙设备列表");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //                    停止搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
        unregisterReceiver(mReceiver);
    }


}
