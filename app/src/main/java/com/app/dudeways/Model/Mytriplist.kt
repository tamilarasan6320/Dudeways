package com.app.dudeways.Model

class Mytriplist {
    var id: String? = null
    var name: String? = null
    var unique_name: String? = null
    var profile: String? = null
    var planning: String? = null
    var from_date: String? = null
    var to_date: String? = null
    var trip_title: String? = null
    var trip_description: String? = null
    var from_location: String? = null
    var location: String? = null
    var meetup_location: String? = null
    var datetime: String? = null
    var updated_at: String? = null
    var created_at: String? = null
    var friend: String? = null
    var verified: String? = null
    var trip_image: String? = null
    var trip_status: String? = null


    constructor(
        id: String?,
        name: String?,
        unique_name: String?,
        planning: String?,
        from_date: String?,
        to_date: String?,
        trip_title: String?,
        trip_description: String?,
        from_location: String?,
        location: String?,
        meetup_location: String?,
        datetime: String?,
        updated_at: String?,
        created_at: String?,
        profile: String?,
        friend: String?,
        verified: String?,
        trip_image: String?,
        trip_status: String?

    ) {
        this.id = id
        this.name = name
        this.unique_name = unique_name
        this.planning = planning
        this.from_date = from_date
        this.to_date = to_date
        this.trip_title = trip_title
        this.trip_description = trip_description
        this.from_location = from_location
        this.location = location
        this.meetup_location = meetup_location
        this.datetime = datetime
        this.updated_at = updated_at
        this.created_at = created_at
        this.profile = profile
        this.friend = friend
        this.verified = verified
        this.trip_image = trip_image
        this.trip_status = trip_status
    }

}