package hafizzaturrahim.com.poliklinikubantrianonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import hafizzaturrahim.com.poliklinikubantrianonline.adapter.JadwalAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.AntriankuFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.BerandaFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.model.Jadwal;

/**
 * Created by Hafizh on 22/11/2016.
 */

public class LoginActivity extends AppCompatActivity {
    SessionManager session;
    String hasil = null;
    private Button loginButton;
    private TextView txtNotif;
    String inputUsername;
    String inputPassword;
    boolean sucessLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        loginButton = (Button) findViewById(R.id.btnlogin);

        final EditText username = (EditText) findViewById(R.id.idlogin);
        final EditText password = (EditText) findViewById(R.id.passwordlogin);
        txtNotif = (TextView) findViewById(R.id.salahIDorPass);
        txtNotif.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passCorrect = "tes";
                inputUsername = username.getText().toString();
                inputPassword = password.getText().toString();

                if (inputUsername.equals("") || inputPassword.equals("")) {
//                    Toast.makeText(getApplicationContext(), "Semua field harus diisi", Toast.LENGTH_LONG).show();
                    txtNotif.setText("Semua field harus diisi");
                    txtNotif.setVisibility(View.VISIBLE);
                } else {
                    if (checkConnection()) {
                        new GetDataWebService(LoginActivity.this).execute();
                    }
                }
            }
        });
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (!info.isConnected()) {
                Log.v("Internetnya", "ga bisa");
                Toast.makeText(LoginActivity.this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
                return false;

            } else {
                Log.v("Internetnya", "bisa");
                return true;
            }
        } else {
            Toast.makeText(LoginActivity.this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
            Log.v("Internetnya", "ga bisa");
            return false;
        }
    }

    public class GetDataWebService extends AsyncTask<String, Void, Void> {
        private ProgressDialog pDialog;
        Context mContext;


        public GetDataWebService(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Loging in...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON

            String jsonUrl = "http://serviceantrian.hol.es/public/loginclient/" + inputUsername + "/" + inputPassword;

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

            Log.v("url json : ", jsonUrl);
            if (jsonStr != null) {
                try {
                    JSONArray arr = new JSONArray(jsonStr);
                    JSONObject obj = arr.getJSONObject(0);
                    session.createIdLogin(obj.getString("id"));
                    session.createNamaUser(obj.getString("nama"));
                    sucessLogin = true;

                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(mContext, "Tidak dapat menerima data", Toast.LENGTH_SHORT).show();
                    Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 1");
                }
            } else {
                Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 2");
                sucessLogin = false;
                pDialog.dismiss();

            }

//                Toast.makeText(mContext, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

//            Toast.makeText(mContext, "wewewew "+hasil, Toast.LENGTH_SHORT).show();
            if (sucessLogin) {
                session.createLoginSession(inputUsername);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                txtNotif.setText(R.string.wrong_notif);
                txtNotif.setVisibility(View.VISIBLE);
            }
        }


    }


}
