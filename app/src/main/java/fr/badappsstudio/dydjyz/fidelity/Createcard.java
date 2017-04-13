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
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class Createcard extends AppCompatActivity implements View.OnClickListener {
    Button button;
    String leprenom, lenom;
    int Fidel;
    EditText editText, editText2;
    private ProgressDialog pDialog;
    private Handler handler;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://badappsstudio.fr/Fidelity/create.php";
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_NOM = "Nom";
    private static final String TAG_PRENOM = "Prenom";
    private static final String TAG_PASSAGE = "Passage";
    int success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcard);
        // getIntent().getStringExtra("Number")
        button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Createcard.this);
        Fidel = Integer.parseInt(sharedPreferences.getString("Fidel", "000"));
    }

    public void onClick(View v) {
        // ACTION DU BOUTON
        leprenom = editText.getText().toString();
        lenom = editText2.getText().toString();
        new AttemptLogin().execute();
    }
    public void onPause(){

        super.onPause();
        if(pDialog != null)
            pDialog.dismiss();
    }
    class AttemptLogin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Createcard.this);
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
                params.add(new BasicNameValuePair("prenom", leprenom));
                params.add(new BasicNameValuePair("nom", lenom));
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // checking  log for json response
                Log.d("Login attempt", json.toString());

                // success tag for json
                success = json.getInt(TAG_SUCCESS);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Createcard.this);
            Toast.makeText(Createcard.this, "Fin du processus "+Fidel, Toast.LENGTH_SHORT).show();
            if(success ==1) {
                Intent intent = new Intent(Createcard.this, Accueilfidelite.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Createcard.this, "Carte non reconnue !", Toast.LENGTH_LONG).show();
                // ICI FAIRE PROCESSUS DE CREATION DE CARTE DE FIDELITé !
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
