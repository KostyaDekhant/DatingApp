package com.example.datingappclient.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.datingappclient.model.UserImage;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    public static List<UserImage> objectListToUserImageList(List<Object[]> objects) {
        List<UserImage> userImages = new ArrayList<>();
        for (Object[] it : objects) {
            String imageStr = it[2].toString();
            byte[] array = Base64.getDecoder().decode(imageStr);
            Bitmap image = ImageUtils.convertPrimitiveByteToBitmap(array);

            Integer imageNum, imageID;
            Double tempImageNum, tempImageID;
            tempImageID = (Double) it[0];
            tempImageNum = (Double) it[1];
            imageID = tempImageID.intValue();
            imageNum = tempImageNum.intValue();

            userImages.add(new UserImage(imageNum, imageID, image));
        }
        return userImages;
    }
}
