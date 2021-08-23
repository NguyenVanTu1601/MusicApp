package com.example.musicapp.view.main

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.broadcast.MusicBroadcast
import com.example.musicapp.data.model.Music
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.service.MusicPlayerService
import com.example.musicapp.utils.Constants
import com.example.musicapp.view.library.MusicLibraryActivity
import com.example.musicapp.view.notification.MusicNotificationCallback
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(),
    View.OnClickListener,MainContract.View, SeekBar.OnSeekBarChangeListener,
    MusicNotificationCallback {

    private lateinit var binding: ActivityMainBinding
    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private var controlButtons = listOf<TextView>()
    private lateinit var runnable: Runnable
    private val handler = Handler()
    private lateinit var musicPlayerService: MusicPlayerService
    private var musicBroadcast: MusicBroadcast? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicPlayerService = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicPlayerService.stopSelf()
        }

    }
    private val musics = mutableListOf<Music>()
    private lateinit var nameSong: String
    private lateinit var author: String
    private lateinit var id: String
    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewBinding()
        initComponents()
    }

    private fun initComponents() {
        if (checkPermission()) {
            initView()
            initService()
            initData()
            setOnClicks()
        } else requestPermission()
    }
    private fun initView() {
        binding.run {
            seekBarTime.setOnSeekBarChangeListener(this@MainActivity)
            listOf(
                textNextSong,
                textPlaySong,
                textPreviousSong
            ).forEach {
                it.setOnClickListener(this@MainActivity)
            }
        }
    }

    private fun initService() {
        musicPlayerService = MusicPlayerService()
        bindService(
            MusicPlayerService.getIntent(this),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        musicBroadcast = MusicBroadcast(this)
        IntentFilter().apply {
            addAction(Constants.NEXT)
            addAction(Constants.PLAY_PAUSE)
            addAction(Constants.PREVIOUS)
        }.run {
            registerReceiver(musicBroadcast, this)
        }
    }

    private fun initData(){
        this.presenter = MainPresenter(this)
        getDataSongFromIntent()
    }
    private fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getDataSongFromIntent() {
        val music = intent?.extras?.getSerializable(Constants.KEY_PASS_DATA) as Music
        setTextMusic(music)
    }

    private fun setTextMusic(music: Music) {
        nameSong = music.name.toString()
        author = music.author.toString()
        id = music.id.toString()
        binding.textNameSong.text = nameSong
        binding.textAuthor.text = author
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(permissions, Constants.REQUEST_CODE)
    }
    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (p in permissions) {
                if (checkSelfPermission(p) == PackageManager.PERMISSION_DENIED)
                    return false
            }
        return true
    }

    private fun setOnClicks() {
        with(binding) {
            controlButtons = listOf(
                textBack,
                textPreviousSong,
                textPlaySong,
                textNextSong,
            )
        }
        controlButtons.forEach { it.setOnClickListener(this) }
    }

    override fun loadListMusic(listMusics: ArrayList<Music>) {
        this.musics.addAll(listMusics)
    }

    override fun loadMusicFailed() {
        Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }

    override fun loadNextMusic(music: Music) {
        setTextMusic(music)
        musicPlayerService.createMedia(this@MainActivity, music)
        musicPlayerService.play()
        binding.textPlaySong.setBackgroundResource(R.drawable.ic_pause)
        updateSeekBar()
    }

    override fun loadPrevMusic(music: Music) {
        setTextMusic(music)
        musicPlayerService.createMedia(this@MainActivity, music)
        musicPlayerService.play()
        binding.textPlaySong.setBackgroundResource(R.drawable.ic_pause)
        updateSeekBar()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
            changeStatusPlayPause(true)
            musicPlayerService.seekTo(it)
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
            musicPlayerService.seekTo(it)
        }
    }

    override fun onNotifyPlayPause() {
        if (musicPlayerService.isPlaying() == true) {
            changeStatusPlayPause(false)
        } else {
            musicPlayerService.createMedia(this@MainActivity, Music(id, nameSong, author))
            changeStatusPlayPause(true)
        }
        updateSeekBar()
    }

    override fun onNotifyNext() {
        presenter.loadDataNextMusic(id, this.musics)
    }

    override fun onNotifyPrev() {
        presenter.loadDataPrevMusic(id, this.musics)
    }

    override fun onClick(view: View?) = with(binding) {
        if (view is TextView) {
            when (view) {
                textPlaySong -> playOrPauseMusic()
                textNextSong -> nextMusic()
                textPreviousSong -> prevMusic()
                textBack -> backView()
                else -> Unit
            }
        }
    }

    private fun backView() {
        musicPlayerService.stop()
        startActivity(Intent(this@MainActivity, MusicLibraryActivity::class.java))
    }

    private fun prevMusic() {
        presenter.loadDataPrevMusic(id, this.musics)
    }

    private fun nextMusic() {
        presenter.loadDataNextMusic(id, this.musics)
    }

    private fun playOrPauseMusic() {
        if (musicPlayerService.isPlaying() == true) {
            changeStatusPlayPause(false)
        } else {
            musicPlayerService.createMedia(this@MainActivity, Music(id, nameSong, author))
            changeStatusPlayPause(true)
        }
        updateSeekBar()
    }

    private fun changeStatusPlayPause(status: Boolean) {
        if (status) {
            musicPlayerService.play()
            binding.textPlaySong.setBackgroundResource(R.drawable.ic_pause)
        } else {
            musicPlayerService.pause()
            binding.textPlaySong.setBackgroundResource(R.drawable.ic_play)
        }
    }

    private fun updateSeekBar() {
        with(binding) {
            textTotalTime.text = getTimeFormat(musicPlayerService.getDuration())
            with(seekBarTime) {
                progress = Constants.PROGRESS_INIT
                max = musicPlayerService.getDuration()
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        view: SeekBar?,
                        progress: Int,
                        changed: Boolean
                    ) {
                        if (changed) {
                            musicPlayerService.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(view: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(view: SeekBar?) {
                    }

                })
            }
        }

        runnable = Runnable {
            musicPlayerService.getCurrentPosition()?.let {
                binding.seekBarTime.progress = it
                binding.textTimeCurrent.text = getTimeFormat(it)
                if (musicPlayerService.isPlaying() == true) {
                    handler.postDelayed(runnable, Constants.TIME_DELAYED.toLong())
                }
            }
        }
        handler.postDelayed(runnable, Constants.TIME_DELAYED.toLong())
        musicPlayerService.mediaPlayer?.setOnCompletionListener {
            binding.seekBarTime.progress = Constants.PROGRESS_INIT
        }
    }

    fun getTimeFormat(millis: Int): String {
        return String.format(
            Constants.TIME_FORMAT,
            TimeUnit.MILLISECONDS.toMinutes(millis.toLong()) % TimeUnit.HOURS.toMinutes(Constants.DURATION),
            TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) % TimeUnit.MINUTES.toSeconds(Constants.DURATION)
        )
    }

    override fun onBackPressed() {
        musicPlayerService.stop()
        startActivity(Intent(this@MainActivity, MusicLibraryActivity::class.java))
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerService.stopSelf()
        unregisterReceiver(musicBroadcast)
    }
}
