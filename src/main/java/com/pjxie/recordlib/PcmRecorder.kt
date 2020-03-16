package com.pjxie.recordlib

import android.media.AudioFormat
import android.media.AudioRecord
import android.os.Environment
import android.util.Log
import java.io.File


/**
 * 创建者：pjxie
 * 创建日期：2019-05-23PcmRecorder
 * 邮箱：pjxie@iflytek.com
 * 描述：TODO
 */
class PcmRecorder(sampleRate: Int, audioSource: Int, byteSize: Int) : Thread() {
    var mSampleRate: Int
    var mByteSize: Int
    var mAudioSource: Int
    private var mRecorder: AudioRecord? = null
    var isRecording = false
    var isPauseing = false
    var mDataBuffer: ByteArray
    var mListener: RAMananger.RecordListener? = null
        set

    init {
        this.mSampleRate = sampleRate
        this.mAudioSource = audioSource
        this.mByteSize = byteSize
        mDataBuffer = ByteArray(mByteSize)
        val file = File(FILE_PATH)
        if (file.exists()) {
            file.delete()
        }
    }


    private fun initRecord() {
        try {
            val bufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRate,
                    12, AudioFormat.ENCODING_PCM_16BIT)
            mRecorder = AudioRecord(mAudioSource, mSampleRate, 12, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            mListener?.onError()
        }
    }

    fun startRecord() {
        Log.d("TAG", "开始录音")
        if (mRecorder != null) {
            release()
        }
        initRecord()
        isRecording = true
        mRecorder?.startRecording()
        this.start()
    }

    fun pauseRecord() {
        isPauseing = false
    }

    fun resumeRecord() {
        isPauseing = true
    }

    private fun byteArrayToShortArray(bytes: ByteArray): ShortArray {
        val count = bytes.size shr 1
        var dest = ShortArray(count)
        for (i in 0..count) {
            dest[i] = (bytes[i * 2].toInt() shl 8 or bytes[i * 2 + 1].toInt() and 0xff).toShort()
        }
        return dest

    }

    private fun calculateRealVolume(buffer: ShortArray): Int {

        var sum = 0.0
        val readSize = buffer.size

        for (i in 0 until readSize) {
            sum += (buffer[i] * buffer[i]).toDouble()
        }
        when (readSize > 0) {
            true -> {
                val amplitude = sum / readSize.toDouble()
                return Math.sqrt(amplitude).toInt()
            }
            false -> return 0
        }
    }

    override fun run() {
        super.run()
        while (isRecording) {
            if (isPauseing)
                return
            val count = mRecorder?.read(mDataBuffer, 0, mDataBuffer.size)
            mListener?.onBuffer(mDataBuffer, count!!, calculateRealVolume(byteArrayToShortArray(mDataBuffer)))
            Log.d("TAG", count.toString())
            saveFile(mDataBuffer, String.format("%s/%s", Environment.getExternalStorageDirectory().path, "record.pcm"), -1)
        }
    }


    @Synchronized
    fun release() {
        Log.d("TAG", "释放录音")
        isRecording = false
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
    }

}