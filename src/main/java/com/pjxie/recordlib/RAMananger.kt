package com.pjxie.recordlib

import android.app.Activity
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

/**
 * 创建者：pjxie
 * 创建日期：2019-05-23RecordMananger
 * 邮箱：pjxie@iflytek.com
 * 描述：TODO
 */

class RAMananger private constructor() : RecordInterface {


    @RequiresApi(Build.VERSION_CODES.M)
    override fun requestpermission(activity: Activity) {
        activity.requestPermissions(arrayOf("Manifest.permission.RECORD_AUDIO"), REQUEST_CODE)
    }


    private var pcmRecorder: PcmRecorder? = null
    private var pcmPlayer: PcmPlayer? = null

    companion object {
        val instance: RAMananger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RAMananger()
        }
            get
    }


    override fun startRecord() {
        pcmRecorder?.startRecord()
    }

    override fun stopRecord() {
        pcmRecorder?.release()
    }

    override fun init(context: Context) {
        init(context, FILE_PATH)
    }

    override fun init(context: Context, path: String) {
        FILE_PATH = path
        pcmRecorder = PcmRecorder(16000, MediaRecorder.AudioSource.MIC
                , 1280)
        pcmPlayer = PcmPlayer()
    }

    override fun isRecord(): Boolean {
        return pcmRecorder?.isRecording!!
    }

    override fun isPauseRecord(): Boolean {
        return pcmRecorder?.isPauseing!!
    }

    override fun pauseRecord() {
        pcmRecorder?.pauseRecord()
    }

    override fun resumeRecord() {
        pcmRecorder?.resumeRecord()
    }

    override fun startPlay() {
        pcmPlayer?.startPlay()
    }

    override fun stopPlay() {
        pcmPlayer?.stopPlay()
    }

    override fun clearCache() {
        val f = File(FILE_PATH)
        if (f.exists()) {
            f.delete()
        }
    }

    interface RecordListener {
        fun onBuffer(byteArray: ByteArray, i: Int, volume: Int)
        fun onError()
    }


}