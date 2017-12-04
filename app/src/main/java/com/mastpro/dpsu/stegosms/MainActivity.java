package com.mastpro.dpsu.stegosms;

import com.mastpro.dpsu.stegosms.R;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onEncryptButtonClick(View v){
        Intent intent = new Intent(this, EmbeddActivity.class);
        startActivity(intent);
    }

    public void onDecryptButtonClick(View v){
        Intent intent = new Intent(this, DecodeActivity.class);
        startActivity(intent);
    }

   
}
