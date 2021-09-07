package com.example.gym_market.model;

public class ModelDaftarPesanan {

    String idTransaksi, tanggalTransaksi, status, totalBelanja;

    public ModelDaftarPesanan(){

    }

    public ModelDaftarPesanan(String idTransaksi, String tanggalTransaksi, String status, String totalBelanja){
        this.idTransaksi = idTransaksi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.status = status;
        this.totalBelanja = totalBelanja;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalBelanja() {
        return totalBelanja;
    }

    public void setTotalBelanja(String totalBelanja) {
        this.totalBelanja = totalBelanja;
    }
}
