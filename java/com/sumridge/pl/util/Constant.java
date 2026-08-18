package com.sumridge.pl.util;

public interface Constant
{
    static String RATING_CACHE = "rating";
    static String CURRENCY_CACHE = "currency";
    static String POSITION_CACHE = "position";
    static String ACCOUNT_CACHE = "account";
    static String PRICE_CACHE = "price";
    static String RESULT_CACHE = "result";
    static String PROPERTY_CACHE = "property";
    static String COMMON_CACHE = "common";

    static String TAG = "|";
    static String LAST_UPDATE = "LAST_UPDATE";

    static String REPORT_CCY = "USD";

    public interface Bucket
    {

    }

    public enum Signal
    {
        Total, Position, Trade, Setup, Price, Currency, MSD
    }
}
