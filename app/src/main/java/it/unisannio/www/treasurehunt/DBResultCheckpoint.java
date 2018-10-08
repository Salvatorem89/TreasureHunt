package it.unisannio.www.treasurehunt;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SUPER INGEGNERE on 08/10/2018.
 */

public class DBResultCheckpoint {
    private String url;
    public String viewDatiRicevuti = "";
    public int stato;

    public DBResultCheckpoint(String url){
        this.url = url;
        DBConnection con = new DBConnection();
        con.execute(url);
    }

    public String getResult(){
        return viewDatiRicevuti;
    }

    public int getStato() {
        return stato;
    }

    class DBConnection extends AsyncTask<String,String,String> {

        public DBConnection(){

        }

        @Override
        protected String doInBackground(String... params) {
            stato = 0;
            String result = "";
            String page = params[0];

            InputStream is = null;
            publishProgress(stato + "%");
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(page);
                stato =10;
                publishProgress(stato + "%");
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                stato =50;
                publishProgress(stato + "%");
                if(is != null){

                    //converto la risposta in stringa
                    try{
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        stato =60;
                        publishProgress(stato + "%");
                        while (true) {
                            int data=reader.read();
                            if(data==-1){
                                break;
                            }else{
                                sb.append((char)data);
                            }
                        }
                        is.close();

                        result=sb.toString();
                    }catch(Exception e){
                        Log.e("TEST", "Errore nel convertire il risultato "+e.toString());
                    }

                }
                else{//non ho avuto risposta
                    result = "no communication";
                }

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection "+e.toString());
            }

            //convert response to string


            Log.i("SendQUERY", result.toString());
            viewDatiRicevuti = result;
            stato = 100;
            publishProgress(stato + "%");
            return viewDatiRicevuti.toString();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i("Stato", stato + "%");
        }

        @Override
        protected void onPostExecute(String result) {
            // aggiorno la textview con il risultato ottenuto
            viewDatiRicevuti = result;
        }

    }
}
