package hafizzaturrahim.com.poliklinikubantrianonline.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.ServiceHandler;
import hafizzaturrahim.com.poliklinikubantrianonline.SessionManager;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    SessionManager session;
    String dataDiri[];
    Context context;
    private TextView[] data;


    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profil, container, false);
        context = getActivity();
        session = new SessionManager(context);
        TextView ubahPass = (TextView) v.findViewById(R.id.st_pass);

        data = new TextView[]{
                (TextView) v.findViewById(R.id.st_nama),
                (TextView) v.findViewById(R.id.st_id),
                (TextView) v.findViewById(R.id.st_ttl),
                (TextView) v.findViewById(R.id.st_kelamin),
                (TextView) v.findViewById(R.id.st_alamat),
                (TextView) v.findViewById(R.id.st_gol_darah),
                (TextView) v.findViewById(R.id.st_email)
        };

        if (checkConnection()) {
            new GetDataWebService(context).execute();
        }

        ubahPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_chage_password);
                dialog.setTitle("Ubah Password");
                dialog.show();

                Button yes = (Button) dialog.findViewById(R.id.btnSubmit);
                Button no = (Button) dialog.findViewById(R.id.btnCancel);
                final EditText oldPass = (EditText) dialog.findViewById(R.id.old_password);
                final EditText newPass = (EditText) dialog.findViewById(R.id.new_password);

                yes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String getOldPass = oldPass.getText().toString();
                        String newPassword = newPass.getText().toString();
                        if (getOldPass.equals(dataDiri[7])) {
                            if(!newPassword.equals("")){
                                if (checkConnection()) {
                                    new GetDataWebService(context, true, newPassword).execute();
                                }
                                Toast.makeText(getActivity(), "Password berhasil diganti", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        } else {

                            oldPass.setError("Password salah");
                        }

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
            }
        });
        return v;
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (!info.isConnected()) {
                Log.v("Internetnya", "ga bisa");
                Toast.makeText(getActivity(), "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
                return false;

            } else {
                return true;
            }
        } else {
            Toast.makeText(getActivity(), "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
            Log.v("Internetnya", "ga bisa");
            return false;
        }
    }

    public class GetDataWebService extends AsyncTask<String, Void, Void> {
        private ProgressDialog pDialog;
        Context mContext;
        boolean isGantiPassword;
        List parameter = new ArrayList();


        public GetDataWebService(Context mContext) {
            this.mContext = mContext;

        }

        public GetDataWebService(Context mContext, boolean isGantiPassword, String newPassword) {
            this.mContext = mContext;
            this.isGantiPassword = isGantiPassword;
            parameter.add(new BasicNameValuePair("id_pasien", session.getIdLogin()));
            parameter.add(new BasicNameValuePair("password", newPassword));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Mengambil Data...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON


            String jsonUrl = "http://serviceantrian.hol.es/public/get-pasien/" + session.getIdLogin();

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            if (isGantiPassword) {
                String GantiPassUrl = "http://serviceantrian.hol.es/public/get-pasien/" + session.getIdLogin();
                String jsonStrgPass = sh.makeServiceCall(GantiPassUrl, ServiceHandler.POST, parameter);
                Log.v("json ganti pass : ", jsonStrgPass);
            }

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

            Log.v("url json : ", jsonUrl);
            if (jsonStr != null) {
                try {
                    Log.v("hasil json : ", jsonStr);
                    JSONObject obj = new JSONObject(jsonStr);

                    dataDiri = new String[9];
                    dataDiri[0] = obj.getString("nama");
                    dataDiri[1] = obj.getString("no_identitas");

                    String tanggal = changeDateFormat(obj.getString("tanggal_lahir"));
                    dataDiri[2] = obj.getString("tempat_lahir") + ", " + tanggal;

                    if (obj.getString("jenis_kelamin").equals("1")) {
                        dataDiri[3] = "Laki-laki";
                    } else {
                        dataDiri[3] = "Perempuan";
                    }

                    dataDiri[4] = obj.getString("alamat");
                    dataDiri[5] = obj.getString("gol_darah");
                    dataDiri[6] = obj.getString("email");
                    dataDiri[7] = obj.getString("password");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 1");
                }
            } else {
                Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 2");
                pDialog.dismiss();
//                Toast.makeText(mContext, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            }

//                Toast.makeText(mContext, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            for (int i = 0; i < data.length; i++) {
                data[i].setText(dataDiri[i]);
            }


            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

//            Toast.makeText(mContext, "wewewew "+hasil, Toast.LENGTH_SHORT).show();
        }


    }

    public String changeDateFormat(String oldDate){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

}
