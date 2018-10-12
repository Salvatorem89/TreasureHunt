package it.unisannio.www.treasurehunt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Answer extends AppCompatActivity {

    private RadioButton risposta1;
    private RadioButton risposta2;
    private RadioButton risposta3;
    private RadioGroup answerRadioGroup;
    private ArrayList<Checkpoint> percorso;
    private Checkpoint checkpoint;
    private int nextId;
    private long start;
    private  String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        String resp = "";
        risposta1 = new RadioButton(this);
        risposta2 = new RadioButton(this);
        risposta3 = new RadioButton(this);
        answerRadioGroup = new RadioGroup(this);

        percorso = (ArrayList<Checkpoint>) getIntent().getExtras().get("percorso");
        checkpoint = (Checkpoint) getIntent().getExtras().get("checkpoint");
        nextId = getIntent().getExtras().getInt("nextId");
        start = getIntent().getExtras().getLong("start");
        user = getIntent().getExtras().getString("user");

        if(!isNetworkAvailable())
        {
            Toast to = Toast.makeText(getApplicationContext(), "Tentativo di connessione fallito. Attiva connessione dati", Toast.LENGTH_LONG);
            to.show();
        }
        else {
            String url = "http://treshunte.altervista.org/answer.php?question=" + checkpoint.getQuestion();
                DBRequest rq = new DBRequest(url);
                int stato = 0;
                stato = rq.getStato();
                int progress = 0;
                while (stato != 100) {
                    if (stato != progress) {
                        progress = stato;
                    }
                    stato = rq.getStato();
                }
                resp = rq.getResult();
            }

        try {
            ArrayList<String> answers = getAnswers(resp);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(this);
            textView.setText(checkpoint.getQuestion().replace("_", " "));
            risposta1.setText(answers.get(0));
            risposta2.setText(answers.get(1));
            risposta3.setText(answers.get(2));
            answerRadioGroup.addView(risposta1);
            answerRadioGroup.addView(risposta2);
            answerRadioGroup.addView(risposta3);
            linearLayout.addView(textView);
            linearLayout.addView(answerRadioGroup);
            this.setContentView(linearLayout);

            risposta1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Risposta errata", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent("android.intent.action.Challenge");
                    intent.putExtra("percorsoScelto", percorso);
                    intent.putExtra("nextId", nextId);
                    intent.putExtra("start", start - 30000);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            risposta2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Risposta errata", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent("android.intent.action.Challenge");
                    intent.putExtra("percorsoScelto", percorso);
                    intent.putExtra("nextId", nextId);
                    intent.putExtra("start", start - 30000);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            risposta3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Risposta corretta", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent("android.intent.action.Challenge");
                    intent.putExtra("percorsoScelto", percorso);
                    intent.putExtra("nextId", nextId);
                    intent.putExtra("start", start);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getAnswers(String resp) throws JSONException {
        ArrayList<String> answers = new ArrayList<String>();
        JSONObject obj = new JSONObject(resp);
        String answer1 = obj.getString("answer1");
        String answer2 = obj.getString("answer2");
        String correctAnswer = obj.getString("correctanswer");

        answers.add(answer1);
        answers.add(answer2);
        answers.add(correctAnswer);
        return answers;
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
