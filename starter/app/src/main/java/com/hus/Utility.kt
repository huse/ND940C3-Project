package com.hus

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor

object Utility  {

    fun Intent.receive(downloadAction: String, context: Context) : Boolean{
        var loadingResult = false
        if (downloadAction == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val query = DownloadManager.Query()
            query.setFilterById(getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadCursor: Cursor = downloadManager.query(query)
            if (downloadCursor.moveToFirst()) {
                if (downloadCursor.count > 0) {
                    val downloadStatus = downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) loadingResult = true
                }
            }
        }
        return loadingResult
    }


    fun String.convertToHTTP(): String {
        var temp = this
        if (!this.startsWith("http")) {
            temp = "https://$temp"
        }
        return temp
    }

}