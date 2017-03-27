package hafizzaturrahim.com.poliklinikubantrianonline.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.ServiceHandler;
import hafizzaturrahim.com.poliklinikubantrianonline.SessionManager;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.AntriankuFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;

/**
 * Created by Hafizh on 24/11/2016.
 */

public class BuktiPeriksaAdapter extends ArrayAdapter<RawatJalan> {

    private Context context;
     ArrayList<RawatJalan> jd = new ArrayList<>();
    private AntriankuFragment antriankuFragment;
    AlertDialog.Builder builder;
    SessionManager session;
    String id_antrian;

    public BuktiPeriksaAdapter(Context context, ArrayList<RawatJalan> jadwalArrayAdapter) {
        super(context, R.layout.item_bukti_periksa, jadwalArrayAdapter);
        this.context = context;
        jd = jadwalArrayAdapter;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder = new AlertDialog.Builder(context);

        //Load Custom Layout untuk list
        View rowView = inflater.inflate(R.layout.item_bukti_periksa, null, true);
        session = new SessionManager(context);
        //Declarasi komponen
        TextView txtKode = (TextView) rowView.findViewById(R.id.kode_antrian);
        TextView txtPoli = (TextView) rowView.findViewById(R.id.bp_jenis_poli);
        TextView txtDokter = (TextView) rowView.findViewById(R.id.bp_nama_dokter);
        TextView txtNoAntrian = (TextView) rowView.findViewById(R.id.bp_no_antrian);
        TextView txtAntrianSkrg = (TextView) rowView.findViewById(R.id.bp_antrian_sekarang);
        TextView txtTanggal = (TextView) rowView.findViewById(R.id.bp_tanggal);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.bp_status);
//        TextView txtKet = (TextView) rowView.findViewById(R.id.bp_keterangan);
        Button batal = (Button) rowView.findViewById(R.id.btnBatal);

        txtStatus.setText(jd.get(position).getStatus());
        if (txtStatus.getText().toString().equals("Terverifikasi")) {
            txtStatus.setTextColor(Color.parseColor("#ff0099cc"));
        } else if (txtStatus.getText().toString().equals("Selesai")) {
            txtStatus.setTextColor(Color.parseColor("#ff669900"));
        }

        //Set Parameter Value
        id_antrian = jd.get(position).getId_antrian();
        txtKode.setText(jd.get(position).getKode());
        txtPoli.setText(jd.get(position).getJenis_poli());
        txtDokter.setText(jd.get(position).getNama_dokter());
        txtNoAntrian.setText(jd.get(position).getNo_antrian());
        txtTanggal.setText(jd.get(position).getTanggal());
        txtAntrianSkrg.setText(jd.get(position).getAntrian_sekarang());

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Konfirmasi Pembatalan");
                builder.setMessage("Apakah anda melakukan pembatalan antrian rawat jalan?");
                builder.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                new DeleteAntrian(context).execute();
                                jd.clear();
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

            }
        });


        return rowView;
    }


    public class DeleteAntrian extends AsyncTask<String, Void, Void> {
        private ProgressDialog pDialog;
        Context mContext;

        public DeleteAntrian(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Memproses Data...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // URL to get contacts JSON

            String jsonUrl = "http://serviceantrian.hol.es/public/cancelAntrian/" + id_antrian;

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);
            Log.v("url hapus antrian : ", jsonUrl);
            Log.v("json hapus antrian : ", jsonStr);
            if (jsonStr != null) {
//                session.createRawatJalan(false);
            }else{

            }

//

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
        }


    }
}
