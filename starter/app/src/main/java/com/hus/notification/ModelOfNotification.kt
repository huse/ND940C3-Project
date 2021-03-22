package com.hus.notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelOfNotification (
        val nameOfFile: String,
        val downloadStatus: String
) : Parcelable