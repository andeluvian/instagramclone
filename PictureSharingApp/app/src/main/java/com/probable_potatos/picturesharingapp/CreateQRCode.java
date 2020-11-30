package com.probable_potatos.picturesharingapp;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class CreateQRCode
{
    String stringForQR;

    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH=500;
    public final static int QRcodeWidth = 500;

    public CreateQRCode( String string ) {
        stringForQR = string;
    }

    public Bitmap GenerateBitmap(){

        try {
            Bitmap bitmap = TextToImageEncode(stringForQR);

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException
    {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE, QRcodeWidth, QRcodeWidth, null);
        }
        catch (IllegalArgumentException Illegalargumentexception)
        {
            return null;
        }
        int width = bitMatrix.getWidth();

        int height = bitMatrix.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++)
        {
            int offset = y * width;

            for (int x = 0; x < width; x++)
            {
                pixels[offset + x] = bitMatrix.get(x, y) ? black:white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, width, height);
        return bitmap;
    }
}
