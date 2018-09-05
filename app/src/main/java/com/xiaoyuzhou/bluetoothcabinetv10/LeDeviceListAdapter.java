package com.xiaoyuzhou.bluetoothcabinetv10;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/4/4.
 */

public class LeDeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mLeDevices;
    //LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
    //它的作用类似于findViewById()
    private LayoutInflater mInflator;
    private Activity mContext;//获得 LayoutInflater 实例的一种方法就是使用Activity；

    public LeDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<BluetoothDevice>();
        mInflator = mContext.getLayoutInflater();
    }


    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
            System.out.println(device.getName() + "  " + device.getAddress());
        }
    }

    // 获取子项中对应的设备
    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    // 清空列表的数据
    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mLeDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.item, null);//实例化这个控件
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.Address);
            viewHolder.deviceName = view.findViewById(R.id.Name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            //the Object stored in this view as a tag
        }

        // 对应的设备进行处理
        BluetoothDevice device = mLeDevices.get(position);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText("未知设备");
        }
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }

    //将要显示的信息封装成一个类
    final class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }





}
