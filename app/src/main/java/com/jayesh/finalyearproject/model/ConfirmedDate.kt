package com.jayesh.finalyearproject.model

class ConfirmedDate {
    var date: String? = null
    var currId: String? = null
    var currTime: String? = null

    constructor() {}

    constructor(date: String, currId: String, currTime: String) {
        this.date = date
        this.currId = currId
        this.currTime = currTime
    }
}