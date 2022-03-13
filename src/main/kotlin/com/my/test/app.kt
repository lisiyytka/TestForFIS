package com.my.test

import java.math.RoundingMode
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.math.pow

fun main() {
    val idUserFromConsole = readLine()
    val applicationParameters1 = ApplicationParameters()
    val listOfData = getData()
    for (i in listOfData) {
        if (i.idUser == idUserFromConsole) {
            applicationParameters1.idUser = i.idUser
            applicationParameters1.creditTerm = i.creditTerm
            applicationParameters1.loanRate = i.loanRate
            applicationParameters1.amount = i.amount
        }
    }
    var monthlyPayment: Double
    var mainDebt: Double
    var interestDebt: Double
    var balanceOfThePrincipal = applicationParameters1.amount
    val psM = applicationParameters1.loanRate / (100 * applicationParameters1.creditTerm)
    monthlyPayment = applicationParameters1.amount * (psM / (1 - (1 + psM).pow(-1 * applicationParameters1.creditTerm)))
    monthlyPayment = monthlyPayment.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    println("Сумма кредита: ${applicationParameters1.amount}")
    println("Ставка: ${applicationParameters1.loanRate}")
    println("Срок: ${applicationParameters1.creditTerm}")
    println("Месяц | Ежемесячный платеж | Основной долг | Долг по процентам | Остаток основного платежа")
    for (i in 1..applicationParameters1.creditTerm) {
        interestDebt = balanceOfThePrincipal * psM
        interestDebt = interestDebt.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        mainDebt = monthlyPayment - interestDebt
        mainDebt = mainDebt.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        balanceOfThePrincipal -= mainDebt
        balanceOfThePrincipal = balanceOfThePrincipal.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        println("$i      | $monthlyPayment          | $mainDebt          | $interestDebt          | $balanceOfThePrincipal")
    }
}

fun getData(): ArrayList<ApplicationParameters> {
    var conn: Connection? = null

    try {
        val controller = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:identifier.sqlite"
        conn = DriverManager.getConnection(url)
        Class.forName(controller)
    } catch (ex: ClassNotFoundException) {
        println("Проблема с драйвером")
    } catch (ex: SQLException) {
        println("Проблема с подключением")
    }

    val listOfData = ArrayList<ApplicationParameters>()

    try {
        val st = conn?.createStatement()
        val query = "SELECT * FROM APPLICATION_PARAMETERS"
        val rs = st?.executeQuery(query)
        while (rs!!.next()) {
            val applicationParameters = ApplicationParameters(
                rs.getString("userId"),
                rs.getDouble("loanAmountIncludingAddServices"),
                rs.getInt("creditTerm"),
                rs.getDouble("loanRate")
            )
            listOfData.add(applicationParameters)
        }
    } catch (ex: SQLException) {
        println(ex)
    }

//    for (applicationParameters in listOfData) {
//        println(applicationParameters)
//    }
    conn?.close()

    return listOfData
}
