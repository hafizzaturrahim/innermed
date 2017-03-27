package hafizzaturrahim.com.poliklinikubantrianonline.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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

import hafizzaturrahim.com.poliklinikubantrianonline.LoginActivity;
import hafizzaturrahim.com.poliklinikubantrianonline.MainActivity;
import hafizzaturrahim.com.poliklinikubantrianonline.PilihJadwalActivity;
import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.ServiceHandler;
import hafizzaturrahim.com.poliklinikubantrianonline.SessionManager;
import hafizzaturrahim.com.poliklinikubantrianonline.adapter.BuktiPeriksaAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;

/**
 * A simple {@link Fragment} subclass.
 */
public class AntriankuFragment extends Fragment {
    ArrayList<RawatJalan> rj = new ArrayList<>();
    SessionManager session;
    SwipeRefreshLayout mSwipeRefreshLayout;
    BuktiPeriksaAdapter adapter;
    Context context;
    ListView lv;
    TextView petunjuk;
    AlertDialog.Builder builder;
    boolean isRegistered = false;

    public AntriankuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_antrian, container, false);
        context = getActivity();
        builder = new AlertDialog.Builder(context);
        petunjuk = (TextView) v.findViewById(R.id.txtpetunjuk);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        lv = (ListView) v.findViewById(R.id.list_bukti_periksa);
        session = new SessionManager(context);

//        fab = (FloatingActionButton) v.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(getContext(), PilihJadwalActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                // Add new Flag to start new Activity
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getActivity().startActivity(intent);
//            }
//        });
//        RawatJalan rawat = new RawatJalan();
//        rawat.setKode("WKS124a");
//        rj.add(rawat);

        if (checkConnection()) {
            new GetDataWebService(context, true).execute();
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (checkConnection()) {
                            new GetDataWebService(context, false).execute();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        return v;
    }

    private void setupAdapter() {
        adapter = new BuktiPeriksaAdapter(context, rj);
        lv.setAdapter(adapter);
        if (adapter.isEmpty()) {
            lv.setVisibility(View.GONE);
            petunjuk.setVisibility(View.VISIBLE);
        }
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
        boolean isFirstTime;


        public GetDataWebService(Context mContext, boolean isFirstTime) {
            this.mContext = mContext;
            this.isFirstTime = isFirstTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(mContext);
            if (isFirstTime) {
                pDialog.setMessage("Mengambil Data...");
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON

            String jsonUrl = "http://serviceantrian.hol.es/public/get-antrian/" + session.getIdLogin();

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

            Log.v("url json : ", jsonUrl);
            if (!jsonStr.equals("[]")) {
                isRegistered = true;
                try {
                    rj.clear();
                    Log.v("url json : ", jsonStr);
                    JSONArray arr = new JSONArray(jsonStr);
                    JSONObject obj = arr.getJSONObject(0);

                    RawatJalan rawatJalan = new RawatJalan();
                    rawatJalan.setId_antrian(obj.getString("id"));
                    rawatJalan.setKode(obj.getString("code"));
                    rawatJalan.setNo_antrian(obj.getString("no_antrian"));

                    if (obj.getString("status").equals("0")) {
                        rawatJalan.setStatus("Belum terverifikasi");
                    } else if (obj.getString("status").equals("1")) {
                        rawatJalan.setStatus("Terverifikasi");
                    } else {
                        rawatJalan.setStatus("Selesai");
                    }

                    String newTanggal = changeDateFormat(obj.getString("created_at"));
                    rawatJalan.setTanggal(newTanggal);

                    JSONObject dokter = obj.getJSONObject("dokter");
                    rawatJalan.setNama_dokter(dokter.getString("nama"));

                    String id_poli = dokter.getString("id_poli");
                    if (id_poli.equals("1")) {
                        rawatJalan.setJenis_poli("Poli Umum");
                    } else if (id_poli.equals("2")) {
                        rawatJalan.setJenis_poli("Poli Gigi");
                    } else {
                        rawatJalan.setJenis_poli("Poli Kecantikan");
                    }
                    String id_dokter = dokter.getString("id");
                    String jsonAntrian = "http://serviceantrian.hol.es/public/get-no-antrian/" +id_dokter;
                    String jsonStr2 = sh.makeServiceCall(jsonAntrian, ServiceHandler.GET);

                    if(jsonStr2 != null){
                        Log.v("json antrian: ", jsonStr2);
                        if(jsonStr2.equals("[]")){
                            rawatJalan.setAntrian_sekarang("-");
                        }else{
                            JSONArray a = new JSONArray(jsonStr2);
                            JSONObject b = a.getJSONObject(0);
                            rawatJalan.setAntrian_sekarang(b.getString("no_antrian"));
                        }
                    }

                    rj.add(rawatJalan);


                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(mContext, "Tidak dapat menerima data", Toast.LENGTH_SHORT).show();
                    Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 1");
                }
            } else {
                Log.v("ServiceHandler", "Data kosong");
                isRegistered = false;
                pDialog.dismiss();
//                Toast.makeText(mContext, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            }

//                Toast.makeText(mContext, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!isRegistered) {
                petunjuk.setVisibility(View.VISIBLE);
            } else {
                petunjuk.setVisibility(View.GONE);
            }

            if (petunjuk.getVisibility() == View.VISIBLE) {
                lv.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.VISIBLE);
            }
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            Log.v("isRegistered : ", String.valueOf(isRegistered));
            setupAdapter();
//            Toast.makeText(mContext, "wewewew "+hasil, Toast.LENGTH_SHORT).show();
        }


    }

    public String changeDateFormat(String oldDate) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm");
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
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
