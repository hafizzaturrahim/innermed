package hafizzaturrahim.com.poliklinikubantrianonline.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.ServiceHandler;
import hafizzaturrahim.com.poliklinikubantrianonline.SessionManager;
import hafizzaturrahim.com.poliklinikubantrianonline.adapter.BuktiPeriksaAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.adapter.HistoryAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    ArrayList<RawatJalan> rj = new ArrayList<>();
    ListView lv;
    Context context;
    SessionManager session;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        context = getActivity();
        session = new SessionManager(context);
//        RawatJalan rawat = new RawatJalan();
//        rawat.setKode("WKS124a");
//        rj.add(rawat);

        lv = (ListView) v.findViewById(R.id.list_history);
        if (checkConnection()) {
            new GetDataWebService(getActivity()).execute();
        }

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

        public GetDataWebService(Context mContext) {
            this.mContext = mContext;
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

            String jsonUrl = "http://serviceantrian.hol.es/public/getHistory/" + session.getIdLogin();

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

            Log.v("url json : ", jsonUrl);
            if (!jsonStr.equals("[]")) {
                try {
                    rj.clear();

                    JSONArray item = new JSONArray(jsonStr);
                    for (int i = 0; i < item.length(); i++) {
                        JSONObject a = item.getJSONObject(i);
                        RawatJalan rawatJalan = new RawatJalan();

                        rawatJalan.setKode(a.getString("code"));
                        rawatJalan.setTanggal(changeDateFormat(a.getString("created_at")));

                        JSONObject b = a.getJSONObject("dokter");
                        rawatJalan.setNama_dokter(b.getString("nama"));

                        String id_poli = b.getString("id_poli");
                        if (id_poli.equals("1")) {
                            rawatJalan.setJenis_poli("Poli Umum");
                        }else if(id_poli.equals("2")){
                            rawatJalan.setJenis_poli("Poli Gigi");
                        }else{
                            rawatJalan.setJenis_poli("Poli Kecantikan");
                        }
                        rj.add(rawatJalan);
                    }


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
            HistoryAdapter adapter = new HistoryAdapter(getActivity(), rj);
            lv.setAdapter(adapter);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }


//            Toast.makeText(mContext, "wewewew "+hasil, Toast.LENGTH_SHORT).show();
        }


    }

    public String changeDateFormat(String oldDate) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm");
        Date date = null;
        try {
            date = inputFormat.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

}
