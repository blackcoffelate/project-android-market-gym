package com.example.gym_market.model;

public class ModelKeranjang {

    String idBarang, namaBarang, hargaBrang, qty, deskripsiBarang;
    private boolean isSelected;

    public ModelKeranjang(){

    }

    public ModelKeranjang(String idBarang, String namaBarang, String hargaBrang, String qty, String deskripsiBarang, boolean isSelected){
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBrang = hargaBrang;
        this.qty = qty;
        this.deskripsiBarang = deskripsiBarang;
        this.isSelected = isSelected;
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
        return hargaBrang;
    }

    public void setHargaBrang(String hargaBrang) {
        this.hargaBrang = hargaBrang;
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
