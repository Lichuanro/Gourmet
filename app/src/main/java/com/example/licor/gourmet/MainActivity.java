package com.example.licor.gourmet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.io.File;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_PHOTO = 2;
    public final static String EXTRA_MESSAGE = "com.example.licor.gourmet.SELECT_PHOTO";
    Uri outputFileUri;

    private MainFragment mainFragment;
    private BlogFragment blogFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outputFileUri != null) {
            outState.putString("outputFileUri", outputFileUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            outputFileUri = Uri.parse((String.valueOf(savedInstanceState.get("outputFileUri"))));
        }
        setContentView(R.layout.activity_main);

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_nav_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home_icon, "Home").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.blog_icon, "Blog").setActiveColorResource(R.color.blue))
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);

        initMainFragment();
    }

    public void takePhoto(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        File file = FileUtils.createImageFile();
        outputFileUri = Uri.fromFile(file);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void selectPhoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent uploadPhotoActivity = new Intent(this, uploadPhoto.class);
            uploadPhotoActivity.setData(outputFileUri);
            startActivity(uploadPhotoActivity);
        }
        if( requestCode == SELECT_PHOTO && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            String realPath = getRealPathFromURI(this, selectedImageUri);
            Intent uploadPhotoActivity = new Intent(this, uploadPhoto.class);
            uploadPhotoActivity.setData(selectedImageUri);
            uploadPhotoActivity.putExtra(EXTRA_MESSAGE, realPath);
            startActivity(uploadPhotoActivity);
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void initMainFragment() {
        mainFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, mainFragment);
        transaction.commit();
    }

    @Override
    public void onTabSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position){
            case 0:
                if(mainFragment == null){
                    mainFragment = new MainFragment();
                }
                transaction.replace(R.id.fragment_content,mainFragment);
                break;
            case 1:
                if(blogFragment == null){
                    blogFragment = new BlogFragment();
                }                transaction.replace(R.id.fragment_content,blogFragment);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
