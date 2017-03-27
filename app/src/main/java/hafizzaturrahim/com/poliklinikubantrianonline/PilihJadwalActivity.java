package hafizzaturrahim.com.poliklinikubantrianonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import hafizzaturrahim.com.poliklinikubantrianonline.adapter.PilihJadwalAdapter;
import hafizzaturrahim.com.poliklinikubantrianonline.model.Jadwal;

/**
 * Created by Hafizh on 25/11/2016.
 */

public class PilihJadwalActivity extends AppCompatActivity {

    ArrayList<Jadwal> jadwal = new ArrayList<>();
    Jadwal jadwal1 = new Jadwal();
    AlertDialog.Builder builder;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_jadwal);
        session = new SessionManager(getApplicationContext());
        jadwal1.setNama_dokter("Pak Bagimin Sepertigajah");
        jadwal.add(jadwal1);

        ListView lv = (ListView) findViewById(R.id.list_pilih_jadwal);
        builder = new AlertDialog.Builder(this);
        PilihJadwalAdapter adapter = new PilihJadwalAdapter(PilihJadwalActivity.this,jadwal);
        lv.setAdapter(adapter);
        getSupportActionBar().setTitle("Pilih Jadwal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                builder.setTitle("Konfirmasi Periksa Jalan");
                builder.setMessage("Apakah anda akan melakukan registrasi rawat jalan dengan dokter " +jadwal.get(i).getNama_dokter()+" ?");
                builder.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(PilihJadwalActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                session.createRawatJalan(true);
                                startActivity(intent);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
