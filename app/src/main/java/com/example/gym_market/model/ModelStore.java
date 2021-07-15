package com.example.gym_market.model;

public class ModelStore {

    String _id, namaBarang, hargaBrang, stokBarang, deskripsiBarang, fotoBarang;

    public ModelStore(){

    }

    public ModelStore(String _id, String namaBarang, String hargaBrang, String stokBarang, String deskripsiBarang, String fotoBarang){
        this._id = _id;
        this.namaBarang = namaBarang;
        this.hargaBrang = hargaBrang;
        this.stokBarang = stokBarang;
        this.deskripsiBarang = deskripsiBarang;
        this.fotoBarang = fotoBarang;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getStokBarang() {
        return stokBarang;
    }

    public void setStokBarang(String stokBarang) {
        this.stokBarang = stokBarang;
    }

    public String getDeskripsiBarang() {
        return deskripsiBarang;
    }

    public void setDeskripsiBarang(String deskripsiBarang) {
        this.deskripsiBarang = deskripsiBarang;
    }

    public String getFotoBarang() {
        return fotoBarang;
    }

    public void setFotoBarang(String fotoBarang) {
        this.fotoBarang = fotoBarang;
    }
}
