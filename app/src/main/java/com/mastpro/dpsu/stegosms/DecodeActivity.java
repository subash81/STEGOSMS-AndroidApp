package com.mastpro.dpsu.stegosms;



import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DecodeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_activity);
    }

    public void onPickSmsClick(View v){
        Intent intent = new Intent(this, PickSmsActivity.class);
        intent.setType("decrypt");
        startActivity(intent);
    }
}
