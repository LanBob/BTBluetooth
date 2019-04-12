package com.lusr.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DataListAdapter extends ArrayAdapter<DataBean> {

    // 子项布局的id
    private int resourceId;

    public DataListAdapter(Context context, int resource, List<DataBean> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        Log.e("size","data" + objects.size());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataBean dataBean = getItem(position);
        View view;
        DataListAdapter.ViewHolder viewHolder = null;
        // inflate出子项布局，实例化其中的图片控件和文本控件
        view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        viewHolder = new DataListAdapter.ViewHolder();
        viewHolder.time = view.findViewById(R.id.time);
        viewHolder.temperature = view.findViewById(R.id.temperature);
        viewHolder.humidity = view.findViewById(R.id.humidity);
        viewHolder.airPressure = view.findViewById(R.id.airPressure);
        viewHolder.windDirection = view.findViewById(R.id.windDirection);
        viewHolder.windSpeed = view.findViewById(R.id.windSpeed);
        viewHolder.windDirection2 = view.findViewById(R.id.windDirection2);
        viewHolder.windSpeed2 = view.findViewById(R.id.windSpeed2);
        Log.e("data", position + "");
        // 缓存图片控件和文本控件的实例
        view.setTag(viewHolder);

        viewHolder.time.setText(dataBean.getTime()+"    ");
        viewHolder.temperature.setText(dataBean.getTemperature()+"      ");
        viewHolder.humidity.setText(dataBean.getHumidity()+"     ");
        viewHolder.airPressure.setText(dataBean.getAirPressure()+"     ");
        viewHolder.windDirection.setText(dataBean.getWindDirection()+"     ");
        viewHolder.windSpeed.setText(dataBean.getWindSpeed()+"     ");
        viewHolder.windDirection2.setText(dataBean.getWindDirection2()+"     ");
        viewHolder.windSpeed2.setText(dataBean.getWindSpeed2());
//        holder.setText(R.id.time, dataBean.getTime()+"    ");
//        holder.setText(R.id.temperature, dataBean.getTemperature()+"      ");
//        holder.setText(R.id.humidity, dataBean.getHumidity()+"     ");
//        holder.setText(R.id.airPressure, dataBean.getAirPressure()+"     ");
//        holder.setText(R.id.windDirection, dataBean.getWindDirection()+"     ");
//        holder.setText(R.id.windSpeed, dataBean.getWindSpeed()+"     ");
//        holder.setText(R.id.windDirection2, dataBean.getWindDirection2()+"     ");
//        holder.setText(R.id.windSpeed2, dataBean.getWindSpeed2());
        return view;
    }

    private class ViewHolder {
        TextView time;
        TextView temperature;
        TextView humidity;
        TextView airPressure;
        TextView windDirection;
        TextView windSpeed;
        TextView windDirection2;
        TextView windSpeed2;
    }


}
