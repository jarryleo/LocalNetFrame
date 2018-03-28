package cn.leo.drawonline.constant

enum class MsgCode constructor(//丢拖鞋
        val code: Int) {
    HEART(0), //心跳
    REG_SUC(1000),//注册成功
    REG_FAI(1001),//注册失败
    LOG_SUC(2000),//登录成功
    LOG_FAI(2001),//登录失败
    EDIT_SUC(3000),//修改成功
    EDIT_FAI(3001),//修改失败
    ROOM_LIST(5000),//房间列表
    ROOM_INFO(5001),//房间信息
    ROOM_JOIN(5002),//加入房间
    ROOM_EXIT(5003),//退出房间
    ROOM_CREATE(5100),//创建房间
    ROOM_CREATE_SUC(5101),//创建房间成功
    ROOM_CREATE_FAIL(5102),//创建房间失败
    ROOM_JOIN_SUC(5200),//加入房间成功
    ROOM_JOIN_FAI(5201),//加入房间失败
    GAME_START(6000),//游戏开始
    GAME_START_SUC(6001),//游戏开始成功
    GAME_START_FAIL(6002),//游戏开始失败
    GAME_EXIT(6001),//退出游戏
    GAME_ANSWER(6003),//显示答案
    GAME_TIPS(6004),//显示提示
    GAME_TIMER(6005),//显示倒计时
    GAME_CHAT(6006),//显示聊天信息、答案
    GAME_GIFT_FLOWER(6100),//送花
    GAME_GIFT_SLIPPER(6101)//丢拖鞋
}
