package it.unisannio.www.treasurehunt;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    private EditText email = null;
    private EditText username = null;
    private EditText password = null;
    private Button submit =null;
    private ProgressBar bar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        bar = findViewById(R.id.progressBar);
    }

    public void submit(View view){
        if(!isNetworkAvailable())
        {
            Toast to = Toast.makeText(getApplicationContext(), "Tentativo di connessione fallito. Attiva connessione dati", Toast.LENGTH_LONG);
            to.show();
        }
        else {
            String mail = email.getText().toString();
            String user = username.getText().toString();
            String pwd = password.getText().toString();
            String url = "http://treshunte.altervista.org/registration.php?email=" + mail + "&name=" + user + "&password=" + pwd;
            if (!mail.contains("@") || user.matches("") || pwd.matches("")) {
                Toast.makeText(getApplicationContext(), "Dati inseriti non validi", Toast.LENGTH_LONG).show();
            } else {
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
                if (stato == 100) {
                    bar.setProgress(100);
                }
                resp = rq.getResult();
                Toast to = Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG);
                to.show();
                if (resp.equalsIgnoreCase("Registrazione Avvenuta")) {
                    Intent intent = new Intent("android.intent.action.Home");
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else {
                    to = Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG);
                    to.show();
                }
                bar.setProgress(0);
            }
        }

    }
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
