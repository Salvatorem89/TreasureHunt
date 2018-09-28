package it.unisannio.www.treasurehunt;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText username = null;
    private EditText password = null;
    private ProgressBar bar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.user);
        password = findViewById(R.id.pwd);
        bar = findViewById(R.id.progressBarLogin);
    }

    public void sign(View view) {
        if(!isNetworkAvailable())
        {
            Toast to = Toast.makeText(getApplicationContext(), "Tentativo di connessione fallito. Attiva connessione dati", Toast.LENGTH_LONG);
            to.show();
        }
        else {
            String user = username.getText().toString();
            String pwd = password.getText().toString();
            if (user.matches("") || pwd.matches("")) {
                Toast.makeText(getApplicationContext(), "Dati inseriti non validi", Toast.LENGTH_LONG).show();
            } else {
                String url = "http://treshunte.altervista.org/login.php?name=" + user + "&password=" + pwd;
                DBRequest rq = new DBRequest(url);
                String resp = "";
                int stato = 0;
                stato = rq.getStato();
                int progress = 0;
                while (stato != 100) {
                    if (stato != progress) {
                        bar.setProgress(stato);
                        progress = stato;
                    }

                    stato = rq.getStato();
                }
                resp = rq.getResult();
                Toast to = Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG);
                if (resp.equalsIgnoreCase("Login avvenuto con successo")) {
                    to.show();
                    startActivity(new Intent("android.intent.action.MAIN"));
                }
                else {
                    to.show();
                }
            }
        }
    }
    public void register(View view){
        startActivity(new Intent("android.intent.action.Registration"));
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
