package com.gmwapp.dudeways.helper

object Constant {
    const val AppPlayStoreUrl: String =
        "https://play.google.com/store/apps/details?id=com.gmwapp.dudeways"
    const val MainBaseUrl: String = "https://dudeways.com/"
    const val PAYMENT_LINK: String = "https://gateway.graymatterworks.com/api/create_payment_request.php"


    const val BaseUrl: String = MainBaseUrl + "api/"


    const val REGISTER: String = BaseUrl + "register"
    const val PROFESSION_LIST: String = BaseUrl + "profession_list"
    const val VERIFICATION_LIST: String = BaseUrl + "verification_list"
    const val DELETE_ACCOUNT: String = BaseUrl + "delete_account"
    const val UPDATE_USERS: String = BaseUrl + "update_users"
    const val CHECK_MOBILE: String = BaseUrl + "check_mobile"
    const val CHECK_EMAIL: String = BaseUrl + "check_email"
    const val USERDETAILS: String = BaseUrl + "userdetails"
    const val OTHER_USER_DETAILS: String = BaseUrl + "other_userdetails"
    const val SETTINGS_LIST: String = BaseUrl + "settings_list"
    const val PRIVACY_POLICY: String = BaseUrl + "privacy_policy"
    const val REFUND_POLICY: String = BaseUrl + "refund_policy"
    const val TERMS_CONDITIONS: String = BaseUrl + "terms_conditions"

    const val UPDATE_IMAGE: String = BaseUrl + "update_image"
    const val UPDATE_COVER_IMG: String = BaseUrl + "update_cover_img"
    const val VERIFY_FRONT_IMAGE: String = BaseUrl + "verify_front_image"
    const val VERIFY_BACK_IMAGE: String = BaseUrl + "verify_back_image"
    const val VERIFY_SELFIE_IMAGE: String = BaseUrl + "verify_selfie_image"
    const val TRIP_LIST: String = BaseUrl + "trip_list"
    const val ACTIVE_USERS_LIST: String = BaseUrl + "active_users_list"
    const val USERS_LIST: String = BaseUrl + "users_list"
    const val UPDATE_LOCATION: String = BaseUrl + "update_location"
    const val ADD_FEEDBACK: String = BaseUrl + "add_feedback"
    const val ADD_CHAT: String = BaseUrl + "add_chat"
    const val DELETE_CHAT: String = BaseUrl + "delete_chat"
    const val POINTS_LIST: String = BaseUrl + "points_list"
    const val ADD_POINTS: String = BaseUrl + "add_points"
    const val SPIN_POINTS: String = BaseUrl + "spin_points"
    const val REWARD_POINTS: String = BaseUrl + "reward_points"
    const val TRIP_TYPE: String = "trip_type"
    const val TRIP_LOCATION: String = "location"
    const val PROFILE_IMAGE: String = "profile_image"
    const val TRIP_DETAILS: String = BaseUrl + "trip_details"
    const val TRIP_TITLE: String = "trip_title"
    const val TRIP_DESCRIPTION: String = "trip_description"
    const val TRIP_FROM_DATE: String = "from_date"
    const val TRIP_TO_DATE: String = "to_date"
    const val TRIP_IMAGE: String = "trip_image"
    const val MY_TRIP_LIST: String = BaseUrl + "my_trip_list"
    const val DELETE_TRIP: String = BaseUrl + "delete_trip"
    const val FREINDS_LIST: String = BaseUrl + "friends_list"
    const val CHAT_LIST: String = BaseUrl + "chat_list"
    const val NOTFICATION_LIST: String = BaseUrl + "notification_list"
    const val UPDATE_NOTIFY: String = BaseUrl + "update_notify"
    const val ADD_FRIENDS: String = BaseUrl + "add_friends"
    const val MSG_SEEN_URL: String = BaseUrl + "msg_seen"
    const val READ_CHATS: String = BaseUrl + "read_chats"
    const val UNREAD_ALL: String = BaseUrl + "unread_all"
    const val PROFILE_VIEW: String = BaseUrl + "profile_view"
    const val ADD_TRIP: String = BaseUrl + "add_trip"
    const val UPDATE_TRIP_IMAGE: String = BaseUrl + "update_trip_image"
    const val MYBOOKS: String = BaseUrl + "mybooks"
    const val CART_LIST: String = BaseUrl + "cartlist"
    const val ADD_TO_CART: String = BaseUrl + "booklist/add-cart"
    const val SEARCHBOOKS: String = BaseUrl + "booklist/searchbooks"
    const val APPUPDATE: String = BaseUrl + "appsettings_list"
    const val RECHARGE_CREATE: String = BaseUrl + "create_recharge"
    const val CREATE_VERIFICATION: String = BaseUrl + "create_verification"
    const val RECHARGE_STATUS: String = BaseUrl + "check_recharge_status"
    const val VERIFICATION_STATUS: String = BaseUrl + "verification_status"
    const val PLAN_LIST: String = BaseUrl + "plan_list"
    const val PAYMENT_IMAGE_API: String = BaseUrl + "payment_image"

    const val STATUS: String = "status"
    const val PAYMENT_STATUS: String = "payment_status"


