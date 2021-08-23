package com.example.musicapp.data.library

import android.content.ContentResolver
import android.provider.MediaStore
import com.example.musicapp.data.model.Music
import com.example.musicapp.data.repository.MusicDataSource

class MusicDataLocal(
    private val contentResolver: ContentResolver
    ) : MusicDataSource {

    override fun getAllMusic(callback: OnDataCallBack<List<Music>>) {
        DataLoad<Unit, List<Music>>(callback) {
            getAllAudioFromDevice()
        }.execute(Unit)
    }

    private fun getAllAudioFromDevice(): List<Music> {
        val musics = ArrayList<Music>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf<String>(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                musics.add(Music(it))
            }
        }
        cursor?.close()
        return musics
    }

    companion object {
        private var instance: MusicDataLocal? = null
        fun getInstance(contentResolver: ContentResolver) = synchronized(this) {
            instance ?: MusicDataLocal(contentResolver).also { instance = it }
        }
    }
}
