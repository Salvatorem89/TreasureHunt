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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Answer extends AppCompatActivity {

    private CheckBox risposta1;
    private CheckBox risposta2;
    private CheckBox risposta3;
    private Button conferma;
    private Button cancella;
    private ArrayList<Checkpoint> percorso;
    private Checkpoint checkpoint;
    private int nextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        String resp = "";
        risposta1 = new CheckBox(this);
        //risposta2 = new CheckBox(this);
        //risposta3 = new CheckBox(this);
        conferma = findViewById(R.id.confirm);
        cancella = findViewById(R.id.back);

        percorso = (ArrayList<Checkpoint>) getIntent().getExtras().get("percorso");
        checkpoint = (Checkpoint) getIntent().getExtras().get("checkpoint");
        nextId = getIntent().getExtras().getInt("nextId");

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

            risposta1.setText(answers.get(0));
            //risposta2.setText(answers.get(1));
            //risposta3.setText(answers.get(2));
            Log.i("risposta", answers.get(0));
            Log.i("risposta", answers.get(1));
            Log.i("risposta", answers.get(2));
            this.setContentView(risposta1);
            Log.i("risposta", answers.get(0));
            Log.i("risposta", answers.get(1));
            Log.i("risposta", answers.get(2));
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

    private View.OnClickListener getOnClick(final Button button){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Controllo click", "Click avvenuto");
            }
        };
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
