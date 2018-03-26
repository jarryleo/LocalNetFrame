package cn.leo.drawonline.net

import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.bean.RoomBean
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.constant.MsgType
import cn.leo.nio_client.core.Client

/**
 * Created by Leo on 2018/3/26.
 */
class NetManager {
    /**
     * 注册
     */
    fun reg(name: String, icon: Int) {
        val userBean = UserBean()
        userBean.userName = name
        userBean.icon = icon
        val msgBean = MsgBean()
        msgBean.time = System.currentTimeMillis()
        msgBean.type = MsgType.REG
        msgBean.msg = userBean.toString()
        Client.sendMsg(msgBean.toString().toByteArray())
    }

    /**
     * 登录
     */
    fun login(name: String) {
        val userBean = UserBean()
        userBean.userName = name
        val msgBean = MsgBean()
        msgBean.time = System.currentTimeMillis()
        msgBean.type = MsgType.LOGIN
        msgBean.msg = userBean.toString()
        Client.sendMsg(msgBean.toString().toByteArray())
    }

    /**
     * 创建房间
     */

    fun createRoom() {

    }

    /**
     * 加入房间
     */

    fun joinRoom(room: RoomBean) {

    }

    /**
     * 获取房间列表
     */

    fun findRoom() {

    }

    /**
     * 退出房间
     */
    fun exitRoom() {

    }

    /**
     * 退出游戏
     */
    fun stopGame() {

    }
}