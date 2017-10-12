package com.trumpets.trackmybus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddBusActivity extends AppCompatActivity {

    private EditText tidEditText;
    private EditText busEditText;
    private Button trackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        tidEditText = (EditText) findViewById(R.id.tidEditText);
        busEditText = (EditText) findViewById(R.id.busEditText);
        trackBtn = (Button) findViewById(R.id.trackBtn);

        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackBus();
            }


        });



    }


    private void trackBus() {
        String tid = tidEditText.getText().toString().trim();
        String bus = busEditText.getText().toString().trim();
        Intent trackActivity = new Intent(AddBusActivity.this, MapsActivity.class);
        trackActivity.putExtra("tracker-id", tid);
        trackActivity.putExtra("bus", bus);
        startActivity(trackActivity);
    }
}
