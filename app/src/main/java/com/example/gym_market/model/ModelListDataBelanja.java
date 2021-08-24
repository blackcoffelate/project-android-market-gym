package com.example.gym_market.model;

public class ModelListDataBelanja {

    String trx_id, idPembeli, idBarang, namaBarang, hargaBarang, qtyBarang, deskripsiBarang, totalHarga;

    public ModelListDataBelanja(){

    }

    public ModelListDataBelanja(String trx_id, String idPembeli, String idBarang, String namaBarang, String hargaBarang, String qtyBarang, String deskripsiBarang, String totalHarga){
        this.trx_id = trx_id;
        this.idPembeli = idPembeli;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.qtyBarang = qtyBarang;
        this.deskripsiBarang = deskripsiBarang;
        this.totalHarga = totalHarga;
    }

    public String getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(String trx_id) {
        this.trx_id = trx_id;
    }

    public String getIdPembeli() {
        return idPembeli;
    }

    public void setIdPembeli(String idPembeli) {
        this.idPembeli = idPembeli;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(String hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    public String getQtyBarang() {
        return qtyBarang;
    }

    public void setQtyBarang(String qtyBarang) {
        this.qtyBarang = qtyBarang;
    }

    public String getDeskripsiBarang() {
        return deskripsiBarang;
    }

    public void setDeskripsiBarang(String deskripsiBarang) {
        this.deskripsiBarang = deskripsiBarang;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }
}
