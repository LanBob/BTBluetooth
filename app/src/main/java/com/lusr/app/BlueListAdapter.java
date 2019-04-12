package com.lusr.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BlueListAdapter extends ArrayAdapter<BlueBean> {

    // 子项布局的id
    private int resourceId;

    public BlueListAdapter(Context context, int resource, List<BlueBean> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        Log.e("size","data" + objects.size());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BlueBean blueBean = getItem(position);
        View view;
        ViewHolder viewHolder = null;
        // inflate出子项布局，实例化其中的图片控件和文本控件
        view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.blueName = view.findViewById(R.id.blueName);
        viewHolder.macid = view.findViewById(R.id.macid);
        Log.e("data", position + "");
        // 缓存图片控件和文本控件的实例
        view.setTag(viewHolder);

        Log.e("data", blueBean.getName());
        viewHolder.blueName.setText(blueBean.getName());
        viewHolder.macid.setText(blueBean.getMacId());
        viewHolder.bluetoothDevice = blueBean.getBluetoothDevice();
        return view;
    }

    private class ViewHolder {
        TextView blueName;
        TextView macid;
        BluetoothDevice bluetoothDevice;
    }



//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mExpandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, mExpandSpec);
//    }

}
