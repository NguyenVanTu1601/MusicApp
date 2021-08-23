package com.example.musicapp.service

import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import com.example.musicapp.data.model.Music
import com.example.musicapp.utils.Constants
import com.example.musicapp.view.notification.MusicNotification

class MusicPlayerService : Service() {

    var mediaPlayer: MediaPlayer? = null
    private var musicNotification: MusicNotification? = null
    private var playingMusic: Music? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        musicNotification = MusicNotification(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder(this)
    }

    fun createMedia(context: Context, music: Music) {
        playingMusic = music
        mediaPlayer?.release()
        val uri = music.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }
        mediaPlayer = MediaPlayer.create(context, uri)
    }

    fun play() {
        mediaPlayer?.setOnPreparedListener { mediaPlayer?.start() }
        playingMusic?.let { musicNotification?.createNotification(it) }
        musicNotification?.chageIconNotification(Constants.PLAY_PAUSE)
    }

    fun pause() {
        mediaPlayer?.pause()
        musicNotification?.chageIconNotification(Constants.PAUSE)
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun prepare() {
        mediaPlayer?.prepare()
    }

    fun seekTo(newPosition: Int) {
        mediaPlayer?.seekTo(newPosition)
    }

    fun getDuration() = mediaPlayer?.duration ?: Constants.PROGRESS_INIT

    fun getCurrentPosition() = mediaPlayer?.currentPosition

    fun isPlaying() = mediaPlayer?.isPlaying

    class MusicBinder(private var service: MusicPlayerService) : Binder() {
        fun getService(): MusicPlayerService = service
    }

    override fun onDestroy() {
        super.onDestroy()
        with(mediaPlayer) {
            this?.release()
            stop()
        }
        this.stopSelf()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MusicPlayerService::class.java)
    }
}
