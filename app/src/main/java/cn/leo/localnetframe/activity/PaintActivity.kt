package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.MsgListAdapter
import cn.leo.localnetframe.bean.Msg
import cn.leo.localnetframe.net.NetManager
import cn.leo.localnetframe.view.DrawBoard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_paint.*

class PaintActivity : AppCompatActivity(), DrawBoard.OnDrawListener, NetManager.OnMsgArrivedListener {

    private var chatAdapter: MsgListAdapter? = null
    private var userAdapter: MsgListAdapter? = null
    private lateinit var netManager: NetManager
    private var word: String = "测试"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        drawBoard.onDrawListener = this
        netManager = MyApplication.getNetManager(this)
        initView()
        checkPlayer()
    }

    //检查谁在作画
    private fun checkPlayer() {
        drawBoard.lock = !netManager.isMePlaying()
    }

    private fun initView() {
        chatAdapter = MsgListAdapter()
        userAdapter = MsgListAdapter()
        rvMsgList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvUserScoreList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMsgList.adapter = chatAdapter
        rvUserScoreList.adapter = userAdapter
        refreshUsers()
        //发送消息按钮
        btnSendMsg.setOnClickListener {
            sendMsgClick()
        }
    }

    //点击发送消息执行逻辑
    private fun sendMsgClick() {
        val str = etMsg.text.trim().toString()
        if (str.isEmpty()) {
            ToastUtilK.show(this, "不能发送空的消息")
        } else {
            //获取输入的聊天信息
            val msg = Msg(netManager.getMeName(), str)
            //清空输入框
            etMsg.setText("")
            //如果是我在画画，把信息发给其他人，自己添加到聊天栏
            if (netManager.isMePlaying()) {
                showMsg(msg)
                netManager.sendData(("C" + msg.toString()).toByteArray())
            } else {
                //如果是别人在画画，把消息发给画画的人
                netManager.sendToPainter(("C" + msg.toString()).toByteArray())
            }
        }
    }

    //刷新用户分数
    private fun refreshUsers() {
        userAdapter?.clearData()
        netManager.getRoomUsers()?.forEach {
            val score = Msg(it.name, it.score.toString())
            userAdapter?.addData(score)
        }
    }

    /**
     * 显示消息
     */
    private fun showMsg(msg: Msg) {
        chatAdapter?.addData(msg)
        rvMsgList.smoothScrollToPosition(chatAdapter?.itemCount!!)
    }

    //发送数据
    override fun onDraw(code: String) {
        netManager.sendData(("P" + code).toByteArray())
    }

    //接收数据
    override fun onMsgArrived(data: String) {
        when (data.first()) {
            'C' -> {
                //聊天数据
                val msg = data.substring(1)
                val msgBean = Gson().fromJson<Msg>(msg, Msg::class.java)
                //处理聊天数据
                if (netManager.isMePlaying()) {
                    //我在画画，负责判断答案，然后转发聊天信息，是答案的话，掩盖后载转发
                    if (msgBean.msg == word) {
                        msgBean.msg = "猜对了！"
                        msgBean.isAnswer = true
                        //给答对的人加分，并把分数共享给其他人 TODO

                    }
                    //转发聊天信息
                    netManager.sendData(("C" + msgBean.toString()).toByteArray())
                } else {
                    //直接展示聊天内容
                    showMsg(msgBean)
                }
            }
            'P' -> {
                //画画数据
                if (!netManager.isMePlaying()) {
                    drawBoard.setBitmapCode(data)
                }
            }
            'E' -> {
                refreshUsers()
            }
            else -> {

            }
        }

    }
}
