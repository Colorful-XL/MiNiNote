package com.example.mininote;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {//图片工具类,压缩图片
    public static Bitmap getScaledBitmap(String path , Activity activity){
        //由于Fragment刚启动时不知道PhotoView到底有多大
        //oncreate(),onstart(),onResume()方法都执行完后才会有实例化布局出现
        //此时显示在屏幕上的视图才会有尺寸大小
        //先确认屏幕尺寸,按屏幕比例缩放图像,保证加载的图片不会过大
        Point size = new Point();
        activity .getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path,size.x,size.y);
    }

    public static Bitmap getScaledBitmap(String path , int desWidth , int desHeight){
        //先读入原图片信息
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//禁止解析方法在构造Bitmap对象时为其分配内存
        BitmapFactory.decodeFile(path,options);//解析图片

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //计算缩放比例
        int inSampleSize = 1;
        if (srcHeight > desHeight || srcWidth > desWidth){
            float heightScale = srcHeight /desHeight;
            float widthScale  = srcWidth / desWidth;

            inSampleSize = Math.round(heightScale < widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path,options);
    }


}
