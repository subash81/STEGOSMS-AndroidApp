package com.mastpro.dpsu.stegosms;

import com.mastpro.dpsu.stegosms.R;
import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SendCipherMsgActivity extends ActionBarActivity {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_cipher_msg_activity);
    }

    public void pickContact(View v) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    public void onSendClick(View v) {
        String SENT="SMS_SENT";
        ArrayList<PendingIntent> sentPIs = new ArrayList<PendingIntent>();
        PendingIntent sentPI=PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
        BroadcastReceiver smsListener=new BroadcastReceiver(){
            @Override public void onReceive(    Context context,    Intent intent){
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(SendCipherMsgActivity.this, "the sms was sent successfully", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(SendCipherMsgActivity.this, "failed in sending the sms", Toast.LENGTH_LONG).show();
                }
            }
        };
        IntentFilter filter=new IntentFilter(SENT);
        registerReceiver(smsListener,filter);

        SmsManager smsManager = SmsManager.getDefault();
        String body = getIntent().getExtras().getString("finalMsg");
        String recipient = ((TextView)findViewById(R.id.recipient)).getText().toString();
        ArrayList<String> data = smsManager.divideMessage(body);
        sentPIs.add(sentPI);
        smsManager.sendMultipartTextMessage(recipient, null, data, sentPIs, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};


                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                // Do something with the phone number...
                EditText recipient = (EditText) findViewById(R.id.recipient);

                recipient.setText(number);
            }
        }
    }
}
