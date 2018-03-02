package cn.leo.localnetframe.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.MsgListAdapter
import cn.leo.localnetframe.bean.Msg
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.net.NetManager
import cn.leo.localnetframe.utils.WordChooser
import cn.leo.localnetframe.view.DrawBoard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_paint.*

class PaintActivity : AppCompatActivity(), DrawBoard.OnDrawListener, NetManager.OnMsgArrivedListener {

    private var chatAdapter: MsgListAdapter? = null
    private var userAdapter: MsgListAdapter? = null
    private var wordChooser: WordChooser? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var netManager: NetManager
    private var word: String = "测试"
    private var isMePaint: Boolean = false
    private var showAnswerDialog: AlertDialog? = null
    private var rightUsers = ArrayList<User>() //答对的人

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        drawBoard.onDrawListener = this
        netManager = MyApplication.getNetManager(this)
        initView()
        initData()
    }

    private fun initData() {
        wordChooser = WordChooser(this)
        refreshUsers()
    }
    //倒计时
    private fun countDown(){
        countDownTimer = object : CountDownTimer(75 * 1000, 1000) {
            override fun onFinish() {
                nextPlayer()
            }

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                if (sec in 40L..70L) {
                    //发送第一个提示，几个字
                    netManager.sendData("T${word.length}个字")
                }
                if (sec in 5L..40L) {
                    //发送第二个提示
                    netManager.sendData("T${word.length}个字,${wordChooser?.getTips()}")
                }
                if (sec == 5L) {
                    netManager.sendData("A" + word)
                }
                //倒计时
                val time = sec - 5
                //预留5秒显示答案
                if (time >= 0L) {
                    showCountDown(time.toString())
                    //同步倒计时
                    netManager.sendData("D" + time)
                    if (time == 0L) {
                        //公布结果
                        showAnswer(word)
                    }
                }
            }
        }
        countDownTimer?.start()
    }

    /**
     *游戏绘画全力转移到下一个玩家
     */
    private fun nextPlayer() {
        tvTitle.text = resources.getText(R.string.app_name)
        isMePaint = false
        countDownTimer?.cancel()
        rightUsers.clear()
        netManager.nextPainter()
        hideAnswer()
        drawBoard.clear()
        checkPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        netManager.stopGame()
    }

    //检查现在是谁在作画
    private fun checkPlayer() {
        if (!isMePaint && netManager.isMePlaying()) {
            //轮到我画画
            isMePaint = netManager.isMePlaying()
            //准备我画画的工作
            //获取要画的词汇
            word = wordChooser?.chooseWord()?.getWord()!!
            //展示词汇
            tvTitle.text = word
            //开始倒计时
            countDown()
            //通知其他人清空上次画画的内容,并同步倒计时
            netManager.sendData("P")
        }
        drawBoard.lock = !netManager.isMePlaying()
    }

    private fun initView() {
        chatAdapter = MsgListAdapter()
        userAdapter = MsgListAdapter()
        rvMsgList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvUserScoreList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMsgList.adapter = chatAdapter
        rvUserScoreList.adapter = userAdapter
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
                netManager.sendData("C" + msg.toString())
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
        checkPlayer()
    }

    /**
     * 显示消息
     */
    private fun showMsg(msg: Msg) {
        chatAdapter?.addData(msg)
        rvMsgList.smoothScrollToPosition(chatAdapter?.itemCount!!)
    }

    /**
     * 显示答案
     */
    private fun showAnswer(answer: String) {
        if (showAnswerDialog == null) {
            showAnswerDialog = AlertDialog.Builder(this)
                    .setTitle("答案是：")
                    .setMessage(answer)
                    .show()
        } else {
            showAnswerDialog?.setMessage(answer)
            showAnswerDialog?.show()
        }
    }

    /**
     * 隐藏答案
     */
    private fun hideAnswer() {
        if (showAnswerDialog != null) {
            if (showAnswerDialog?.isShowing!!) {
                showAnswerDialog?.hide()
            }
        }
    }

    /**
     * 显示提示信息
     */
    private fun showTips(tips: String) {
        tvTitle.text = tips
    }

    /**
     * 显示倒计时
     */
    private fun showCountDown(time: String) {
        tvTimer.text = time
    }

    //发送画板数据
    override fun onDraw(code: String) {
        netManager.sendData("P" + code)
    }

    //接收数据
    override fun onMsgArrived(data: String, host: String) {
        when (data.first()) {
            'C' -> {
                //聊天数据
                val msg = data.substring(1)
                val msgBean = Gson().fromJson<Msg>(msg, Msg::class.java)
                var allRight = false
                //处理聊天数据
                if (netManager.isMePlaying()) {
                    //我在画画，负责判断答案，然后转发聊天信息，是答案的话，掩盖后载转发
                    if (msgBean.msg == word) {
                        msgBean.msg = "猜对了！"
                        msgBean.isAnswer = true
                        //给答对的人加分，并把分数共享给其他人
                        val user = netManager.getSendMsgUser(host)!!
                        user.score += 1
                        netManager.sendData("U" + user.toString())
                        refreshUsers()
                        //统计答对人数
                        if (!rightUsers.contains(user)) {
                            rightUsers.add(user)
                            if (rightUsers.size >= netManager.getRoomUsers().size - 1) {
                                //每个人都答对了下一个玩家开始游戏
                                allRight = true
                            }
                        }
                    }
                    //转发聊天信息
                    netManager.sendData("C" + msgBean.toString())
                }
                //展示聊天内容
                showMsg(msgBean)
                //下一个玩家开始游戏
                if(allRight){
                    nextPlayer()
                }
            }
            'P' -> {
                //画画数据
                if (!netManager.isMePlaying()) {
                    drawBoard.setBitmapCode(data.substring(1))
                }
            }
            'E', 'U' -> {
                //退出，同步积分，下一个人开始游戏
                refreshUsers()
            }
            'N' -> {
                hideAnswer()
                refreshUsers()
                drawBoard.clear()
            }
            'A' -> {
                //显示答案
                showAnswer(data.substring(1))
            }
            'T' -> {
                //提示内容
                showTips(data.substring(1))
            }
            'D' -> {
                //倒计时
                showCountDown(data.substring(1))
            }
            else -> {

            }
        }
    }
}
