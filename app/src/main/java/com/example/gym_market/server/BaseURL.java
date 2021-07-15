package com.example.gym_market.server;

public class BaseURL {

    public static String baseUrl = "http://192.168.18.5:5050/";

    public static String login = baseUrl + "users/signin";
    public static String register = baseUrl + "users/signup";

    public static String add_store = baseUrl + "barangs/addbarang";
    public static String get_store = baseUrl + "barangs/getall";
    public static String get_store_by_id = baseUrl + "barangs/getbyid/";

}
