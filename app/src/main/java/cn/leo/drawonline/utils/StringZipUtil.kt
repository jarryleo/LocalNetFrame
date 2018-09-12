package cn.leo.localnet.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Created by Leo on 2018/2/26.
 */
object StringZipUtil {
    // 压缩
    @Throws(IOException::class)
    fun compress(str: String): ByteArray {
        val out = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(out)
        gzip.write(str.toByteArray())
        gzip.close()
        return out.toByteArray()
    }

    // 解压缩
    @Throws(IOException::class)
    fun uncompress(byteArray: ByteArray): String {
        val bos = ByteArrayOutputStream()
        val bis = ByteArrayInputStream(byteArray)
        val gunZip = GZIPInputStream(bis)
        val buffer = ByteArray(256)
        var n: Int = gunZip.read(buffer)
        while (n >= 0) {
            bos.write(buffer, 0, n)
            n = gunZip.read(buffer)
        }
        return bos.toString()
    }
}