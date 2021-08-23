package com.example.musicapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapp.utils.Constants
import com.example.musicapp.view.notification.MusicNotificationCallback

class MusicBroadcast(
    private val musicCallback: MusicNotificationCallback?
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Constants.NEXT -> musicCallback?.onNotifyNext()
            Constants.PLAY_PAUSE -> musicCallback?.onNotifyPlayPause()
            Constants.PREVIOUS -> musicCallback?.onNotifyPrev()
        }
    }
}
