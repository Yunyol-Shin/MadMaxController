package com.example.q.splashcreenexercise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by q on 2017-01-12.
 */

public class OpenActivity extends Activity {

    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.start_activity);

        Button button=(Button)findViewById(R.id.start);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText= (EditText)findViewById(R.id.editText);
                int i = new Integer(editText.getText().toString()).intValue();
                if(i<0 || i>15){
                    Toast.makeText(getBaseContext(), "wrong number", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("id", i);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

}
