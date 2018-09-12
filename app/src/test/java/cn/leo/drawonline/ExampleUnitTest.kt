package cn.leo.drawonline

import cn.leo.localnet.utils.StringZipUtil
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val s = "jdsafhdaghjdaflfhjd"
        val compress = StringZipUtil.compress(s)
        println("${s.toByteArray().size}  ${compress.size} ")
        println(Arrays.toString(compress))
        val uncompress = StringZipUtil.uncompress(compress)
        println(uncompress)
    }
}
