package hafizzaturrahim.com.poliklinikubantrianonline.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hafizzaturrahim.com.poliklinikubantrianonline.MainActivity;
import hafizzaturrahim.com.poliklinikubantrianonline.PilihJadwalActivity;
import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.ServiceHandler;
import hafizzaturrahim.com.poliklinikubantrianonline.SessionManager;
import hafizzaturrahim.com.poliklinikubantrianonline.adapter.JadwalAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.model.Jadwal;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;


public class BerandaFragment extends Fragment {

    Context context;
    ArrayList<Jadwal> jadwal = new ArrayList<>();
    ListView lv;
    SessionManager session;
    AlertDialog.Builder builder;
    AlertDialog.Builder absen;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ActionBar actionBar;
    boolean isRegistered = false;


    public BerandaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_beranda, container, false);
        context = getActivity();
        builder = new AlertDialog.Builder(context);
        absen = new AlertDialog.Builder(context);
        session = new SessionManager(context);
        actionBar = getActivity().getActionBar();

        //contoh
//        jadwal1.setNama_dokter("Bakwan");
//        jadwal.add(jadwal1);
//        jadwal.add(jadwal1);

        lv = (ListView) v.findViewById(R.id.list_jadwal);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_jadwal);
        GetDataWebService getDataWebService = new GetDataWebService(context, checkConnection(), true);
        getDataWebService.execute();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataWebService getData = new GetDataWebService(context, checkConnection(), false);
                        getData.execute();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
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
        boolean statusConnection;
        boolean isFirstTime;
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        public GetDataWebService(Context mContext, boolean statusConnection, boolean isFirstTime) {
            this.mContext = mContext;
            this.statusConnection = statusConnection;
            this.isFirstTime = isFirstTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(mContext);
            if (isFirstTime) {
                pDialog.setMessage("Mengambil data...");
//            pDialog.setCancelable(true);
                pDialog.show();
            }


        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON

            if (statusConnection) {

                String jsonUrl = "http://serviceantrian.hol.es/public/get-dokter";
                String jsonUrl2 = "http://serviceantrian.hol.es/public/get-antrian/" + session.getIdLogin();

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);
                String jsonStr2 = sh.makeServiceCall(jsonUrl2, ServiceHandler.GET);
                Log.v("url json cek reg: ", jsonUrl);


                if (!jsonStr2.equals("[]")) {
//                    session.createRawatJalan(false);
                    isRegistered = true;
                }

                Log.v("cek sudah antri? : ", session.getSessionRawatJalan().toString());
                Log.v("hasil json 2 : ", jsonStr2);
                if (jsonStr != null) {
                    try {
                        Log.v("hasil json : ", jsonStr);
                        jadwal.clear();
                        JSONArray jsonArray = new JSONArray(jsonStr);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Jadwal jd = new Jadwal();
                            jd.setNama_dokter(obj.getString("nama"));
                            jd.setId_dokter(obj.getString("id"));
                            jd.setStatus(obj.getString("status"));
                            JSONObject poli = obj.getJSONObject("poli");
                            jd.setJenis_poli("Jenis Poli : " + poli.getString("jenis_poli"));

//                            String listJAdwal;
//                            JSONArray jadwalDokter = obj.getJSONArray("jadwal_dokter");
//                            for(int j =0; j < jadwalDokter.length();j++){
//                                JSONObject jdItem = jadwalDokter.getJSONObject(j);
//
//                            }

                            //untuk mendapatkan jadwal
                            String detailjsonUrl = "http://serviceantrian.hol.es/public/get-jadwalDokter/" + obj.getString("id");
                            jsonStr = sh.makeServiceCall(detailjsonUrl, ServiceHandler.GET);
                            String detailJadwal = "";
                            if (detailjsonUrl != null) {
                                JSONArray detailarr = new JSONArray(jsonStr);

                                for (int j = 0; j < detailarr.length(); j++) {
                                    JSONObject detailobj = detailarr.getJSONObject(j);
                                    JSONObject detailjd = detailobj.getJSONObject("jadwal");
                                    detailJadwal = detailJadwal + " " + detailjd.getString("Hari") + ", " + detailjd.getString("JamBuka") + "-" + detailjd.getString("JamTutup") + "\n";
                                }
                            }
                            jd.setJadwal_dokter(detailJadwal);
                            jadwal.add(jd);
                        }


//                        JSONArray resultArray = new JSONArray(jsonStr);
//                     looping through All Places
//                            for (int i = 0; i < resultArray.length(); i++) {
//                                Jadwal jd = new Jadwal();
//                                JSONObject a = resultArray.getJSONObject(i);
//
//                                jd.setNama_dokter(a.getString("StudentName"));
//                                jd.setJenis_poli(a.getString("StudentMarks"));
//
//                                jadwal.add(jd);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(getActivity(), "Tidak dapat menerima data", Toast.LENGTH_SHORT).show();
                        Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 1");
                    }
                } else {
                    Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 2");
//                    Toast.makeText(getActivity(), "Tidak dapat menerima data", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            } else {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog


            JadwalAdapter adapter = new JadwalAdapter(context, jadwal);
            lv.setAdapter(adapter);

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    if (isRegistered) {
                        builder.setMessage("Anda tidak dapat melakukan registrasi rawat jalan karena masih memiliki registrasi yang masih aktif. Untuk pengecekan, silahkan buka menu Antrianku");
                        builder.show();
                    } else {
                        if (jadwal.get(position).getStatus().equals("1")) {
                            builder.setTitle("Konfirmasi Periksa Jalan");
                            builder.setMessage("Apakah anda akan melakukan registrasi rawat jalan dengan dokter " + jadwal.get(position).getNama_dokter() + " ?");
                            builder.setPositiveButton("Ya",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new SendDataBooking(context, checkConnection(), jadwal.get(position).getId_dokter()).execute();

                                            // set the toolbar title

                                        }
                                    });

                            builder.setNegativeButton("Tidak",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                            builder.show();
                        } else {
                            absen.setTitle("Dokter sedang tidak hadir");
                            absen.setMessage("Anda tidak dapat mendaftar pada dokter yang tidak hadir");
                            absen.show();
                        }

                    }
                }
            });

        }

    }

    public class SendDataBooking extends AsyncTask<String, Void, Void> {
        private ProgressDialog pDialog;
        Context mContext;
        boolean statusConnection;
        boolean failLoad;
        String id_dokter;
        List parameter = new ArrayList();
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        public SendDataBooking(Context mContext, boolean statusConnection, String id_dokter) {
            this.mContext = mContext;
            this.statusConnection = statusConnection;
            this.id_dokter = id_dokter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            parameter.add(new BasicNameValuePair("id_dokter", id_dokter));
            parameter.add(new BasicNameValuePair("id_pasien", session.getIdLogin()));
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Mengirim data...");
//            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON

            if (statusConnection) {
                String jsonUrl = "http://serviceantrian.hol.es/public/create-antrian";

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Making a request to url and getting response
                String jsonStr;

                Log.v("url json : ", jsonUrl);

                try {
                    jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.POST, parameter);
                    Log.d("Response: jsonStr ", "> " + jsonStr);
                    failLoad = false;

                } catch (Exception e) {
                    e.printStackTrace();
//                        Toast.makeText(getActivity(), "Tidak dapat menerima data", Toast.LENGTH_SHORT).show();
                    Log.v("ServiceHandler", "Tidak bisa mendapat data dari URL yang diminta 1");
                    failLoad = true;
                }

            } else {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (!failLoad) {
                session.createRawatJalan(true);
                Fragment fragment = new AntriankuFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main, fragment);
                fragmentTransaction.commit();
            } else {
//                Toast.makeText(getActivity(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            }

        }


    }


}
