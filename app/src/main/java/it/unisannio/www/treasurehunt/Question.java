package it.unisannio.www.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

public class Question extends AppCompatActivity {
    private double latitude,longitude;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        spinner = findViewById(R.id.spinner);
        if(getIntent().getExtras()!=null)
        {
            latitude= getIntent().getExtras().getDouble("lat");
            longitude=getIntent().getExtras().getDouble("long");
        }
    }

    public void cancel(View view){
        startActivity(new Intent("android.intent.action.CreateChallenge"));
    }

    public void ok(View view){
        String q = spinner.getSelectedItem().toString();
        Log.i("Domanda ",q);
        Intent intent = new Intent("android.intent.action.CreateChallenge");
        intent.putExtra("question", q);
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longitude);
        startActivity(intent);
    }
}