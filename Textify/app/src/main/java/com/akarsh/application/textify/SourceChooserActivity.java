package com.akarsh.application.textify;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SourceChooserActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private CameraView mCameraView;
    private WebView videoView;
    private CardView cameraCardView, galleryCardView;
    private ImageView cameraViewAlpha, galleryViewAlpha;
    final int CLICK_ACTION_THRESHOLD = 200;
    float startX, startY;
    private static final String TAG = "SourceChooserTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_chooser);

        videoView = findViewById(R.id.videoView);
        cameraCardView = findViewById(R.id.cameraCardView);
        galleryCardView= findViewById(R.id.galleryCardView);
        cameraViewAlpha = findViewById(R.id.cameraViewAlpha);
        galleryViewAlpha = findViewById(R.id.galleryViewAlpha);

        cameraCardView.setOnTouchListener(this);
        galleryCardView.setOnTouchListener(this);
        videoView.setOnTouchListener(this);
        cameraCardView.setOnClickListener(this);
        galleryCardView.setOnClickListener(this);
        videoView.loadUrl("file:///android_asset/gallery.gif");

        EasyImage.configuration(this).setImagesFolderName("Textify");

        mCameraView = findViewById(R.id.camera);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName+".jpg");
        File compressed;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            Compressor compressor = new Compressor(SourceChooserActivity.this);
//            compressed = compressor.compressToFile(mypath, "compressed.jpg");

            Log.d(TAG, mypath.getAbsolutePath());

//            compressed = new Compressor(this)
//                    .setQuality(75)
//                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                    .setDestinationDirectoryPath(directory.getAbsolutePath())
//                    .compressToFile(mypath, "compressed.jpg");

//            Log.d(TAG, "Compressed image save in " + compressed.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    saveToInternalStorage(bitmap, "original");
                    Intent intent = new Intent(SourceChooserActivity.this, ModelChooserActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
        else {*/
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    Toast.makeText(SourceChooserActivity.this, "ERROR!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                @Override
                public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagesFiles.get(0).getAbsolutePath());
                    saveToInternalStorage(bitmap, "original");
                    Toast.makeText(SourceChooserActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//                    CropImage.activity(Uri.fromFile(imagesFiles.get(0))).start(SourceChooserActivity.this);

                    Intent intent = new Intent(SourceChooserActivity.this, CropImageActivity.class);
                    startActivity(intent);
                }
            });
//        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                if(view==videoView)
                    galleryViewAlpha.setAlpha(0.6f);
                else if(view==cameraCardView)
                    cameraViewAlpha.setAlpha(0.8f);
                break;

            case MotionEvent.ACTION_UP:
                float endX = motionEvent.getX();
                float endY = motionEvent.getY();
                if(view==videoView) {
                    galleryViewAlpha.setAlpha(0.4f);
                    if(isAClick(startX, endX, startY, endY)) {
                        Log.d("aakarsh", "Gallery Touch");
                        galleryCardView.performClick();
                    }
                }
                else if(view==cameraCardView) {
                    cameraViewAlpha.setAlpha(0.6f);
                    if (isAClick(startX, endX, startY, endY)) {
                        Log.d("aakarsh", "Camera Touch");
                        cameraCardView.performClick();
                    }
                }
                break;
        }

        return true;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    @Override
    public void onClick(View view) {
        if(view==cameraCardView) {
            //ToDo: Open Camera Intent
            Intent intent = new Intent(SourceChooserActivity.this, ImageCaptureActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Opening Camera", Toast.LENGTH_SHORT).show();
        } else if(view==galleryCardView) {
            /*CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(SourceChooserActivity.this);
            Toast.makeText(this, "Opening Gallery", Toast.LENGTH_SHORT).show();*/
            EasyImage.openGallery(SourceChooserActivity.this, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
