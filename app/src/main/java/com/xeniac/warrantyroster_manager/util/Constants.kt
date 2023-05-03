package com.xeniac.warrantyroster_manager.util

object Constants {
    // Web URLs
    const val URL_PRIVACY_POLICY = "https://xeniacdev.github.io/WarrantyRoster/privacy_policy"
    const val URL_DONATE = "https://xeniacdev.github.io/donate"
    const val URL_CROWDIN = "https://crowdin.com/project/warranty-roster"

    // DataStore Constants
    const val DATASTORE_NAME_SETTINGS = "settings"
    const val DATASTORE_IS_LOGGED_IN_KEY = "isUserLoggedIn"
    const val DATASTORE_APP_THEME_INDEX_KEY = "theme"
    const val DATASTORE_RATE_APP_DIALOG_CHOICE_KEY = "rateAppDialogChoice"
    const val DATASTORE_PREVIOUS_REQUEST_TIME_IN_MILLIS_KEY = "previousRequestTimeInMillis"

    // App Theme Constants
    const val THEME_INDEX_DEFAULT = 0
    const val THEME_INDEX_LIGHT = 1
    const val THEME_INDEX_DARK = 2

    // App Locale Constants
    const val LANGUAGE_DEFAULT_OR_EMPTY = "en"
    const val LOCALE_TAG_ENGLISH_UNITED_STATES = "en-US"
    const val LOCALE_TAG_ENGLISH_GREAT_BRITAIN = "en-GB"
    const val LOCALE_TAG_PERSIAN_IRAN = "fa-IR"
    const val LOCALE_INDEX_DEFAULT_OR_EMPTY = -1
    const val LOCALE_INDEX_ENGLISH_UNITED_STATES = 0
    const val LOCALE_INDEX_ENGLISH_GREAT_BRITAIN = 1
    const val LOCALE_INDEX_PERSIAN_IRAN = 2

    // OnBoarding ViewPager Items Index
    const val ONBOARDING_1ST_INDEX = 0
    const val ONBOARDING_2ND_INDEX = 1
    const val ONBOARDING_3RD_INDEX = 2
    const val ONBOARDING_4TH_INDEX = 3

    // Forgot PW Timer Constants
    const val TIMER_START_TIME_IN_MILLIS = 120 * 1000L // 120 Seconds
    const val TIMER_COUNTDOWN_INTERVAL_IN_MILLIS = 1000L // 1 Second

    // Google Play In-App Reviews API Constants
    const val IN_APP_REVIEWS_DAYS_FROM_FIRST_INSTALL_TIME = 10
    const val IN_APP_REVIEWS_DAYS_FROM_PREVIOUS_REQUEST_TIME = 5

    // Warranty Adapter View Type Constants
    const val VIEW_TYPE_WARRANTY = 0
    const val VIEW_TYPE_AD = 1

    // Add Warranty Activity Calendars Fragment Tag Constants
    const val FRAGMENT_TAG_ADD_CALENDAR_STARTING = "fragment_tag_add_calendar_starting"
    const val FRAGMENT_TAG_ADD_CALENDAR_EXPIRY = "fragment_tag_add_calendar_expiry"

    // Edit Warranty Activity Calendars Fragment Tag Constants
    const val FRAGMENT_TAG_EDIT_CALENDAR_STARTING = "fragment_tag_edit_calendar_starting"
    const val FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY = "fragment_tag_edit_calendar_expiry"

    // Firestore Collections Constants
    const val FIRESTORE_CATEGORIES_COLLECTION_SIZE = 22
    const val COLLECTION_ID_CATEGORIES = "categories"
    const val COLLECTION_ID_WARRANTIES = "warranties"

    // Firestore Categories Collection Fields Constants
    const val FIRESTORE_CATEGORIES_TITLE = "title"
    const val FIRESTORE_CATEGORIES_ICON = "icon"

    // Firestore Warranties Collection Fields Constants
    const val WARRANTIES_TITLE = "title"
    const val WARRANTIES_BRAND = "brand"
    const val WARRANTIES_MODEL = "model"
    const val WARRANTIES_SERIAL_NUMBER = "serialNumber"
    const val WARRANTIES_LIFETIME = "lifetime"
    const val WARRANTIES_STARTING_DATE = "startingDate"
    const val WARRANTIES_EXPIRY_DATE = "expiryDate"
    const val WARRANTIES_DESCRIPTION = "description"
    const val WARRANTIES_CATEGORY_ID = "categoryId"
    const val WARRANTIES_UUID = "uuid"

    // Firebase Auth Provider Ids
    const val FIREBASE_AUTH_PROVIDER_ID_GOOGLE = "google.com"
    const val FIREBASE_AUTH_PROVIDER_ID_TWITTER = "twitter.com"
    const val FIREBASE_AUTH_PROVIDER_ID_FACEBOOK = "facebook.com"

    // Account Input Errors
    const val ERROR_INPUT_BLANK_EMAIL = "Email is blank"
    const val ERROR_INPUT_BLANK_PASSWORD = "Password is blank"
    const val ERROR_INPUT_BLANK_NEW_PASSWORD = "New Password is blank"
    const val ERROR_INPUT_BLANK_RETYPE_PASSWORD = "Retype Password is blank"
    const val ERROR_INPUT_EMAIL_INVALID = "Invalid email"
    const val ERROR_INPUT_EMAIL_SAME = "New Email is the same as current email"
    const val ERROR_INPUT_PASSWORD_SHORT = "Password is too short"
    const val ERROR_INPUT_PASSWORD_NOT_MATCH = "Password and Retype Password do not match"

