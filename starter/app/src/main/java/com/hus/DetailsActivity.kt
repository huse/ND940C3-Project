package com.hus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hus.notification.ModelOfNotification
import kotlinx.android.synthetic.main.content_detail.*
import timber.log.Timber

class DetailsActivity : AppCompatActivity() {

    private lateinit var downloadStatus: String
    private lateinit var downloadedName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val initiateExtras = intent.extras
        initiateExtras?.let {
            val modelOfNotification: ModelOfNotification? = it.getParcelable("notification_model")
            modelOfNotification?.apply {
                this@DetailsActivity.downloadStatus = downloadStatus
                downloadedName = nameOfFile
            }
        }
        if (::downloadStatus.isInitialized){
            Timber.i("sss  yess : "  + downloadStatus)
            status_result.text = downloadStatus
            file_name.text = downloadedName
        }else{

            Timber.i("sss  no : "  + downloadStatus)
            status_result.text = getString(R.string.failure)
            file_name.text = downloadedName

        }

        ok_button.setOnClickListener {
            finish()
        }
    }


}
