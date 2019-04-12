package com.lusr.app;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class FileStringHelper {


    private FileStringHelper() {
        super();
    }

//
//    public FileHelper() {
//
////        this.mContext = mContext;
//    }

    /*
     * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
     * */
    public static void save(String filename, byte[] bytes) throws Exception {

        File f = new File(getFilePath() + "/" + filename);
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
//        FileOutputStream output = mContext.openFileOutput(filename, Context.MODE_APPEND);
        FileOutputStream output = new FileOutputStream(f, true);

        output.write(bytes);  //将String字符串以字节流的形式写入到输出流中
        output.write("\r\n".getBytes());//写入换行
        output.close();         //关闭输出流
    }


    /*
     * 这里定义的是文件读取的方法
     * */
    public static String read(String filename) throws IOException {

        //打开文件输入流
//        FileInputStream input = mContext.openFileInput(filename);
        FileInputStream input = new FileInputStream(getFilePath() + "/" + filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(getFilePath() + "/" + strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static String getFilePath() {
        return Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    //判断外部存储是否可以读写
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getTime() {
        Long mill = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date(mill));
    }

    public static String getDay() {
        Long mill = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(mill));
    }

    public static String getSeconde() {
        Long mill = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(mill));
    }

    public static boolean isEmpry(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }


//    输出的数据从+开始包括+用逗号隔开的依次是1温度2湿度3气压4瞬时风向5瞬时风速6平均风向7平均风速8电池电压9板温

    /**
     * 获取保存的信息
     * 时间   气温   湿度   气压   风向  风速   风向2   风速2   风向10   风速10   经度   纬度(共12)
     * 不要电池电压、板温，（在SearchServer）加上时间、经度，纬度
     * 返回十个：时间   气温   湿度   气压   风向  风速   风向2   风速2   风向10   风速10
     * @param soap
     * @return
     */
    public static String getSubUtil(String soap) {
        String rgex = "MDS(.*?),END";
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        String subStr = "";
        if (m.find()) {
            subStr = m.group(1);
        }
        String[] strings = subStr.split(",");

        String result = getTime() + " ";
        for (int i = 0; i < strings.length; ++i) {
            if (i <= 8) {
                if(i == 1)
                    result += strings[i] + "     ";
                else if(i == 4){
                    result += strings[i] + "    ";
                }else if(i == 5){
                    result += strings[i] + "      ";
                }else if(i == 6){
                    result += strings[i] + "    ";
                }else if(i == 7){
                    result += strings[i] + "     ";
                }else {
                    result += strings[i] + "    ";
                }
            }
        }
        result += "\n";
        return result;
    }


    /**
     * 获取显示的信息
     * DataBean
     * 时间   气温   湿度   气压   风向  风速   风向2   风速2
     *
     * @param soap
     * @return
     */
    public static DataBean getSubShow(String soap) {
        String rgex = "MDS(.*?),END";
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        String subStr = "";
        if (m.find()) {
            subStr = m.group(1);
        }
        String[] strings = subStr.split(",");

        DataBean dataBean = new DataBean();
        dataBean.setTime(getSeconde() + " ");
//    输出的数据从+开始包括+用逗号隔开的依次是1温度2湿度3气压4瞬时风向5瞬时风速6平均风向7平均风速8电池电压9板温
//        String result = getSeconde() + " ";
        for (int i = 0; i < strings.length; ++i) {
            if (i == 0)
                dataBean.setTemperature(strings[i]);
            if (i == 1)
                dataBean.setHumidity(strings[i]);
            if (i == 2)
                dataBean.setAirPressure(strings[i]);
            if (i == 3)
                dataBean.setWindDirection(strings[i]);
            if (i == 4)
                dataBean.setWindSpeed(strings[i]);
            if (i == 5)
                dataBean.setWindDirection2(strings[i]);
            if (i == 6)
                dataBean.setWindSpeed2(strings[i]);
            if (i == 9)
                dataBean.setV(strings[i]);
        }
        return dataBean;
    }

}
