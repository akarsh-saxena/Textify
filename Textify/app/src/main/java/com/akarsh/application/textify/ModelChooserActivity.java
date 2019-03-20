package com.akarsh.application.textify;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

public class ModelChooserActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3;
    String model;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_chooser);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);

        ActionBar actionBar = getSupportActionBar();
        android.app.ActionBar bar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        else if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, "original.jpg");

        image = findViewById(R.id.image);
//        image.setImageDrawable(getDrawableFromPath(f.getAbsolutePath()));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(radioGroup.getCheckedRadioButtonId()==rb1.getId())
                    model = "capital";
                else if(radioGroup.getCheckedRadioButtonId()==rb2.getId())
                        model = "digit";
                    else
                        model = "allcharacters";

                Intent intent = new Intent(ModelChooserActivity.this, RecognizeActivity.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
        });
    }

    public Drawable getDrawableFromPath(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //Here you can make logic for decode bitmap for ignore oom error.
        return new BitmapDrawable(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server_url_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.server_url) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(ModelChooserActivity.this);
            View promptsView = li.inflate(R.layout.server_url_alert_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ModelChooserActivity.this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);
            final SharedPreferences sharedPreferences = getSharedPreferences("server_url", Context.MODE_PRIVATE);
            userInput.setText(sharedPreferences.getString("server_url", "Empty"));

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("server_url", userInput.getText().toString());
                                    editor.apply();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }
        return false;
    }
}
