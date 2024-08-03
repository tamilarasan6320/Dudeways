package com.gmwapp.dudeways.Model

class Planlist {



    var id: String? = null
    var plan_name: String? = null
    var validity: String? = null
    var price: String? = null
    var save_amount: String? = null



    constructor(
        id: String?,
        plan_name: String?,
        validity: String?,
        price: String?,
        save_amount: String?,


    ) {
        this.id = id
        this.plan_name = plan_name
        this.validity = validity
        this.price = price
        this.save_amount = save_amount


    }




}