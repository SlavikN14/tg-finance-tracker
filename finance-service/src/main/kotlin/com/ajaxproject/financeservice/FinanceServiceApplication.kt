package com.ajaxproject.financeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinanceServiceApplication

fun main(vararg args: String) {
    runApplication<FinanceServiceApplication>(*args)
}
