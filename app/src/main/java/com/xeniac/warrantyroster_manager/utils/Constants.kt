package com.xeniac.warrantyroster_manager.utils

object Constants {
    //Web URLs
    const val URL_PRIVACY_POLICY = "https://warranty-roster.herokuapp.com/privacy-policy"

    //ROOM Database Constants
    const val DB_FILE_NAME_NEW = "WarrantyRosterDB.db"
    const val DB_TABLE_NAME_CATEGORIES = "categories"

    //Tapsell Ads Constants
    const val TAPSELL_KEY =
        "phbftfiakptpjbkegafmqmmkdsjcjmkldcahhapfssfftdnbgpqeimkjiitfpcoingqkad"
    const val WARRANTIES_NATIVE_ZONE_ID = "61647159b2c8056d868b66c6"
    const val SETTINGS_NATIVE_ZONE_ID = "616b0d4cb2c8056d868b6a1a"
    const val DELETE_WARRANTY_Interstitial_ZONE_ID = "616476f7b2c8056d868b66cb"

    //SharedPreference Settings Constants
    const val PREFERENCE_SETTINGS = "preference_settings"
    const val PREFERENCE_THEME_KEY = "theme"
    const val PREFERENCE_LANGUAGE_KEY = "language"
    const val PREFERENCE_COUNTRY_KEY = "country"

    //SharedPreference Login Constants
    const val PREFERENCE_LOGIN = "preference_login"
    const val PREFERENCE_IS_LOGGED_IN_KEY = "is_logged_in"

    //SharedPreference Settings Constants
    const val PREFERENCE_DB_SEED = "preference_db_seed"
    const val PREFERENCE_EN_US_KEY = "en-US"

    //Warranty Adapter View Type Constants
    const val VIEW_TYPE_WARRANTY = 0
    const val VIEW_TYPE_AD = 1

    //Add Warranty Activity Calendars Fragment Tag Constants
    const val FRAGMENT_TAG_ADD_CALENDAR_STARTING = "fragment_tag_add_calendar_starting"
    const val FRAGMENT_TAG_ADD_CALENDAR_EXPIRY = "fragment_tag_add_calendar_expiry"

    //Edit Warranty Activity Calendars Fragment Tag Constants
    const val FRAGMENT_TAG_EDIT_CALENDAR_STARTING = "fragment_tag_edit_calendar_starting"
    const val FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY = "fragment_tag_edit_calendar_expiry"

    //Firestore Collections ID Constants
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_WARRANTIES = "warranties"

    //Firestore Categories Collection Fields Constants
    const val CATEGORIES_TITLE = "title"
    const val CATEGORIES_ICON = "icon"

    //Firestore Warranties Collection Fields Constants
    const val WARRANTIES_TITLE = "title"
    const val WARRANTIES_BRAND = "brand"
    const val WARRANTIES_MODEL = "model"
    const val WARRANTIES_SERIAL_NUMBER = "serialNumber"
    const val WARRANTIES_STARTING_DATE = "startingDate"
    const val WARRANTIES_EXPIRY_DATE = "expiryDate"
    const val WARRANTIES_DESCRIPTION = "description"
    const val WARRANTIES_CATEGORY_ID = "categoryId"
    const val WARRANTIES_UUID = "uuid"

    //Response Errors
    const val ERROR_NETWORK_CONNECTION = "Unable to connect to the internet"
    const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS =
        "The email address is already in use by another account"
    const val ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND =
        "There is no user record corresponding to this identifier"
    const val ERROR_FIREBASE_AUTH_CREDENTIALS =
        "The password is invalid or the user does not have a password"
    const val ERROR_EMPTY_WARRANTY_LIST = "Warranty list is empty"
}