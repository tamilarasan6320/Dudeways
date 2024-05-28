package gmw.app.dudeways.Model

class HomeProfile {



    var id: String? = null
    var user_name: String? = null
    var unique_name: String? = null
    var user_profile: String? = null
    var planning: String? = null
    var from_date: String? = null
    var to_date: String? = null
    var trip_title: String? = null
    var trip_description: String? = null
    var from_location: String? = null
    var to_location: String? = null
    var meetup_location: String? = null
    var datetime: String? = null
    var updated_at: String? = null
    var created_at: String? = null


    constructor(
        id: String?,
        user_name: String?,
        unique_name: String?,
        planning: String?,
        from_date: String?,
        to_date: String?,
        trip_title: String?,
        trip_description: String?,
        from_location: String?,
        to_location: String?,
        meetup_location: String?,
        datetime: String?,
        updated_at: String?,
        created_at: String?,
        user_profile: String?
    ) {
        this.id = id
        this.user_name = user_name
        this.unique_name = unique_name
        this.planning = planning
        this.from_date = from_date
        this.to_date = to_date
        this.trip_title = trip_title
        this.trip_description = trip_description
        this.from_location = from_location
        this.to_location = to_location
        this.meetup_location = meetup_location
        this.datetime = datetime
        this.updated_at = updated_at
        this.created_at = created_at
        this.user_profile = user_profile
    }



}