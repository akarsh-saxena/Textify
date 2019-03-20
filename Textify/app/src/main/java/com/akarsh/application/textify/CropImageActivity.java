package com.akarsh.application.textify;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class CropImageActivity extends AppCompatActivity {

    CropImageView cropImageView;
    FloatingActionButton fabGo;
    private final String TAG = "CropImageActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView = findViewById(R.id.cropImageView);
        fabGo = findViewById(R.id.fabGo);

        ActionBar actionBar = getSupportActionBar();
        android.app.ActionBar bar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        else if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, "original.jpg");
        cropImageView.setImageUriAsync(Uri.fromFile(f));

        fabGo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File mypath = new File(directory, "original.jpg");
                        File compressed;
                        try {
                            compressed = new Compressor(CropImageActivity.this)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(directory.getAbsolutePath())
                                    .compressToFile(mypath, "compressed.jpg");
                            Log.d(TAG, "Compressed image save in " + compressed.getPath());
                            Intent intent = new Intent(CropImageActivity.this, ModelChooserActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                            Toast.makeText(cw, "Error!!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crop_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bitmap cropped;
        if(item.getItemId()==R.id.crop) {
            cropped = cropImageView.getCroppedImage();
            saveToInternalStorage(cropped, "original");
            intent = new Intent(CropImageActivity.this, ModelChooserActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.flip)
            cropImageView.flipImageVertically();
        else if(item.getItemId()==R.id.rotate)
            cropImageView.rotateImage(90);
        else
            return false;
        return true;
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
//            Compressor compressor = new Compressor(CropImageActivity.this);
//            compressed = compressor.compressToFile(mypath, "compressed.jpg");

            Log.d(TAG, mypath.getAbsolutePath());

            compressed = new Compressor(this)
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
}
