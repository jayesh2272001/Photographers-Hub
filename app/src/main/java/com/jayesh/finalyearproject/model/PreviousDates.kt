package com.jayesh.finalyearproject.model

class PreviousDates {
    var date: String? = null
    var name: String? = null
    var availability: Boolean? = null


    constructor() {}

    constructor(date: String, name: String, availability: Boolean) {
        this.date = date
        this.availability = availability
        this.name = name
    }
}