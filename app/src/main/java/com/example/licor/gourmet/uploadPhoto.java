package com.example.licor.gourmet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;

public class uploadPhoto extends AppCompatActivity {

    ImageView photoView;
    Uri photoUri;
    String selectPhotoPath;
    ProgressBar uploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        photoView = (ImageView)findViewById(R.id.photoView);
        uploadProgressBar = (ProgressBar)findViewById(R.id.uploadprogressBar);

        uploadProgressBar.setVisibility(View.INVISIBLE);

        Intent photoIntent = getIntent();
        photoUri = photoIntent.getData();
        selectPhotoPath = photoIntent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Bitmap photo = null;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri), null, o);

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            while(true) {
                if(width_tmp / 2 < 500 || height_tmp / 2 < 500)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            photo =  BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri), null, o2);

//            ExifInterface exif = new ExifInterface(photoUri.getPath());
//            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int rotationInDegrees = exifToDegrees(rotation);
//
//            Matrix matrix = new Matrix();
//            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
//            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

            photo = modifyOrientation(photo, photoUri.getPath());
            photo = cropToSquare(photo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        photoView.setImageBitmap(photo);
        photoView.invalidate();

    }
//    private static int exifToDegrees(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
//        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
//        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
//        return 0;
//    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    //

    public void searchButtonPress(View view) {
        uploadProgressBar.setVisibility(View.VISIBLE);
        if (selectPhotoPath == null) {
            selectPhotoPath = photoUri.getPath();
        }
        new getPredictResult(getApplicationContext()).execute(selectPhotoPath);
    }
}
