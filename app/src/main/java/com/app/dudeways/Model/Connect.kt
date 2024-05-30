package com.app.dudeways.Model

class Connect {



    var id: String? = null
    var user_id: String? = null
    var name: String? = null
    var image: String? = null
    var last_seen: String? = null
    var friend_user_id: String? = null
    var friend_user_name: String? = null
    var status: String? = null
    var datetime: String? = null
    var updated_at: String? = null
    var created_at: String? = null
    var profile: String? = null



    constructor(id: String?, user_id: String?, name: String?, image: String?, last_seen: String?, friend_user_id: String?, friend_user_name: String?, status: String?, datetime: String?, updated_at: String?, created_at: String?,profile: String?) {
        this.id = id
        this.user_id = user_id
        this.name = name
        this.image = image
        this.last_seen = last_seen
        this.friend_user_id = friend_user_id
        this.friend_user_name = friend_user_name
        this.status = status
        this.datetime = datetime
        this.updated_at = updated_at
        this.created_at = created_at
        this.profile = profile
    }



}