package com.vandenbreemen.mobilesecurestorage.patterns.mvp

import java.util.*

//  Common interactors

interface DateInteractor {
    fun getDateTime(): Calendar
}

fun getDateInteractor(): DateInteractor {
    return object : DateInteractor {
        override fun getDateTime(): Calendar {
            return Calendar.getInstance()
        }
    }
}