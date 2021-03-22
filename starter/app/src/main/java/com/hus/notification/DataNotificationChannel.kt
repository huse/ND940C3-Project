package com.hus.notification

data class DataNotificationChannel(

        val notificationImportant: Int,
        val notificationPrior: Int,
        val notificationVisible: Int,
        val notificationId: String,
        val notificationName: String,
        val notificationDescription: String
)
