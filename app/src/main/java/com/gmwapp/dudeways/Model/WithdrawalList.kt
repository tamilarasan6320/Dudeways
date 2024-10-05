package com.gmwapp.dudeways.Model

class WithdrawalList {

    var id: Int? = null
    var user_id: String? = null
    var amount: String? = null
    var datetime: String? = null
    var status: Int? = null

    constructor(
        id: Int?,
        userId: String?,
        amount: String?,
        datetime: String?,
        status: Int?,
    ) {
        this.id = id
        this.user_id = userId
        this.amount = amount
        this.datetime = datetime
        this.status = status
    }
}
