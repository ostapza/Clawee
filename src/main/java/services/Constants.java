package services;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


public class Constants {

    public static final String APPLICATION_NAME = "Clawee";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public static final String GMAIL_HOST_IMAP = "imap.gmail.com";
    public static final String GMAIL_HOST_POP = "pop.gmail.com";
    public static final String PROTOCOL_POP = "pop3s";
    public static final String PROTOCOL_IMAP = "imap";

    public static final double IMAGE_DIFFERENCE = 5;
    public static final int IMAGE_DIFFERENCE_WITH_EXCLUDE = 30;
    public static final int PIXEL_ACCURACY = 5;
    public static final int MAIL_TIMEOUT_MIN_LONG  = 60; //check every 20 seconds
    public static final int MAIL_TIMEOUT_MIN_SHORT  = 15; //check every 20 seconds
    public static final int PAGE_TIMEOUT = 30;



}
