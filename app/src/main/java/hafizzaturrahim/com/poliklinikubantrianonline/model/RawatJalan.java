package hafizzaturrahim.com.poliklinikubantrianonline.model;

/**
 * Created by Hafizh on 25/11/2016.
 */

public class RawatJalan {
    private String kode;
    private String nama_dokter;
    private String jenis_poli;
    private String no_antrian;
    private String antrian_sekarang;
    private String status;
    private String tanggal;
    private String keterangan;
    private String id_antrian;

    public RawatJalan() {
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
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

    public String getNo_antrian() {
        return no_antrian;
    }

    public void setNo_antrian(String no_antrian) {
        this.no_antrian = no_antrian;
    }

    public String getAntrian_sekarang() {
        return antrian_sekarang;
    }

    public void setAntrian_sekarang(String antrian_sekarang) {
        this.antrian_sekarang = antrian_sekarang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getId_antrian() {
        return id_antrian;
    }

    public void setId_antrian(String id_antrian) {
        this.id_antrian = id_antrian;
    }
}
