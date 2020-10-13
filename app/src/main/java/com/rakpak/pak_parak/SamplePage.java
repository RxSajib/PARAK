package com.rakpak.pak_parak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class SamplePage extends AppCompatActivity {

    private Button buttonrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_page);

        buttonrecord = findViewById(R.id.RecodButtons);


        buttonrecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                }

                if(event.getAction() == MotionEvent.ACTION_UP){

                }

                return true;
            }
        });
    }
}