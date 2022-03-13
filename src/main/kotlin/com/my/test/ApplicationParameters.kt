package com.my.test

data class ApplicationParameters(
    var idUser: String? = null,
    var amount: Double = 0.0,
    var creditTerm: Int = 0,
    var loanRate: Double = 0.0
)