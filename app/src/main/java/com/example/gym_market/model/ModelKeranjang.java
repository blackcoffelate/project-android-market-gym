package com.example.gym_market.model;

public class ModelKeranjang {

    String idKeranjang, idBarang, namaBarang, hargaBarang, qty, stokBarang, deskripsiBarang;
    private boolean isSelected;

    public ModelKeranjang(){

    }

    public ModelKeranjang(String idKeranjang, String idBarang, String namaBarang, String hargaBarang, String stokBarang, String qty, String deskripsiBarang, boolean isSelected){
        this.idKeranjang = idKeranjang;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.qty = qty;
        this.stokBarang = stokBarang;
        this.deskripsiBarang = deskripsiBarang;
        this.isSelected = isSelected;
    }

    public String getStokBarang() {
        return stokBarang;
    }

    public void setStokBarang(String stokBarang) {
        this.stokBarang = stokBarang;
    }

    public String getIdKeranjang() {
        return idKeranjang;
    }

    public void setIdKeranjang(String idKeranjang) {
        this.idKeranjang = idKeranjang;
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

    public String getHargaBrang() {
        return hargaBarang;
    }

    public void setHargaBrang(String hargaBrang) {
        this.hargaBarang = hargaBrang;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDeskripsiBarang() {
        return deskripsiBarang;
    }

    public void setDeskripsiBarang(String deskripsiBarang) { this.deskripsiBarang = deskripsiBarang; }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
