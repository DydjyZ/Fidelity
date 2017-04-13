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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class Resetcard extends AppCompatActivity implements View.OnClickListener {
    TextView textView, textView6, textView7, textView9, textView3;
    private ProgressDialog pDialog;
    private Handler handler;
    int Fidel;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://badappsstudio.fr/Fidelity/reset.php";
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_NOM = "Nom";
    private static final String TAG_PRENOM = "Prenom";
    private static final String TAG_PASSAGE = "Passage";
    private static final String TAG_DATE = "Date";
    int success, passage, success2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetcard);

        textView = (TextView) findViewById(R.id.textView9);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Fidel = Integer.parseInt(sharedPreferences.getString("Fidel", "000"));
        new AttemptLogin().execute();
    }
    public void onPause(){

        super.onPause();
        if(pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {

    }
    class AttemptLogin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Resetcard.this);
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
                if (success == 1) {
                    // SI LA CARTE EST BIEN SUPPRIMEE!
                    // this finish() method is used to tell android os that we are done with current //activity now! Moving to other activity
                    return json.getString(TAG_NOM);
                }else{
                    return json.getString(TAG_NOM);
                    // PROBLEME
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Resetcard.this);

            if(success == 1) {
                Toast.makeText(Resetcard.this, "Carte remise à zero", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Resetcard.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Resetcard.this, "Erreur", Toast.LENGTH_LONG).show();
                // ICI FAIRE PROCESSUS DE CREATION DE CARTE DE FIDELITé !
                Intent intent = new Intent(Resetcard.this, Accueilfidelite.class);
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
}
