package com.app.dudeways.Model

class Chatlist {




    var id: String? = null
    var user_id: String? = null
    var name: String? = null
    var profile: String? = null
    var chat_user_id: String? = null
    var latest_message: String? = null
    var latest_msg_time: String? = null
    var msg_seen: String? = null
    var datetime: String? = null
    var updated_at: String? = null
    var created_at: String? = null

    constructor(
        id: String?,
        user_id: String?,
        name: String?,
        profile: String?,
        chat_user_id: String?,
        latest_message: String?,
        latest_msg_time: String?,
        msg_seen: String?,
        datetime: String?,
        updated_at: String?,
        created_at: String?
    ) {
        this.id = id
        this.user_id = user_id
        this.name = name
        this.profile = profile
        this.chat_user_id = chat_user_id
        this.latest_message = latest_message
        this.latest_msg_time = latest_msg_time
        this.msg_seen = msg_seen
        this.datetime = datetime
        this.updated_at = updated_at
        this.created_at = created_at
    }




}