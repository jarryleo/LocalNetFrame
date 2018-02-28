package cn.leo.localnetframe

import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //assertEquals(4, 2 + 2)
        val room = Room()
        room.id = "1"
        room.addUser(User("192.168.1.1", "张三"))
        room.addUser(User("192.168.1.2", "李思"))
        println(room)
    }
}
