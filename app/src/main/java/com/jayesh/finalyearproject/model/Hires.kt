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


    constructor() {}

    constructor(
        uname: String,
        uid: String,
        uDate: String,
        senderUpi: String,
        receiverUpi: String,
        transactionStatus: String,
        transactionId: String,
        transactionRefId: String,
        approvalRefNo: String,
        amount: String
    ) {
        this.uname = uname
        this.uid = uid
        this.uDate = uDate
        this.senderUpi = senderUpi
        this.receiverUpi = receiverUpi
        this.transactionStatus = transactionStatus
        this.transactionId = transactionId
        this.transactionRefId = transactionRefId
        this.approvalRefNo = approvalRefNo
        this.amount = amount
    }
}