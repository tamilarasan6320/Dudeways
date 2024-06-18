package com.app.dudeways.Model

class Notification {

    var id: Int? = null
    var userId: Int? = null
    var notifyUserId: Int? = null
    var name: String? = null
    var profile: String? = null
    var coverImg: String? = null
    var message: String? = null
    var datetime: String? = null
    var time: String? = null
    var updatedAt: String? = null
    var createdAt: String? = null

    constructor(
        id: Int?,
        userId: Int?,
        notifyUserId: Int?,
        name: String?,
        profile: String?,
        coverImg: String?,
        message: String?,
        datetime: String?,
        time: String?,
        updatedAt: String?,
        createdAt: String?
    ) {
        this.id = id
        this.userId = userId
        this.notifyUserId = notifyUserId
        this.name = name
        this.profile = profile
        this.coverImg = coverImg
        this.message = message
        this.datetime = datetime
        this.time = time
        this.updatedAt = updatedAt
        this.createdAt = createdAt
    }



}