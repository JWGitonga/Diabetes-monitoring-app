package com.example.diabeteshealthmonitoringapplication.models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class ReadingNode(val uid: String?, val readings: ArrayList<Reading>?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(Reading.CREATOR)
    ) {
    }
    constructor() : this("",null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeTypedList(readings)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReadingNode> {
        override fun createFromParcel(parcel: Parcel): ReadingNode {
            return ReadingNode(parcel)
        }

        override fun newArray(size: Int): Array<ReadingNode?> {
            return arrayOfNulls(size)
        }
    }
}