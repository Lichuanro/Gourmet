package com.example.licor.gourmet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

/**
 * Created by Licor on 2017/5/30.
 */

public class getPredictResult extends AsyncTask<String, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>> {

    public final static String EXTRA_RESULT_MESSAGE = "com.example.licor.gourmet.GET_PREDICT_RESULT";

    Context context;
    public getPredictResult(Context context) {
        this.context = context.getApplicationContext();
    }

    ClarifaiClient client =  new ClarifaiBuilder("Bnm3TxPIfsDYbWTfOlblj42uV7weQF3SWSumGUwQ", "LPo6bFVgbZrWkAf2c0Lv9Sb0G3fotrrQBlm0K3q5")
            .client(new OkHttpClient())
            .buildSync();

    @Override
    protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(String... photoPath) {
        File imageFile = new File(photoPath[0]);
        Log.v("old image file size", String.valueOf(imageFile.length()));
        imageFile = saveBitmapToFile(imageFile);
        Log.v("new image file size", String.valueOf(imageFile.length()));
        ClarifaiResponse<List<ClarifaiOutput<Concept>>> predictionResults =
                client.getDefaultModels().foodModel().predict()
                        .withInputs(
                                ClarifaiInput.forImage(ClarifaiImage.of(imageFile))
                        )
                        .executeSync();

        Log.v("prediction", predictionResults.rawBody());
        return predictionResults;
    }

    @Override
    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
        if (response.isSuccessful()) {
            List<Concept> conceptList = response.get().get(0).data();
            List<Result> resultList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                resultList.add(new Result(conceptList.get(i).name(), conceptList.get(i).value()));
            }
            Intent showResult = new Intent(context, showResult.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Result", (Serializable) resultList);
            showResult.putExtras(bundle);
            showResult.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(showResult);
        }
    }

    public File saveBitmapToFile(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}


// test code
// use this when the Clarifai network error occurs

//public class getPredictResult extends AsyncTask<String, Void, List<Result>> {
//
//    public final static String EXTRA_RESULT_MESSAGE = "com.example.licor.gourmet.GET_PREDICT_RESULT";
//
//    Context context;
//    public getPredictResult(Context context) {
//        this.context = context.getApplicationContext();
//    }
//
//    ClarifaiClient client =  new ClarifaiBuilder("Bnm3TxPIfsDYbWTfOlblj42uV7weQF3SWSumGUwQ", "LPo6bFVgbZrWkAf2c0Lv9Sb0G3fotrrQBlm0K3q5")
//            .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
//            .buildSync(); // or use .build() to get a Future<ClarifaiClient>
//
//    @Override
//    protected List<Result> doInBackground(String... photoPath) {
//
//        List<Result> resultList = new ArrayList<>();
//        resultList.add(new Result("blueberry", (float) 0.936));
//        resultList.add(new Result("cereal", (float) 0.898));
//        resultList.add(new Result("oatmeal", (float) 0.868));
//        resultList.add(new Result("muesli", (float) 0.867));
//        resultList.add(new Result("artichoke", (float) 0.868));
//        return resultList;
//    }
//
//    @Override
//    protected void onPostExecute(List<Result> response) {
//
//        Intent showResult = new Intent(context, showResult.class);
//
//        showResult.putExtra("Result", (Serializable) response);
//        showResult.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(showResult);
//    }
//
//    public File saveBitmapToFile(File file){
//        try {
//            // BitmapFactory options to downsize the image
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            o.inSampleSize = 6;
//            // factor of downsizing the image
//
//            FileInputStream inputStream = new FileInputStream(file);
//            //Bitmap selectedBitmap = null;
//            BitmapFactory.decodeStream(inputStream, null, o);
//            inputStream.close();
//
//            // The new size we want to scale to
//            final int REQUIRED_SIZE=75;
//
//            // Find the correct scale value. It should be the power of 2.
//            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
//
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            inputStream = new FileInputStream(file);
//
//            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
//            inputStream.close();
//
//            // here i override the original image file
//            file.createNewFile();
//            FileOutputStream outputStream = new FileOutputStream(file);
//
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
//
//            return file;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}