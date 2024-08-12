package com.gmwapp.dudeways.Model

class Connect {
    var id: String? = null
    var user_id: String? = null
    var friend_user_id: String? = null
    var name: String? = null
    var introduction: String? = null
    var gender: String? = null
    var age: String? = null
    var online_status: String? = null
    var profile: String? = null
    var last_seen: String? = null
    var distance: String? = null
    var status: String? = null
    var datetime: String? = null
    var updated_at: String? = null
    var created_at: String? = null
    var friend: String? = null
    var unique_name: String? = null
    var verified: String? = null


    constructor(
        id: String?,
        user_id: String?,
        friend_user_id: String?,
        name: String?,
        introduction: String?,
        gender: String?,
        age: String?,
        online_status: String?,
        profile: String?,
        last_seen: String?,
        distance: String?,
        status: String?,
        datetime: String?,
        updated_at: String?,
        created_at: String?,
        friend: String?,
        unique_name: String?,
        verified: String?
    ) {
        this.id = id
        this.user_id = user_id
        this.friend_user_id = friend_user_id
        this.name = name
        this.introduction = introduction
        this.gender = gender
        this.age = age
        this.online_status = online_status
        this.profile = profile
        this.last_seen = last_seen
        this.distance = distance
        this.status = status
        this.datetime = datetime
        this.updated_at = updated_at
        this.created_at = created_at
        this.friend = friend
        this.unique_name = unique_name
        this.verified = verified

 }




}