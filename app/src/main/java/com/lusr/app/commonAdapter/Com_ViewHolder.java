package com.lusr.app.commonAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


/**
 * 基本使用
 * 直接通过Com_ViewHolder.get(Context context, ViewGroup parent, int layoutId);
 * 得到ViewHolder对象
 */
public class Com_ViewHolder extends RecyclerView.ViewHolder {

    //不能确定View是哪一种，只能通过集合实现
    private SparseArray<View> mViews;
    //mConvertView是获得指定的item的layout对用的视图
    private View mConvertView;

    private Context mContext;

    //构造器，自身调用,自身类里面个get()方法获得了Layout对象
    public Com_ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    public static Com_ViewHolder get(Context context, ViewGroup parent, int layoutId) {
        //获取layout对象，也就是item的位置
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        Com_ViewHolder holder = new Com_ViewHolder(context, itemView, parent);
        return holder;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            //在item里面找到viewId，比如一个TextView,Button ,一个ImageView
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /*
    辅助方法
     */

    public Com_ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

//    public Com_ViewHolder setCircleImageResource(int viewId, int resId) {
//
//        CircleImageView view = (CircleImageView)getView(viewId);
//        view.setImageResource(resId);
//        return this;
//    }

    public Com_ViewHolder setImageResource(int viewId, int resId) {

        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * Glide加载图片
     *
     * @param viewId
     * @param url    通过Url使用Glide加载图片
     * @return
     */
    public Com_ViewHolder setImageResource(int viewId, String url) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        ImageView view = getView(viewId);
        Log.e("url", url + " ");
        Glide.with(mContext)
                .load(url)
               // .listener(mRequestListener)
                .apply(options)
                .into(view);
        return this;
    }
/*
    RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            Log.d("e", "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            Log.e("ok", "Ok");
            return false;
        }
    };

/*
    /**
     * 通过path加载图片
     *
     * @param viewId
     * @param
     * @return
     */

    public Com_ViewHolder setPathImage(int viewId, int resourceId) {
        ImageView view = (ImageView) getView(viewId);
        view.setImageResource(resourceId);
        return this;
    }

    public Com_ViewHolder setOnclickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public Com_ViewHolder setImageSize(int viewId, int width, int height) {
        ImageView view = getView(viewId);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = width;
        lp.height = height;
        view.setLayoutParams(lp);
        return this;
    }

//    public Com_ViewHolder setCircleImageView(int viewId, int resId) {
//
//        CircleImageView view = (CircleImageView) getView(viewId);
//        view.setImageResource(resId);
//        return this;
//    }

}