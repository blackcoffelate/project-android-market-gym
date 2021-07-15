package com.example.gym_market.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class RupiahConverter {
    public static String convertRupiah(String val) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        String biaya = kursIndonesia.format(Long.valueOf(val));
        return biaya;
    }
}
