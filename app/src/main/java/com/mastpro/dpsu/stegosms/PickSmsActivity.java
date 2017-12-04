package com.mastpro.dpsu.stegosms;

import java.util.ArrayList;
import java.util.*;


import com.mastpro.dpsu.stegosms.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PickSmsActivity extends Activity {
    /** Called when the activity is first created. */

    private static Map<Character, Character> small = new HashMap<>();
    static {
        small.put('a', '\uFF41');
        small.put('b', '\u0253');
        small.put('c', '\uFF43');
        small.put('d', '\u217E');
        small.put('e', '\uFF45');

        small.put('f', '\u1E9D');
        small.put('g', '\u0253');
        small.put('h', '\uFF48');
        small.put('i', '\uFF49');
        small.put('j', '\uFF4A');

        small.put('k', '\u1D0B');
        small.put('l', '\uFF4C');
        small.put('m', '\u006D');
        small.put('n', '\u0272');
        small.put('o', '\uFF4F');

        small.put('p', '\uFF50');
        small.put('q', '\u1D90');
        small.put('r', '\u027C');
        small.put('s', '\uFF53');
        small.put('t', '\u021B');

        small.put('u', '\u1D1C');
        small.put('v', '\uFF56');
        small.put('w', '\u1D21');
        small.put('x', '\uFF58');
        small.put('y', '\uFF59');

        small.put('z', '\u1D22');
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_sms_activity);

        ListView lViewSMS = (ListView) findViewById(R.id.listViewSMS);

        lViewSMS.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3)
            {
                String type = getIntent().getType();
                if (getIntent().getType().equals("encrypt")){
                    String smsBody = ((TextView)view).getText().toString();

                    String msg = getIntent().getExtras().getString("msg");

                    smsBody = smsBody.toLowerCase();
                    smsBody = smsBody.replaceAll(" +", " ");

                    int counter = 0;
                    for ( int i = 0; i < smsBody.length(); i++ ) {
                        char ch=  smsBody.charAt(i);
                        int value = (int) ch;
                        if (value >= 97 && value <= 122 || ch == ' '){
                            counter++;
                        }
                    }

                    if ((msg.length() + 1) * 6 > counter){
                        Toast.makeText(PickSmsActivity.this, "the sms you chose doesnt contain enough english letters or spaces", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String finalMsg = encryptMsgIntoCarrier(msg, smsBody);

                    Intent intent = new Intent(PickSmsActivity.this, SendCipherMsgActivity.class);
                    intent.putExtra("finalMsg", finalMsg);
                    startActivity(intent);
                }else if (getIntent().getType().equals("decrypt")){
                    String smsBody = ((TextView)view).getText().toString();
                    String secret = decryptMsg(smsBody);

                    Intent intent = new Intent(PickSmsActivity.this, OutputDecodeActivity.class);
                    intent.putExtra("secret", secret);
                    startActivity(intent);
                }

            }
        });

        if(fetchInbox()!=null)
        {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox());
            lViewSMS.setAdapter(adapter);
        }
    }

    private String decryptMsg(String carrier){
        ArrayList<Boolean> msgInBits = new ArrayList<Boolean>();
        for (int i = 0; i < carrier.length(); i++){
            int charNum = (int)carrier.charAt(i);

            if (97 <= charNum && charNum <= 122)
                msgInBits.add(false);
            else if(small.containsValue((char)charNum))
                msgInBits.add(true);
            else if(charNum == 32){
                i++;
                if((int)carrier.charAt(i) == 32)
                    msgInBits.add(true);
                else{
                    msgInBits.add(false);
                    i--;
                }
            }
        }
        String secret = "";
        for (int i = 0; i < msgInBits.size(); i += 6){
            int value = 0;
            for (int j = 0; j < 6; j++){
                if(msgInBits.get(i + j)){
                    value += 1 << j;
                }
            }
            if(value == 63)
                return secret;
            if(0 <= value && value <= 9)
                value += 48;
            else if (10 <= value && value <= 35)
                value += 87;
            else if (value == 36)
                value = 32;

            secret += (char)value;
        }

        return secret; }

    private String encryptMsgIntoCarrier(String msg, String carrier){

        String encryptedMsg = "";
        ArrayList<Boolean> msgInBits = new ArrayList<Boolean>();
        msg += (char)63;
        msg = msg.toLowerCase();
        for(int i = 0; i < msg.length(); i++)
        {
            int charNum = (int)msg.charAt(i);

            if(48 <= charNum && charNum <= 57)
                charNum -= 48;
            else if (97 <= charNum && charNum <= 122)
                charNum -= 87;
            else if (charNum == 32)
                charNum = 36;

            for(int j = 0; j < 6; j++){
                msgInBits.add(!((charNum & (1 << j)) == 0));
            }
        }

        int j;
        for(j = 0; j < msgInBits.size(); j++){
            char c = carrier.charAt(j);
            int value = (int) c;
            if(value >= 97 && value <= 122){
                if (msgInBits.get(j)) {
                    //look up for the value in new map to find the corresponding small letter
                    encryptedMsg +=  fetchUnicodeChar(c);
                    //encryptedMsg +=  (char)(value - 32);
                } else {
                    encryptedMsg += c;
                }
            }else if(c == ' '){
                if (msgInBits.get(j)) {
                    encryptedMsg += "  ";
                } else {
                    encryptedMsg += c;
                }
            }
        }

        for(; j< carrier.length(); j++){
            char c = carrier.charAt(j);
            int value = (int) c;
            if(value >= 97 && value <= 122){
                if (Math.random() > 0.65) {
                    //look up for the value in new map to find the corresponding small letter
                    encryptedMsg +=  fetchUnicodeChar(c);
                    //encryptedMsg +=  (char)(value - 32);
                } else {
                    encryptedMsg += c;
                }
            }else if(c == ' '){
                encryptedMsg += "  ";
            }
        }

        return encryptedMsg;
    }

    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED, data);
        } else {
            getParent().setResult(Activity.RESULT_CANCELED, data);
        }
        finish();
    }

    public ArrayList fetchInbox()
    {
        ArrayList sms = new ArrayList();

        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);

        cursor.moveToFirst();
        String body = cursor.getString(3);

        sms.add(body);
        while  (cursor.moveToNext())
        {
            body = cursor.getString(3);

            sms.add(body);
        }

        return sms;

    }

    public char fetchUnicodeChar(char findUni)
    {


        return small.get(findUni);

    }
}