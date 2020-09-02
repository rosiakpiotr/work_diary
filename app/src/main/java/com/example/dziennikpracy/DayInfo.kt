package com.example.dziennikpracy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DayInfo(
    var state: Int = 0,
    var note: String = ""
) : Parcelable