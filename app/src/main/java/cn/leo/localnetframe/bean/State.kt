package cn.leo.localnetframe.bean

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Leo on 2018/3/14.
 */
const val STATE_ONLINE = 1L
const val STATE_OFFLINE = 2L

@IntDef(STATE_ONLINE, STATE_OFFLINE)
@Retention(RetentionPolicy.SOURCE)
annotation class State