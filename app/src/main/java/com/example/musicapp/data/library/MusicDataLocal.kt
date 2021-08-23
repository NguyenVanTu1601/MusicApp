package com.example.musicapp.data.library

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import com.example.musicapp.data.model.Music
class MusicDataLocal(private val contentResolver: ContentResolver) {

    fun getAllAudioFromDevice(): List<Music> {
        val musics = ArrayList<Music>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf<String>(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                musics.add(Music(id, title, artist))
            }
        }
        cursor?.close()
        return musics
    }

    companion object {
        private var instance: MusicDataLocal? = null
        fun getInstance(contentResolver: ContentResolver) =
            instance ?: MusicDataLocal(contentResolver).also { instance = it }
    }
}
