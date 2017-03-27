package hafizzaturrahim.com.poliklinikubantrianonline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hafizzaturrahim.com.poliklinikubantrianonline.R;
import hafizzaturrahim.com.poliklinikubantrianonline.model.Jadwal;
import hafizzaturrahim.com.poliklinikubantrianonline.model.RawatJalan;

/**
 * Created by Hafizh on 24/11/2016.
 */

public class HistoryAdapter extends ArrayAdapter<RawatJalan> {

    private Context context;
    private ArrayList<RawatJalan> jd = new ArrayList<>();

    public HistoryAdapter(Context context, ArrayList<RawatJalan> jadwalArrayAdapter) {
        super(context, R.layout.item_lv_history,jadwalArrayAdapter);
        this.context = context;
        jd = jadwalArrayAdapter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View rowView= inflater.inflate(R.layout.item_lv_history, null, true);

        //Declarasi komponen
        TextView txtKode = (TextView) rowView.findViewById(R.id.hs_kode_antrian);
        TextView txtDokter = (TextView) rowView.findViewById(R.id.hs_nama_dokter);
        TextView txtPoli = (TextView) rowView.findViewById(R.id.hs_poli);
        TextView txtTanggal = (TextView) rowView.findViewById(R.id.hs_tanggal);

        //Set Parameter Value
        txtKode.setText(jd.get(position).getKode());
        txtDokter.setText(jd.get(position).getNama_dokter());
        txtPoli.setText(jd.get(position).getJenis_poli());
        txtTanggal.setText(jd.get(position).getTanggal());

        return rowView;
    }
}
