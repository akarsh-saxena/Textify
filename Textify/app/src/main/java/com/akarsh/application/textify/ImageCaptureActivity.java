package com.akarsh.application.textify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class ImageCaptureActivity extends AppCompatActivity {

    private CameraView mCameraView;
    private FloatingActionButton fabCapture;
    public static final String TAG = "ImageCaptureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        final Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mCameraView = findViewById(R.id.camera);
        fabCapture = findViewById(R.id.fabCapture);
        mCameraView.setLifecycleOwner(this);

        mCameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabCapture.getLayoutParams();
        params.setMargins(0, 0,0,getSoftButtonsBarSizePort(this));
        fabCapture.setLayoutParams(params);
        fabCapture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCameraView.capturePicture();
                    }
                }
        );

        mCameraView.addCameraListener(new CameraListener() {
            @SuppressLint("WrongThread")
            @Override
            public void onPictureTaken(byte[] picture) {
                try {
                    CropImage.activity(getUriFromBitmap(picture)).start(ImageCaptureActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static Uri getUriFromBitmap(byte[] bitmapData) throws IOException {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        tempDir.mkdir();
        File tempFile = File.createTempFile("image", ".jpg", tempDir);

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        return Uri.fromFile(tempFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    saveToInternalStorage(bitmap, "original");
                    Intent intent = new Intent(ImageCaptureActivity.this, ModelChooserActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
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
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            compressed = new Compressor(ImageCaptureActivity.this)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(directory.getAbsolutePath())
                    .compressToFile(mypath, "compressed.jpg");
            Log.d(TAG, "Compressed image save in " + compressed.getPath());
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
        mCameraView.destroy();
    }
}
