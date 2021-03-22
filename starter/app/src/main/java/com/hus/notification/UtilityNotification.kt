package com.hus.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hus.DetailsActivity
import com.hus.R

object UtilityNotification {

    private val idNotification = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun makingChannelNotification(context: Context, dataNotificationChannel: DataNotificationChannel) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannel(
                dataNotificationChannel.notificationId,
                dataNotificationChannel.notificationName,
                dataNotificationChannel.notificationImportant
        ).apply {
            setShowBadge(true)
            description = dataNotificationChannel.notificationDescription
            lockscreenVisibility = dataNotificationChannel.notificationVisible
            notificationManager.createNotificationChannel(this)
        }
    }

    fun NotificationManager.sendingNotification(appContext: Context, notificationModel: ModelOfNotification) {
        val intentContent = Intent(appContext, DetailsActivity::class.java)
        intentContent.putExtra("notification_model", notificationModel)

        val intentPending = PendingIntent.getActivity(
                appContext,
                idNotification,
                intentContent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationCompatBuilder = NotificationCompat.Builder(
                appContext,
                appContext.getString(
                        R.string.channel_id_for_notification
                )
        )
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(appContext.getString(R.string.loading_completed))
                .setContentText(notificationModel.nameOfFile)
                .setContentIntent(intentPending)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(
                        NotificationCompat.Action(
                                null,
                                appContext.getString(R.string.view_details),
                                intentPending
                        )
                )
        notify(idNotification, notificationCompatBuilder.build())
    }
}