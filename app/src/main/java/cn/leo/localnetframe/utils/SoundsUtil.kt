package cn.leo.localnetframe.utils

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import cn.leo.localnetframe.R
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Leo on 2018/3/19.
 */
class SoundsUtil(private val context: Context) {
    private val sp = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
    private val sounds = ConcurrentHashMap<Int, Int>()

    init {
        val sounds = arrayListOf(R.raw.aoao, R.raw.gogogo, R.raw.pa, R.raw.right)
        sounds.forEach { loadSoundRsID(it) }
    }

    /**
     * 提前加载声音资源，在第一次播放的时候就不会延迟
     *
     * @param rsID
     */
    private fun loadSoundRsID(rsID: Int) {
        val id = sp.load(context, rsID, 1)
        sounds[rsID] = id
    }

    /**
     * 播放声音无参 （若无提前加载声音资源，第一次播放会延迟）
     *
     * @param rsID 资源ID
     */
    fun playSound(rsID: Int, loop: Boolean) {
        val l = if (loop) -1 else 0
        playSound(rsID, 1.0f, 1.0f, 0, l, 1.0f)
    }

    /**
     * 带参数的声音播放方法
     *
     * @param rsID        资源id
     * @param leftVolume  左声道音量大小 0.0f - 1.0f
     * @param rightVolume 右声道音量大小 0.0f - 1.0f
     * @param priority    优先级 默认0
     * @param loop        循环播放-1 不循环0
     * @param rate        声音倍速 0.5f - 2.0f ,正常倍速 1.0f
     */
    private fun playSound(rsID: Int, leftVolume: Float, rightVolume: Float,
                          priority: Int, loop: Int, rate: Float) {
        if (sounds.containsKey(rsID)) {
            val playId = sp.play(sounds[rsID]!!, leftVolume, rightVolume, priority, loop, rate)
        } else {
            val id = sp.load(context, rsID, 1)
            sounds[rsID] = id
            sp.setOnLoadCompleteListener({ soundPool, _, _ ->
                val playId = soundPool.play(id, leftVolume, rightVolume, priority, loop, rate)
                sp.setOnLoadCompleteListener(null)
            })
        }
    }
}