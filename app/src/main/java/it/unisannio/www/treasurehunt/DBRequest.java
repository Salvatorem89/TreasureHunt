package it.unisannio.www.treasurehunt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

class DBRequest {
    private String url;
    public String textviewDatiRicevuti = "";
    public int stato;

    public DBRequest(String url){
        this.url = url;
        DBConnection con = new DBConnection();
        con.execute(url);

    }
    public String getResult(){
        return textviewDatiRicevuti;
    }

    public int getStato() {
        return stato;
    }

    class DBConnection extends AsyncTask<String,String,String>{

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
                        while ((line = reader.readLine()) != null) {
                            Log.i("Buffer",line);
                            sb.append(line + "\n");
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


            Log.i("SendQUERY", result);
            textviewDatiRicevuti = result;
            stato = 100;
            publishProgress(stato + "%");
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i("Stato", stato + "%");
        }

        @Override
        protected void onPostExecute(String result) {
            // aggiorno la textview con il risultato ottenuto
            textviewDatiRicevuti = result;
        }
    }
}
