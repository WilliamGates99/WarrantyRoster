package com.xeniac.warrantyrostermanager;

public class Constants {

    //TODO remove URL_GRAPHQL
    public static final String URL_GRAPHQL = "https://warranty-roster.herokuapp.com/graphql";
    public static final String URL_PRIVACY_POLICY = "https://warranty-roster.herokuapp.com/privacy-policy";

    //ROOM Database Constants
    public static final String DB_FILE_NAME = "warranty-roster";
    public static final int DB_VERSION = 1;

    //Tapsell Ads Constants
    public static final String TAPSELL_KEY = "phbftfiakptpjbkegafmqmmkdsjcjmkldcahhapfssfftdnbgpqeimkjiitfpcoingqkad";
    public static final String WARRANTIES_NATIVE_ZONE_ID = "61647159b2c8056d868b66c6";
    public static final String SETTINGS_NATIVE_ZONE_ID = "616b0d4cb2c8056d868b6a1a";
    public static final String DELETE_WARRANTY_Interstitial_ZONE_ID = "6165dea5b2c8056d868b68a9";

    //SharedPreference Settings Constants
    public static final String PREFERENCE_SETTINGS = "preference_settings";
    public static final String PREFERENCE_THEME_KEY = "theme";
    public static final String PREFERENCE_LANGUAGE_KEY = "language";
    public static final String PREFERENCE_COUNTRY_KEY = "country";

    //SharedPreference Login Constants
    public static final String PREFERENCE_LOGIN = "preference_login";
    public static final String PREFERENCE_IS_LOGGED_IN_KEY = "is_logged_in";
    //TODO remove PREFERENCE_USER_TOKEN_KEY
    public static final String PREFERENCE_USER_TOKEN_KEY = "user_token";

    //Add Warranty Activity Calendars Fragment Tag Constants
    public static final String FRAGMENT_TAG_ADD_CALENDAR_STARTING = "fragment_tag_add_calendar_starting";
    public static final String FRAGMENT_TAG_ADD_CALENDAR_EXPIRY = "fragment_tag_add_calendar_expiry";

    //Firestore Collections ID Constants
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_CATEGORIES = "categories";
    public static final String COLLECTION_WARRANTIES = "warranties";

    //Firestore Warranties Collection Fields Constants
    public static final String WARRANTIES_UUID = "uuid";
    public static final String WARRANTIES_TITLE = "title";
    public static final String WARRANTIES_BRAND = "brand";
    public static final String WARRANTIES_MODEL = "model";
    public static final String WARRANTIES_SERIAL_NUMBER = "serialNumber";
    public static final String WARRANTIES_STARTING_DATE = "startingDate";
    public static final String WARRANTIES_EXPIRY_DATE = "expiryDate";
    public static final String WARRANTIES_DESCRIPTION = "description";
    public static final String WARRANTIES_CATEGORY_ID = "categoryId";
}