package cn.leo.drawonline.net

import cn.leo.drawonline.MyApplication
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.constant.MsgCode
import cn.leo.drawonline.constant.MsgType
import cn.leo.nio_client.core.Client

/**
 * Created by Leo on 2018/3/26.
 */
class NetManager {
    /**
     * 获取消息对象
     */
    private fun getMsgBean(type: Int, code: Int = 0): MsgBean {
        val msgBean = MsgBean()
        msgBean.time = System.currentTimeMillis()
        msgBean.type = type
        msgBean.code = code
        return msgBean
    }

    /**
     * 发送json数据
     */
    private fun sendJsonMsg(json: String) {
        Client.sendMsg(json.toByteArray())
    }

    /**
     * 发送msgBean数据
     */
    private fun sendMsgBean(msgBean: MsgBean) {
        sendJsonMsg(msgBean.toString())
    }


    /**
     * 注册
     */
    fun reg(name: String, icon: Int) {
        val userBean = UserBean()
        userBean.userName = name
        userBean.icon = icon
        val msgBean = getMsgBean(MsgType.REG.getType())
        msgBean.msg = userBean.toString()
        sendMsgBean(msgBean)
    }

    /**
     * 修改昵称和头像
     */
    fun update(name: String, icon: Int) {
        val userBean = UserBean()
        userBean.userName = name
        userBean.icon = icon
        userBean.userId = MyApplication.getUser()?.userId!!
        val msgBean = getMsgBean(MsgType.EDIT.getType())
        msgBean.msg = userBean.toString()
        sendMsgBean(msgBean)
    }

    /**
     * 登录
     */
    fun login(name: String) {
        val msgBean = getMsgBean(MsgType.LOGIN.getType())
        msgBean.msg = name
        sendMsgBean(msgBean)
    }

    /**
     * 创建房间
     */

    fun createRoom() {
        val msgBean = getMsgBean(MsgType.GAME.getType(), MsgCode.ROOM_CREATE.code)
        sendMsgBean(msgBean)
    }

    /**
     * 加入房间
     */

    fun joinRoom(room: Int) {
        val msgBean = getMsgBean(MsgType.GAME.getType(), MsgCode.ROOM_JOIN.code)
        msgBean.msg = room.toString()
        sendMsgBean(msgBean)
    }

    /**
     * 获取房间列表
     */

    fun findRoom() {
        val msgBean = getMsgBean(MsgType.GAME.getType(), MsgCode.ROOM_LIST.code)
        sendMsgBean(msgBean)
    }

    /**
     * 退出房间
     */
    fun exitRoom() {
        val msgBean = getMsgBean(MsgType.GAME.getType(), MsgCode.ROOM_EXIT.code)
        sendMsgBean(msgBean)
    }

    /**
     * 开始游戏
     */
    fun startGame() {

    }

    /**
     * 退出游戏
     */
    fun stopGame() {

    }

    /**
     * 发送心跳
     */
    fun heart() {
        val msgBean = getMsgBean(MsgType.SYS.getType(), MsgCode.HEART.code)
        sendMsgBean(msgBean)
    }

}