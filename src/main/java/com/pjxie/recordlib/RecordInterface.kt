package com.pjxie.recordlib

import android.app.Activity
import android.content.Context

/**
 * 创建者：pjxie
 * 创建日期：2019-05-23RecordInterface
 * 邮箱：pjxie@iflytek.com
 * 描述：TODO
 */
interface RecordInterface {

    fun init(context: Context)

    fun init(context: Context, filePath: String)

    fun startRecord()

    fun stopRecord()

    fun isRecord(): Boolean

    fun isPauseRecord(): Boolean

    fun pauseRecord()

    fun resumeRecord()

    fun startPlay()

    fun stopPlay()

    fun clearCache()

    fun requestpermission(activity: Activity)
}