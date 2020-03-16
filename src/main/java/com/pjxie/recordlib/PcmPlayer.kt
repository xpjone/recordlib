package com.pjxie.recordlib

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.HandlerThread
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream
import java.util.concurrent.Executors

/**
 * 创建者：pjxie
 * 创建日期：2019-05-24PcmPlayer
 * 邮箱：pjxie@iflytek.com
 * 描述：TODO
 */
class PcmPlayer {

    private var audioTrack: AudioTrack? = null

    private val SAMPLE_RATE = 16000

    private val CHANNEL = AudioFormat.CHANNEL_OUT_MONO


    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    private var mBufferSizeInBytes = 0

    private val mExecutorService = Executors.newSingleThreadExecutor()


    @Volatile
    var STATUS_STATE: Int = STATUS_NO_READY

    @SuppressLint("NewApi")
    private fun initAudio() {
        mBufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)
        if (mBufferSizeInBytes <= 0) {
            throw IllegalAccessException("AudioTrack not available")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                    .setAudioFormat(AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(2).build())
                    .setBufferSizeInBytes(1280)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
        } else {
            audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 16000, 2, AudioFormat.ENCODING_PCM_16BIT, 1280, AudioTrack.MODE_STREAM)
        }
        STATUS_STATE = STATUS_READY
    }

    fun startPlay() {
        if (STATUS_STATE == STATUS_START) {
            stopPlay()
        }
        if (STATUS_STATE == STATUS_NO_READY) {
            initAudio()
        }
        mExecutorService.execute {
            playAudio()
        }
        STATUS_STATE = STATUS_START
    }

    private fun playAudio() {
        var dis: DataInputStream? = null
        try {
            dis = DataInputStream(BufferedInputStream(FileInputStream(FILE_PATH)))
            val bytes = ByteArray(mBufferSizeInBytes)
            audioTrack?.play()
            var length = dis.read(bytes)
            while (length != -1) {
                audioTrack?.write(bytes, 0, length)
                length = dis.read(bytes)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            STATUS_STATE = STATUS_READY
        } finally {
            dis?.close()
        }

    }
    fun stopPlay() {
        if (STATUS_STATE == STATUS_NO_READY || STATUS_STATE == STATUS_READY) {
            throw IllegalAccessException("播放还没开始")
        } else {
            STATUS_STATE = STATUS_STOP
            audioTrack?.stop()
            audioTrack?.flush()
            audioTrack?.release()
            audioTrack = null
            STATUS_STATE = STATUS_NO_READY
        }
    }
}