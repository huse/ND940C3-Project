package com.hus

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.hus.Utility.convertToHTTP
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import com.hus.Utility.receive
import com.hus.notification.DataNotificationChannel
import com.hus.notification.ModelOfNotification
import com.hus.notification.UtilityNotification.makingChannelNotification
import com.hus.notification.UtilityNotification.sendingNotification
import java.io.File


class MainActivity : AppCompatActivity() {

    private var chosenUrl: String? = null
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadButton : LoadingButton
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {


            when (radio_button_group.checkedRadioButtonId) {
                R.id.radio_button_glide_download -> {
                    downloading(URLForMaterUdacity)
                    Toast.makeText(this, URLForMaterUdacity, Toast.LENGTH_SHORT).show()
                }
                R.id.radio_button_udacity_download -> {
                    downloading(URLForMasterGlide)
                    Toast.makeText(this, URLForMasterGlide, Toast.LENGTH_SHORT).show()
                }
                R.id.radio_button_retrofit_download -> {
                    downloading(UrlForMasterRetrofite)
                    Toast.makeText(this, UrlForMasterRetrofite, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            Timber.i("making receiver: ")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id){

                if (intent.receive(intent.action.toString(), context)){
                    custom_button.settingLoadStateForButton(ButtonState.DownloadCompleted)
                    val notificationModel = ModelOfNotification(
                            chosenUrl.toString(),
                            getString(R.string.successful_download)
                    )
                    notificationManager.sendingNotification(applicationContext, notificationModel)
                }else{
                    custom_button.settingLoadStateForButton(ButtonState.Failure)
                    val notificationModel = ModelOfNotification(
                            chosenUrl.toString(),
                            getString(R.string.failure)
                    )
                    notificationManager.sendingNotification(applicationContext, notificationModel)
                }

            }
        }
    }

    private fun downloading(stringUrl: String) {

        Timber.plant(Timber.DebugTree())
        Timber.i("hhh  download started:  %s", stringUrl)
        chosenUrl = stringUrl
        val urlHttp = stringUrl?.convertToHTTP()
        custom_button.settingLoadStateForButton(ButtonState.Downloading)
        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = DataNotificationChannel(
                    NotificationManager.IMPORTANCE_HIGH,
                    NotificationCompat.PRIORITY_HIGH,
                    NotificationCompat.VISIBILITY_PUBLIC,

                    applicationContext.getString(R.string.channel_id_for_notification),
                    applicationContext.getString(R.string.channel_name_for_notification),
                    applicationContext.getString(R.string.channel_description_for_notification)
            )
            makingChannelNotification(applicationContext, channel)
        }
        val fileForDownloadedContent = File(getExternalFilesDir(null), getString(R.string.stashed_repo_folder))
        if (!fileForDownloadedContent.exists()) {
            fileForDownloadedContent.mkdirs()
        }
        val request =
            DownloadManager.Request(Uri.parse(urlHttp))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        Timber.i("hhh  download queued.   "   + downloadManager)

        val processingLoadingData = ProcessingLoadingData(application, downloadID)
        processingLoadingData.observe(this, Observer {
            var floatProgress = ((it.loadedInTimeBt * 100L) / it.sizeOfFileInBt).toFloat()
            if (it.sizeOfFileInBt != (-1).toLong()){
                floatProgress = (floatProgress / 100)
                custom_button.settingProgressOfButton(floatProgress)
            }else if (it.loadedInTimeBt > 1) {
                custom_button.addingButtonProgress(0.05f)
            }

        })

    }

    companion object {
        private const val URLForMaterUdacity = "https://github.com/hus/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"

        private const val URLForMasterGlide = "https://github.com/bumptech/glide/archive/master.zip"

        private const val UrlForMasterRetrofite = "https://github.com/square/retrofit/archive/master.zip"
    }
    fun onRadioButtonClicked(rbView: View) {
        Timber.plant(Timber.DebugTree())
        Timber.i("hhh  onRadioButtonClicked" + rbView)
        if (rbView is RadioButton && custom_button.stateOfButton == ButtonState.Inititial){

            Timber.i("hhh  onRadioButtonClicked  inside if statement.")
            custom_button.settingLoadStateForButton(ButtonState.ButtonClicked)
        }
    }
}
