package com.mastpro.dpsu.stegosms;

import com.mastpro.dpsu.stegosms.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class OutputDecodeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_decode_activity);

        TextView output = (TextView)findViewById(R.id.output);
        String secret = getIntent().getExtras().getString("secret");
        output.setText(secret);
    }
}
