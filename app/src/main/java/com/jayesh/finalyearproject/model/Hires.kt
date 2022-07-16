package com.jayesh.finalyearproject.model

class Hires {
    var uname: String? = null
    var uid: String? = null
    var uDate: String? = null
    var senderUpi: String? = null
    var receiverUpi: String? = null
    var transactionStatus: String? = null
    var transactionId: String? = null
    var transactionRefId: String? = null
    var approvalRefNo: String? = null
    var amount: String? = null
    var userContact: String? = null
    var payDate: String? = null


    constructor() {}

    constructor(
        uname: String,
        uid: String,
        uDate: String,
        senderUpi: String,
        transactionStatus: String,
        transactionId: String,
        transactionRefId: String,
        amount: String,
        userContact: String,
        payDate: String,
    ) {
        this.uname = uname
        this.uid = uid
        this.uDate = uDate
        this.senderUpi = senderUpi
        this.transactionStatus = transactionStatus
        this.transactionId = transactionId
        this.transactionRefId = transactionRefId
        this.amount = amount
        this.userContact = userContact
        this.payDate = payDate
    }
}