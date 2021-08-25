package com.example.musicapp.view.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.musicapp.R
import com.example.musicapp.data.model.Music
import com.example.musicapp.service.MusicPlayerService
import com.example.musicapp.utils.Constants

class MusicNotification(private val service: MusicPlayerService) {
    private var notificationManager: NotificationManager? = null
    private val remoteView : RemoteViews by lazy {
        RemoteViews(service.packageName, R.layout.remote_play_music)
    }

    fun createNotification(music: Music) {
        createChannel()
        val mBuilder = NotificationCompat.Builder(service, Constants.CHANNEL_ID)
        with(mBuilder){
            setSmallIcon(R.drawable.ic_headphones)
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            setCustomContentView(createRemoteView(music))
            setSound(null)
            setPriority(NotificationCompat.PRIORITY_HIGH) // property

        }
        val notificationManager =
            NotificationManagerCompat.from(service)
        notificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build())

    }

    private fun createRemoteView(music: Music): RemoteViews {
        remoteView.setTextViewText(R.id.text_remote_music_name,music.name)
        createPendingIntent(Constants.PLAY_PAUSE, R.id.text_remote_play)
        createPendingIntent(Constants.NEXT, R.id.text_remote_next)
        createPendingIntent(Constants.PREVIOUS, R.id.text_remote_prev)
        return remoteView

    }

    private fun createPendingIntent(action: String, viewId: Int) {
        val pendingIntent =
            PendingIntent.getBroadcast(service, Constants.BROADCAST_ID, Intent(action), Constants.FLAG)
        remoteView.setOnClickPendingIntent(viewId, pendingIntent)
    }

    private fun createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager = service.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

    }

    fun changeIconNotification(state: String){
        if (state == Constants.PLAY_PAUSE){
            remoteView.setInt(R.id.text_remote_play,Constants.METHOD_NAME_NOTIFY, R.drawable.ic_pause)
        }else if(state == Constants.PAUSE){
            remoteView.setInt(R.id.text_remote_play,Constants.METHOD_NAME_NOTIFY, R.drawable.ic_play)
        }
    }
}
