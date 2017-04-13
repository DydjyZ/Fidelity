package fr.badappsstudio.dydjyz.fidelity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class Accueilfidelite extends AppCompatActivity implements View.OnClickListener {
    TextView textView, textView6, textView9, textView3, textView19, textVie;
    private ProgressDialog pDialog;
    private Handler handler;
    int Fidel;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://badappsstudio.fr/Fidelity/connect.php";
    private static final String LOGIN_PASSAGE = "http://badappsstudio.fr/Fidelity/passage.php";
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_NOM = "Nom";
    private static final String TAG_PRENOM = "Prenom";
    private static final String TAG_PASSAGE = "Passage";
    private static final String TAG_DATE = "Date";
    private static final String TAG_MONEY = "Money";
    int success, passage, success2;
    String Money;
    Button buttonreset, buttonachat, buttonmoney, button3, button8;
    int addno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueilfidelite);


        textView = (TextView) findViewById(R.id.textView9);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Accueilfidelite.this);
        Fidel = Integer.parseInt(sharedPreferences.getString("Fidel", "000"));
        textVie = (TextView) findViewById(R.id.textView);
        textVie.setText("Carte de fidélité n°" + Fidel);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView9 = (TextView) findViewById(R.id.textView9);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView19 = (TextView) findViewById(R.id.textView19);
        buttonreset = (Button) findViewById(R.id.button2);
        buttonreset.setOnClickListener(this);
        buttonmoney = (Button) findViewById(R.id.button5);
        buttonmoney.setOnClickListener(this);
        button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        passage = 0;
        new AttemptLogin().execute();
    }
    public void onPause(){

        super.onPause();
        if(pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonreset) {
            Intent intent = new Intent(Accueilfidelite.this, Resetcard.class);
            startActivity(intent);
            finish();
        }
        if(v == buttonmoney) {
            Intent intent = new Intent(Accueilfidelite.this, Addmoney.class);
            startActivity(intent);
            finish();
        }
        if(v == button3)
        {
            // +
            addno = 1;
            new AttemptLogin2().execute();
        }
        if(v == button8)
        {
            // -
            addno = 2;
            new AttemptLogin2().execute();
        }
    }
    class AttemptLogin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Accueilfidelite.this);
            pDialog.setMessage("Recherche en cours...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", ""+Fidel));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                // checking  log for json response
                Log.d("Login attempt", json.toString());

                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                String prenom = json.getString(TAG_PRENOM);
                String nom = json.getString(TAG_NOM);
                passage = json.getInt(TAG_PASSAGE);
                String date = json.getString(TAG_DATE);
                Money = json.getString(TAG_MONEY);
                savePreferences("prenom", prenom);
                savePreferences("nom", nom);
                savePreferences("passage", ""+passage);
                savePreferences("date", date);
                savePreferences("money", Money);
                if (success == 1) {
                    // SI LA PERSONNE EST BIEN CONNECTée !!!!!
                    // this finish() method is used to tell android os that we are done with current //activity now! Moving to other activity
                    return json.getString(TAG_NOM);
                }else{
                    return json.getString(TAG_NOM);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * Once the background process is done we need to  Dismiss the progress dialog asap
         * **/
        protected void onPostExecute(String message) {

            pDialog.dismiss();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Accueilfidelite.this);
            Toast.makeText(Accueilfidelite.this, "Fin du processus ", Toast.LENGTH_SHORT).show();
            if(success ==1) {
                textView6.setText(sharedPreferences.getString("prenom", "Erreur")+" "+ sharedPreferences.getString("nom", "Erreur"));
                textView9.setText(sharedPreferences.getString("passage", "Erreur"));
                textView3.setText("Dernier passage : "+sharedPreferences.getString("date", "Erreur"));
                textView19.setText(sharedPreferences.getString("money", "Erreur")+"€ collectés");
            } else {
                Toast.makeText(Accueilfidelite.this, "Carte non reconnue : création", Toast.LENGTH_LONG).show();
                // ICI FAIRE PROCESSUS DE CREATION DE CARTE DE FIDELITé !
                Intent intent = new Intent(Accueilfidelite.this, Createcard.class);
                startActivity(intent);
                finish();
            }
        }
    }
    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    // EDIT PASSAGE ----------------------------------------------------------
    class AttemptLogin2 extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Accueilfidelite.this);
            pDialog.setMessage("Mise à jour du passage...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", ""+Fidel));
                params.add(new BasicNameValuePair("type", ""+addno));
                // 1 = +
                // 2 = -
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_PASSAGE, "POST", params);

                // checking  log for json response
                Log.d("Login attempt", json.toString());

                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    int passage = json.getInt(TAG_PASSAGE);
                    savePreferences("passage", ""+passage);
                    String date = json.getString(TAG_DATE);
                    savePreferences("date", date);
                    // SI LA PERSONNE EST BIEN CONNECTée !!!!!
                    // this finish() method is used to tell android os that we are done with current //activity now! Moving to other activity
                    return json.getString(TAG_NOM);
                }else{
                    return json.getString(TAG_NOM);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * Once the background process is done we need to  Dismiss the progress dialog asap
         * **/
        protected void onPostExecute(String message) {

            pDialog.dismiss();
            Toast.makeText(Accueilfidelite.this, "Fin du processus", Toast.LENGTH_SHORT).show();
            if(success ==1) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Accueilfidelite.this);
                textView9.setText(sharedPreferences.getString("passage", "Erreur"));
                textView3.setText("Dernier passage : "+sharedPreferences.getString("date", "Erreur"));
            } else {
                Toast.makeText(Accueilfidelite.this, "Erreurn", Toast.LENGTH_LONG).show();
                // ICI FAIRE PROCESSUS DE CREATION DE CARTE DE FIDELITé !
            }
        }
    }
}