    // const val LOAD_ITEM_LIMIT = 10
    const val LOAD_ITEM_LIMIT: Int = 10


    const val KEY: String = "key"
    const val CLIENT_TXN_ID: String = "client_txn_id"
    const val AMOUNT: String = "amount"
    const val P_INFO: String = "p_info"
    const val CUSTOMER_NAME: String = "customer_name"
    const val CUSTOMER_EMAIL: String = "customer_email"
    const val CUSTOMER_MOBILE: String = "customer_mobile"
    const val REDIRECT_URL: String = "redirect_url"
    const val UDF1: String = "udf1"
    const val UDF2: String = "udf2"
    const val UDF3: String = "udf3"
    const val TXN_ID: String = "txn_id"
    const val MARKET_ID: String = "market_id"
    const val DATE: String = "date"

    const val REMOVE_FROM_CART: String = BaseUrl + "booklist/delete-cart"
    const val ORDER: String = BaseUrl + "order"


    const val AUTHORIZATION: String = "Authorization"
    const val TOKEN: String = "token"
    const val LIMIT: String = "limit"
    const val TOTAL: String = "total"
    const val OFFSET: String = "offset"
    const val PROOF1: String = "proof1"
    const val PROOF2: String = "proof2"
    const val VERDICATION_STATUS: String = "0"
    const val FrontPROOF: String = "front_proof"
    const val BackPROOF: String = "back_proof"

    const val APP_VERSION: String = "app_version"
    const val LINK: String = "link"

    const val LATITUDE: String = "latitude"

    const val LONGITUDE: String = "longtitude"

    const val MOBILE: String = "mobile"
    const val ID: String = "id"
    const val USER_ID: String = "user_id"
    const val OTHER_USER_ID: String = "other_user_id"
    const val ONLINE_STATUS: String = "online_status"
    const val MESSAGE_NOTIFY: String = "message_notify"
    const val ADD_FRIEND_NOTIFY: String = "add_friend_notify"
    const val VIEW_NOTIFY: String = "view_notify"
    const val TYPE: String = "type"
    const val CHAT_USER_ID: String = "chat_user_id"
    const val MSG_SEEN: String = "msg_seen"
    const val UNREAD: String = "unread"
    const val CHAT_ID: String = "chat_id"
    const val TRIP_ID: String = "trip_id"
    const val FRIEND_USER_ID: String = "friend_user_id"
    const val PROFILE_USER_ID: String = "profile_user_id"
    const val FRIEND: String = "friend"
    const val PAYMENT_IMAGE: String = "payment_image"

    const val NAME: String = "name"
    const val CHAT_STATUS: String = "chat_status"
    const val UNREAD_COUNT: String = "unread_count"
    const val UNIQUE_NAME: String = "unique_name"
    const val INSTAGRAM_LINK: String = "instagram_link"
    const val TELEGRAM_LINK: String = "telegram_link"
    const val UPI_ID: String = "upi_id"
    const val SEARCH: String = "search"
    const val EMAIL: String = "email"
    const val AGE: String = "age"
    const val GENDER: String = "gender"
    const val PROFESSION: String = "profession"
    const val PROFESSION_ID: String = "profession_id"
    const val STATE: String = "state"
    const val CITY: String = "city"
    const val INTRODUCTION: String = "introduction"
    const val REFER_CODE: String = "refer_code"
    const val REFERRED_BY: String = "referred_by"
    const val VERIFIED_STATUS: String = "verified_status"
    const val POINTS: String = "points"
    const val LOGIN: String = "login"

    const val SUCCESS: String = "success"
    const val MESSAGE: String = "message"

    const val DATA: String = "data"
    const val ADDRESS: String = "address"


    const val RECIVER_PROFILE: String = "reciver_profile"
    const val RECIVER_COVER_IMG: String = "reciver_cover_img"
    const val RECIVER_GENDER: String = "reciver_gender"
    const val RECIVER_NAME: String = "reciver_name"
    const val RECIVER_AGE: String = "reciver_age"
    const val RECIVER_PROFESSION: String = "reciver_profession"
    const val RECIVER_CITY: String = "reciver_city"
    const val RECIVER_STATE: String = "reciver_state"
    const val RECIVER_UNIQUE_NAME: String = "reciver_unique_name"


    const val IMAGE: String = "image"
    const val PAYMENT_PROOF: String = "payment_proof"

    const val PROFILE: String = "profile"
    const val COVER_IMG: String = "cover_img"
    const val VERIFIED: String = "verified"

    const val PROFILE_VERIFIED: String = "profile_verified"

    const val FRONT_IMAGE: String = "front_image"
    const val BACK_IMAGE: String = "back_image"
    const val BACK_PRESSED: String = "back_pressed"
    const val SELFIE_IMAGE: String = "selfie_image"
    const val VERIFICATION_FALSE: String = "verification_false"
    const val BOOKID: String = "book_id"
    const val CART_ID: String = "cart_id"
    const val TYPE_TRIP_DATE: String = "trip_date"
    val RECEIVER_PROFILE: String? = "receiver_profile"

}