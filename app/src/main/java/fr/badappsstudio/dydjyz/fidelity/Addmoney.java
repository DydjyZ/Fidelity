package fr.badappsstudio.dydjyz.fidelity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class Addmoney extends AppCompatActivity implements View.OnClickListener  {
    Button button;
    TextView textView7;
    private ProgressDialog pDialog;
    private Handler handler;
    int Fidel, success;
    EditText editText;
    String addingmoney;
    boolean add = true;
    TextView textView8;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://badappsstudio.fr/Fidelity/addmoney.php";
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_NOM = "Nom";
    private static final String TAG_PRENOM = "Prenom";
    private static final String TAG_PASSAGE = "Passage";
    private static final String TAG_DATE = "Date";
    private static final String TAG_MONEY = "Money";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoney);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        textView8 = (TextView) findViewById(R.id.textView8);
        textView8.setOnClickListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Addmoney.this);
        textView7 = (TextView) findViewById(R.id.textView7);
        textView7.setText(sharedPreferences.getString("money", "Erreur")+" €");
        editText = (EditText) findViewById(R.id.editText3);
    }

    @Override
    public void onClick(View v) {
        if(v == button)
        {
            new AttemptLogin().execute();
            addingmoney = editText.getText().toString();
        }
        if(v == textView8) {
            if(add == true)
            {
                textView8.setText("-");
                add = false;
            } else {
                textView8.setText("+");
                add = true;
            }
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
            pDialog = new ProgressDialog(Addmoney.this);
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
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Addmoney.this);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", ""+Integer.parseInt(sharedPreferences.getString("Fidel", "000"))));
                if(add == true) {
                    params.add(new BasicNameValuePair("state", ""+1));
                } else {
                    params.add(new BasicNameValuePair("state", ""+2));
                }
                params.add(new BasicNameValuePair("money", ""+sharedPreferences.getString("money", "Erreur")));
                params.add(new BasicNameValuePair("adding", addingmoney));
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                // checking  log for json response
                Log.d("Login attempt", json.toString());
                 String ok = "";
                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // SI LA PERSONNE EST BIEN CONNECTée !!!!!
                    // this finish() method is used to tell android os that we are done with current //activity now! Moving to other activity
                    return ok;
                }else{
                    return ok;

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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Addmoney.this);
            Toast.makeText(Addmoney.this, "Fin du processus ", Toast.LENGTH_SHORT).show();
            if(success ==1) {
                Intent intent = new Intent(Addmoney.this, Accueilfidelite.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Addmoney.this, "ERRRREEEEEUUUUUURRRRRR", Toast.LENGTH_LONG).show();
                // ICI FAIRE PROCESSUS DE CREATION DE CARTE DE FIDELITé !
                Intent intent = new Intent(Addmoney.this, Createcard.class);
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
