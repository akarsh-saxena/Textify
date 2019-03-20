package com.akarsh.application.textify;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class RecognizeActivity extends AppCompatActivity implements OnDataSendToActivity {

    private EditText recognizedText;
    private Button edit, done;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        recognizedText = findViewById(R.id.recognizedText);
        edit = findViewById(R.id.edit);
        done = findViewById(R.id.done);

        ActionBar actionBar = getSupportActionBar();
        android.app.ActionBar bar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        else if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        String model = getIntent().getStringExtra("model");

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, "compressed.jpg");

        UploadFileToServer uploadFileToServer = new UploadFileToServer(this, this);
        uploadFileToServer.execute(f.getAbsolutePath(), model);

        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recognizedText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        edit.setEnabled(false);
                        done.setEnabled(true);
                    }
                }
        );

        done.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recognizedText.setInputType(InputType.TYPE_NULL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        done.setEnabled(false);
                        edit.setEnabled(true);
                    }
                }
        );
    }

    @Override
    public void sendData(String str) {
        recognizedText.setText(str);
    }
}
