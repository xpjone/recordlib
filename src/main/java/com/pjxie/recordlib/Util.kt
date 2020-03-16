package com.pjxie.recordlib

import android.os.Environment
import java.io.File
import java.io.RandomAccessFile

/**
 * 创建者：pjxie
 * 创建日期：2019-05-23UTIL
 * 邮箱：pjxie@iflytek.com
 * 描述：TODO
 */
val STATUS_NO_READY = 0
val STATUS_READY = 1
val STATUS_START = 2
val STATUS_STOP = 3
val REQUEST_CODE = 0x1001

var FILE_PATH = Environment.getExternalStorageDirectory().path + "/record.pcm"
fun saveFile(buffer: ByteArray, filename: String, writeoffset: Int): Boolean {
    var ret = false
    var out: RandomAccessFile? = null
    val file = File(filename)
    try {

        if (!file.exists()) {
            file.createNewFile()
        }
        out = RandomAccessFile(filename, "rw")
        if (writeoffset < 0) {
            out.seek(out.length())
        } else {
            out.seek(writeoffset.toLong())
        }

        out.write(buffer)
        ret = true
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (out != null) {
                out.close()
            }
        } catch (var15: Exception) {
        }

    }

    return ret
}
