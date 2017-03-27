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

/**
 * Created by Hafizh on 24/11/2016.
 */

public class PilihJadwalAdapter extends ArrayAdapter<Jadwal> {

    private Context context;
    private ArrayList<Jadwal> jd = new ArrayList<>();

    public PilihJadwalAdapter(Context context, ArrayList<Jadwal> jadwalArrayAdapter) {
        super(context, R.layout.item_lv_pilih_jadwal,jadwalArrayAdapter);
        this.context = context;
        jd = jadwalArrayAdapter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View rowView= inflater.inflate(R.layout.item_lv_pilih_jadwal, null, true);

        //Declarasi komponen
        TextView txtDokter = (TextView) rowView.findViewById(R.id.nama_dokter);
//        TextView txtPoli = (TextView) rowView.findViewById(R.id.jenis_poli);

        //Set Parameter Value
        txtDokter.setText(jd.get(position).getNama_dokter());

        return rowView;
    }


}
