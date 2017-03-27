package hafizzaturrahim.com.poliklinikubantrianonline.model;

/**
 * Created by Hafizh on 24/11/2016.
 */

public class Jadwal {
    String nama_dokter;
    String jenis_poli;
    int jumlah_antrian;
    String jadwal_dokter;
    String status;
    String id_dokter;

    public Jadwal() {
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public void setNama_dokter(String nama_dokter) {
        this.nama_dokter = nama_dokter;
    }

    public String getJenis_poli() {
        return jenis_poli;
    }

    public void setJenis_poli(String jenis_poli) {
        this.jenis_poli = jenis_poli;
    }

    public int getJumlah_antrian() {
        return jumlah_antrian;
    }

    public void setJumlah_antrian(int jumlah_antrian) {
        this.jumlah_antrian = jumlah_antrian;
    }

    public String getJadwal_dokter() {
        return jadwal_dokter;
    }

    public void setJadwal_dokter(String jadwal_dokter) {
        this.jadwal_dokter = jadwal_dokter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId_dokter() {
        return id_dokter;
    }

    public void setId_dokter(String id_dokter) {
        this.id_dokter = id_dokter;
    }
}