    // Warranty Input Errors
    const val ERROR_INPUT_BLANK_TITLE = "Title is Blank"
    const val ERROR_INPUT_BLANK_STARTING_DATE = "Starting Date is Blank"
    const val ERROR_INPUT_BLANK_EXPIRY_DATE = "Expiry Date is Blank"
    const val ERROR_INPUT_INVALID_STARTING_DATE = "Invalid Starting Date"

    // Response Errors
    const val ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE =
        "Auth.GOOGLE_SIGN_IN_API is not available on this device"
    const val ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED = "12501"
    const val ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE = "7"
    const val ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED = "The web operation was canceled by the user"
    const val ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION = "An internal error has occurred"
    const val ERROR_NETWORK_CONNECTION =
        "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
    const val ERROR_FIREBASE_403 = "403"
    const val ERROR_FIREBASE_DEVICE_BLOCKED =
        "We have blocked all requests from this device due to unusual activity"
    const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS =
        "The email address is already in use by another account"
    const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS =
        "An account already exists with the same email address but different sign-in credentials"
    const val ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND =
        "There is no user record corresponding to this identifier"
    const val ERROR_FIREBASE_AUTH_CREDENTIALS =
        "The password is invalid or the user does not have a password"
    const val ERROR_FIREBASE_AUTH_ALREADY_LINKED =
        "User has already been linked to the given provider"
    const val ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED =
        "An email address must be provided"
    const val ERROR_TIMER_IS_NOT_ZERO = "Timer is not zero"
    const val ERROR_EMPTY_CATEGORY_LIST = "Category list is empty"
    const val ERROR_EMPTY_WARRANTY_LIST = "Warranty list is empty"
    const val ERROR_EMPTY_SEARCH_RESULT_LIST = "Search result list is empty"

    // Login Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_LOGIN_EMAIL = "save_instance_login_email"
    const val SAVE_INSTANCE_LOGIN_PASSWORD = "save_instance_login_password"

    // Register Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_REGISTER_EMAIL = "save_instance_register_email"
    const val SAVE_INSTANCE_REGISTER_PASSWORD = "save_instance_register_password"
    const val SAVE_INSTANCE_REGISTER_CONFIRM_PASSWORD = "save_instance_register_confirm_password"

    // Forgot PW Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_FORGOT_PW_EMAIL = "save_instance_forgot_pw_email"

    // Change Email Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_CHANGE_EMAIL_PASSWORD = "save_instance_change_email_password"
    const val SAVE_INSTANCE_CHANGE_EMAIL_NEW_EMAIL = "save_instance_change_email_new_email"

    // Change Password Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT_PASSWORD =
        "save_instance_change_password_current_password"
    const val SAVE_INSTANCE_CHANGE_PASSWORD_NEW_PASSWORD =
        "save_instance_change_password_new_password"
    const val SAVE_INSTANCE_CHANGE_PASSWORD_CONFIRM_NEW_PASSWORD =
        "save_instance_change_password_confirm_password"

    // Add Warranty Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_ADD_WARRANTY_TITLE = "save_instance_add_warranty_title"
    const val SAVE_INSTANCE_ADD_WARRANTY_BRAND = "save_instance_add_warranty_brand"
    const val SAVE_INSTANCE_ADD_WARRANTY_MODEL = "save_instance_add_warranty_model"
    const val SAVE_INSTANCE_ADD_WARRANTY_SERIAL = "save_instance_add_warranty_serial"
    const val SAVE_INSTANCE_ADD_WARRANTY_DESCRIPTION = "save_instance_add_warranty_description"
    const val SAVE_INSTANCE_ADD_WARRANTY_CATEGORY_ID = "save_instance_add_warranty_category_id"
    const val SAVE_INSTANCE_ADD_WARRANTY_IS_LIFETIME = "save_instance_add_warranty_is_lifetime"
    const val SAVE_INSTANCE_ADD_WARRANTY_STARTING_DATE_IN_MILLIS =
        "save_instance_add_warranty_starting_date_in_millis"
    const val SAVE_INSTANCE_ADD_WARRANTY_EXPIRY_DATE_IN_MILLIS =
        "save_instance_add_warranty_expiry_date_in_millis"

    // Edit Warranty Fragment SaveInstanceState Keys
    const val SAVE_INSTANCE_EDIT_WARRANTY_TITLE = "save_instance_edit_warranty_title"
    const val SAVE_INSTANCE_EDIT_WARRANTY_BRAND = "save_instance_edit_warranty_brand"
    const val SAVE_INSTANCE_EDIT_WARRANTY_MODEL = "save_instance_edit_warranty_model"
    const val SAVE_INSTANCE_EDIT_WARRANTY_SERIAL = "save_instance_edit_warranty_serial"
    const val SAVE_INSTANCE_EDIT_WARRANTY_DESCRIPTION = "save_instance_edit_warranty_description"
    const val SAVE_INSTANCE_EDIT_WARRANTY_CATEGORY_ID = "save_instance_edit_warranty_category_id"
    const val SAVE_INSTANCE_EDIT_WARRANTY_IS_LIFETIME = "save_instance_edit_warranty_is_lifetime"
    const val SAVE_INSTANCE_EDIT_WARRANTY_STARTING_DATE_IN_MILLIS =
        "save_instance_edit_warranty_starting_date_in_millis"
    const val SAVE_INSTANCE_EDIT_WARRANTY_EXPIRY_DATE_IN_MILLIS =
        "save_instance_edit_warranty_expiry_date_in_millis"
}