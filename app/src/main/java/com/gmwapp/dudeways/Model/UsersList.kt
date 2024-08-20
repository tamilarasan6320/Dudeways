package com.gmwapp.dudeways.Model

class UsersList {
    var id: Int? = null
    var name: String? = null
    var unique_name: String? = null
    var email: String? = null
    var mobile: String? = null
    var gender: String? = null
    var profile: String? = null
    var cover_img: String? = null
    var datetime: String? = null
    var age: String? = null
//    var distance: String? = null
    var verified: String? = null
    var friend: String? = null
    var introduction: String? = null

    constructor(
        id: Int?,
        name: String?,
        unique_name: String?,
        email: String?,
        mobile: String?,
        gender: String?,
        profile: String?,
        cover_img: String?,
        datetime: String?,
        age: String?,
//        distance: String?,
        verified: String?,
        friend: String?,
        introduction: String?,

        ) {
        this.id = id
        this.name = name
        this.unique_name = unique_name
        this.email = email
        this.mobile = mobile
        this.gender = gender
        this.profile = profile
        this.cover_img = cover_img
        this.datetime = datetime
        this.age = age
//        this.distance = distance
        this.verified = verified
        this.friend = friend
        this.introduction = introduction

    }
}

