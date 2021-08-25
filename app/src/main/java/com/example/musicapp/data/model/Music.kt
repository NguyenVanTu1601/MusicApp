package com.example.musicapp.data.model

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(val id: String?, val name: String?, val author: String?) : Parcelable {
    constructor(cursor: Cursor) : this(
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
    )
}
