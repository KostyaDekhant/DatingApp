package com.example.datingappclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.datingappclient.model.UserImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static byte[] compressImage(byte[] imageBytes) {
        // Декодирование изображения из массива байтов
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Создание нового изображения с уменьшенным размером
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);

        // Сжатие изображения для сохранения в формате JPEG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

        // Получение сжатого массива байтов
        return outputStream.toByteArray();
    }

    public static Byte[] converPrimitiveByteToByte(byte[] bytesPrim) {
        Byte[] bytesObj = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) {
            bytesObj[i++] = b; // Автоупаковка примитивного типа byte в объект Byte
        }
        return bytesObj;
    }

    public static byte[] uriToByteArray(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
