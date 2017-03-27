package hafizzaturrahim.com.poliklinikubantrianonline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.model.Jadwal;

/**
 * Created by Hafizh on 24/11/2016.
 */

public class JadwalAdapter extends ArrayAdapter<Jadwal> {

    private Context context;
    private ArrayList<Jadwal> jd = new ArrayList<>();

    public JadwalAdapter(Context context, ArrayList<Jadwal> jadwalArrayAdapter) {
        super(context, R.layout.item_lv_jadwal, jadwalArrayAdapter);
        this.context = context;
        jd = jadwalArrayAdapter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View rowView = inflater.inflate(R.layout.item_lv_jadwal, null, true);

        //Declarasi komponen
        TextView txtDokter = (TextView) rowView.findViewById(R.id.nama_dokter);
        TextView txtPoli = (TextView) rowView.findViewById(R.id.jenis_poli);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.status);
        TextView txtJadwal = (TextView) rowView.findViewById(R.id.detail_jadwal);
//        TextView txtPoli = (TextView) rowView.findViewById(R.id.jenis_poli);

        //Set Parameter Value
        txtDokter.setText(jd.get(position).getNama_dokter());
        txtPoli.setText(jd.get(position).getJenis_poli());
        txtJadwal.setText(jd.get(position).getJadwal_dokter());
        String stats = jd.get(position).getStatus();
        if (stats.equals("1")) {
            txtStatus.setText("Hadir");
            txtStatus.setTextColor(Color.parseColor("#ff669900"));
        }else{
            txtStatus.setText("Tidak Hadir");
            txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return rowView;
    }
}
