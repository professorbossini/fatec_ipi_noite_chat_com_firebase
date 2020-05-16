package br.com.bossini.fatec_ipi_noite_chat_com_firebase;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    public static String format (Date date){
        return sdf.format(date);
    }
}
