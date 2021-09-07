package com.example.gym_market.server;

public class BaseURL{

//    public static String baseUrl = "http://192.168.18.5:5050/";
    public static String baseUrl = "http://studio81.co.id:5055/";

    public static String login = baseUrl + "users/signin";
    public static String register = baseUrl + "users/signup";

    public static String add_store = baseUrl + "barangs/addbarang";
    public static String get_store = baseUrl + "barangs/getall";
    public static String get_store_by_id = baseUrl + "barangs/getbyid/";
    public static String delete_store_by_id = baseUrl + "barangs/deletebyid/";
    public static String update_store_by_id = baseUrl + "barangs/update/";

    public static String add_cart = baseUrl + "transaksi/addkeranjang";
    public static String get_cart = baseUrl + "transaksi/getkeranjang/";
    public static String delete_cart = baseUrl + "transaksi/keranjang/";

    public static String get_pesanan = baseUrl + "transaksi/gettransaksi/";

    public static String transfer = baseUrl + "transaksi/pembayaran/";
    public static String log = baseUrl + "transaksi/getalltransaksi/";

    public static String checkout = baseUrl + "transaksi/checkout";

    public static String get_users = baseUrl + "users/getusers/";

}
