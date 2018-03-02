package cn.leo.localnetframe.utils

import android.content.Context
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Leo on 2018/3/2.
 */
class WordChooser(val context: Context) {
    lateinit var words: List<String>
    lateinit var mWord: String

    init {
        loadFile()
    }

    private fun loadFile() {
        val open = context.resources.assets.open("words")
        val inputStreamReader = InputStreamReader(open)
        words = inputStreamReader.readLines()
        chooseWord()
    }

    /**
     * 随机选择一个词汇
     */
    fun chooseWord() = apply { mWord = words[Random().nextInt(words.size)] }

    /**
     * 获取词汇
     */
    fun getWord(): String = mWord.split(",")[0]

    /**
     * 获取描述
     */
    fun getTips(): String = mWord.split(",")[1]

}