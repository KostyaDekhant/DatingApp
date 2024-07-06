package com.example.datingappclient.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {

    public static byte[] convertByteToPrimitiveByte(Byte[] object) {
        byte[] result = new byte[object.length];
        for (int i = 0; i < object.length; i++) {
            result[i] = object[i];
        }
        return result;
    }

    public static Bitmap convertPrimitiveByteToBitmap(byte[] object) {
        return BitmapFactory.decodeByteArray(object, 0, object.length);
    }

    public static Bitmap convertByteToBitmap(Byte[] object) {
        byte[] array = convertByteToPrimitiveByte(object);
        return convertPrimitiveByteToBitmap(array);
    }
}
