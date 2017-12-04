package com.mastpro.dpsu.stegosms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.mastpro.dpsu.stegosms.R;


public class EmbeddActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.embedd_activity);
    }

    public void pickCarrier(View v) {
        if (!checkInputValidity()){
            Toast.makeText(EmbeddActivity.this, "the msg should only contain english letters, numbers and spaces", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, PickSmsActivity.class);
        intent.putExtra("msg", getMsg());
        intent.setType("encrypt");
        startActivity(intent);
    }

    public String getMsg() {
        TextView secret = (TextView) findViewById(R.id.secret);
        return secret.getText().toString();
    }

    private boolean checkInputValidity(){
        String msg = getMsg();
        msg = msg.toLowerCase();

        for (int i = 0; i < msg.length(); i++){
            int charNum = (int)msg.charAt(i);
            if (!(charNum <= 122 && charNum >= 97) && !(charNum <= 57 && charNum >= 48) && charNum != 32)
                return false;
        }

        return true;
    }

}
