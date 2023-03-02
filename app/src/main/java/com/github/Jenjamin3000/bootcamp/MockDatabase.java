package com.github.Jenjamin3000.bootcamp;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MockDatabase implements SDPDatabase{

    TextView phone;
    TextView email;
    TextView answer;

    private final Map<String, String> map = new HashMap<>();

    public MockDatabase(TextView email, TextView phone, TextView answer){
        this.email = email;
        this.phone = phone;
        this.answer = answer;
    }


    @Override
    public void get(View view) {
        String value = map.get(phone.getText().toString());

        Toast.makeText(view.getContext(), "Value is: " +value, Toast.LENGTH_SHORT).show();
        answer.setText(value);
    }

    @Override
    public void set(View view) {
        String emailText = email.getText().toString();
        String phoneText = phone.getText().toString();

        if(map.containsKey(phoneText)){
            map.replace(phoneText, emailText);
        } else {
            map.put(phoneText, emailText);
        }
    }
}
