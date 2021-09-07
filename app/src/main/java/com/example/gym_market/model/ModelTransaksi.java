package com.example.gym_market.model;

import java.util.List;

public class ModelTransaksi {

    String idKeranjang, totalBelanja, created_at;
    List<ModelPesanan> listPesanan;

    public ModelTransaksi(){

    }

    public ModelTransaksi(String idKeranjang, String totalBelanja, String created_at, List<ModelPesanan> listPesanan){
        this.idKeranjang = idKeranjang;
        this.totalBelanja = totalBelanja;
        this.created_at = created_at;
        this.listPesanan = listPesanan;
    }

    public String getIdKeranjang() {
        return idKeranjang;
    }

    public void setIdKeranjang(String idKeranjang) {
        this.idKeranjang = idKeranjang;
    }

    public String getTotalBelanja() {
        return totalBelanja;
    }

    public void setTotalBelanja(String totalBelanja) {
        this.totalBelanja = totalBelanja;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<ModelPesanan> getListPesanan() {
        return listPesanan;
    }

    public void setListPesanan(List<ModelPesanan> listPesanan) {
        this.listPesanan = listPesanan;
    }
}
