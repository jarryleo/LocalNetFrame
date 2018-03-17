package cn.leo.localnetframe.bean

import android.support.annotation.IntDef

/**
 * Created by Leo on 2018/3/14.
 */
const val STATE_ONLINE = 1L
const val STATE_OFFLINE = 2L

@IntDef(STATE_ONLINE, STATE_OFFLINE)
@Retention(AnnotationRetention.SOURCE)
annotation class State