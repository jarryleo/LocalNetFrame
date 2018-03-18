package cn.leo.localnetframe.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.MsgListAdapter
import cn.leo.localnetframe.adapter.ScoreListAdapter
import cn.leo.localnetframe.bean.Msg
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.net.NetInterFace
import cn.leo.localnetframe.utils.WordChooser
import cn.leo.localnetframe.view.ColorCircle
import cn.leo.localnetframe.view.DrawBoard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.layout_color_palette.*

class PaintActivity : AppCompatActivity(), DrawBoard.OnDrawListener, ColorCircle.OnColorClickListener {

    private lateinit var netManager: NetImpl
    private val dataReceiver = DataReceiver()
    private var widthPixels: Int = 0
    private var density: Float = 0f
    private var chatAdapter: MsgListAdapter? = null
    private var userAdapter: ScoreListAdapter? = null
    private var wordChooser: WordChooser? = null
    private var countDownTimer: CountDownTimer? = null
    private var word: String = "测试"
    private var isMePaint: Boolean = false
    private var showAnswerDialog: AlertDialog? = null
    private var rightUsers = ArrayList<User>() //答对的人
    private val handler = Handler()
    private lateinit var heartTask: Runnable

    init {
        //定时任务，检测画画的人是否掉线
        heartTask = Runnable {
            refreshUsers()
            if (!isMePaint &&
                    netManager.getPainter().isOffline() &&
                    netManager.meIsNextPainter()) {
                netManager.getRoom().countDownTime--
                val time = netManager.getRoom().countDownTime.toString()
                showCountDown(time)
                netManager.sendMsgOther("D$time")
                countToDo(time.toLong())
            }
            handler.removeCallbacks(heartTask)
            handler.postDelayed(heartTask, 1000)
        }
        handler.postDelayed(heartTask, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        drawBoard.onDrawListener = this
        netManager = MyApplication.getNetManager(dataReceiver)
        initView()
        initData()
    }

    private fun initData() {
        wordChooser = WordChooser(this)
        refreshUsers()
        checkPlayer()
    }

    //倒计时
    private fun countDown() {
        countDownTimer = object : CountDownTimer(
                netManager.getRoom().countDownTime * 1000L, 1000) {
            override fun onFinish() {
                nextPlayer()
            }

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                countToDo(sec)
            }
        }
        countDownTimer?.start()
    }

    private fun countToDo(sec: Long) {
        when (sec) {
            in 56L..Long.MAX_VALUE -> {
                //发送第一个提示，几个字
                netManager.sendMsgOther("T${word.length}个字")
            }
            in 6L..55L -> {
                //发送第二个提示
                netManager.sendMsgOther("T${word.length}个字,${wordChooser?.getTips()}")
            }
            5L -> {
                //发送答案
                netManager.sendMsgOther("A$word")
            }
        }
        //展示词汇
        tvTitle.text = word
        //倒计时
        val time = sec - 5
        //预留5秒显示答案
        if (time >= 0L) {
            showCountDown(time.toString())
            //同步倒计时
            netManager.sendMsgOther("D$time")
            if (time == 0L) {
                //公布结果
                showAnswer(word)
            }
        }
    }

    /**
     *游戏绘画全力转移到下一个玩家
     */
    private fun nextPlayer() {
        tvTitle.text = resources.getText(R.string.app_name)
        isMePaint = false
        rightUsers.clear()
        countDownTimer?.cancel()
        netManager.nextPainter()
        hideAnswer()
        checkPlayer()
        refreshUsers()
        drawBoard.clear()
        hideColorLens()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(heartTask)
        countDownTimer?.cancel()
        netManager.stopGame()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    //检查现在是谁在作画
    private fun checkPlayer() {
        Log.e("下一个", "----" + isMePaint + netManager.meIsNextPainter())
        if (!isMePaint && netManager.meIsPainter()) {
            //轮到我画画
            isMePaint = true
            //准备我画画的工作
            //获取要画的词汇
            word = wordChooser?.chooseWord()?.getWord()!!
            //展示词汇
            tvTitle.text = word
            //清空帮忙记录的答对人数
            rightUsers.clear()
            //房间倒计时复位
            netManager.getRoom().countDownTime = 85
            //开始倒计时
            countDown()
            //通知其他人清空上次画画的内容,并同步倒计时
            //暗中传递答案，再画画的人掉线后，接管的人能控制倒计时和答案
            netManager.sendMsgOther("S$word")
            //提示我开始画画
            ToastUtilK.show(this, "轮到我开始画画了")
        }
        drawBoard.lock = !isMePaint
    }

    private fun initView() {
        //保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //初始聊天信息和玩家列表
        chatAdapter = MsgListAdapter()
        userAdapter = ScoreListAdapter(netManager)
        rvMsgList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        rvUserScoreList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        rvMsgList.adapter = chatAdapter
        rvUserScoreList.adapter = userAdapter
        //发送消息按钮
        btnSendMsg.setOnClickListener {
            sendMsgClick()
        }

        //调色板位置
        widthPixels = resources.displayMetrics.widthPixels
        density = resources.displayMetrics.density
        hideColorLens()
        btnColorLens.setOnClickListener {
            if (netManager.meIsPainter()) {
                if (llColorLens.x + llColorLens.width <= widthPixels) {
                    hideColorLens()
                } else {
                    showColorLens()
                }
            }
        }
        //调色板按钮点击事件
        click(cc1, cc2, cc3, cc4, cc5, cc6, cc7, cc8, cc9, cc10, cc11, cc12)
        btnUndo.setOnClickListener { drawBoard.undo() }
        btnUndo.setOnLongClickListener { drawBoard.clear(); true }
        cc01.setOnClickListener { drawBoard.setStrokeWidth(3f);hideColorLens() }
        cc02.setOnClickListener { drawBoard.setStrokeWidth(5f);hideColorLens() }
        cc03.setOnClickListener { drawBoard.setStrokeWidth(7f);hideColorLens() }
    }

    //批量设置点击事件
    private fun click(vararg views: ColorCircle) {
        views.forEach { it.setOnColorClickListener(this) }
    }

    //点击颜色
    override fun onColorClick(color: Int) {
        drawBoard.setColor(color)
        hideColorLens()
    }

    //显示调色板
    private fun showColorLens() {
        ViewCompat
                .animate(llColorLens)
                .setDuration(200)
                .translationX((widthPixels - llColorLens.width).toFloat())
                .setInterpolator(AccelerateInterpolator())
                .start()
    }


    //隐藏调色板
    private fun hideColorLens() {
        ViewCompat
                .animate(llColorLens)
                .setDuration(200)
                .translationX(widthPixels - (40 * density))
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    //点击发送消息执行逻辑
    private fun sendMsgClick() {
        val str = etMsg.text.trim().toString()
        if (str.isEmpty()) {
            ToastUtilK.show(this, "不能发送空的消息")
        } else {
            //获取输入的聊天信息
            val msgBean = Msg(netManager.getMeName(), str)
            //清空输入框
            etMsg.setText("")
            //如果是我在画画，把信息发给其他人，自己添加到聊天栏
            if (netManager.meIsPainter()) {
                //如果画画的人想泄题，把含有题目文字打码
                word.forEach { msgBean.msg = msgBean.msg.replace(it.toString(), "**") }
                //转发聊天信息
                netManager.sendMsgOther("C$msgBean")
                showMsg(msgBean)
            } else {
                if ((netManager.getPainter().isOffline()
                        && netManager.meIsNextPainter())) {
                    //获取答题人
                    val user = netManager.getMe()
                    checkAnswer(msgBean, user)
                } else {
                    //如果是别人在画画，把消息发给画画的人
                    netManager.sendMsgPainter("C$msgBean")
                }
            }
        }
    }

    //检查聊天的答案
    private fun checkAnswer(msgBean: Msg, user: User) {
        var allRight = false
        if (msgBean.msg == word) {
            msgBean.msg = "猜对了！"
            msgBean.isAnswer = true
            //统计答对人数
            if (!rightUsers.contains(user)) {
                //给第一次答对的人加分
                user.score += 1
                rightUsers.add(user)
                if (rightUsers.size >= netManager.getRoomUsers().size - 1) {
                    //每个人都答对了下一个玩家开始游戏
                    allRight = true
                }
            }
            //发送分数
            netManager.sendMsgOther("U$user")
            refreshUsers()
        }
        //转发聊天信息
        netManager.sendMsgOther("C$msgBean")
        showMsg(msgBean)
        //下一个玩家开始游戏
        if (allRight) {
            nextPlayer()
        }
    }

    //刷新用户分数
    private fun refreshUsers() {
        userAdapter?.clearData()
        netManager.getRoomUsers()?.forEach {
            userAdapter?.addData(it)
        }
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
                    .setCancelable(false)
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
        netManager.sendMsgOther("P$code")
    }

    /**
     * 接受到数据
     */
    inner class DataReceiver : NetInterFace.OnDataArrivedListener() {
        override fun onChat(pre: Char, msg: String, host: String) {
            //聊天数据
            val msgBean = Gson().fromJson<Msg>(msg, Msg::class.java)
            //获取答题人
            val user = netManager.getSendMsgUser(host)!!
            //处理聊天数据
            if (netManager.meIsPainter()) {
                //如果我是画画的人，检测答案
                checkAnswer(msgBean, user)
            } else {
                //展示聊天内容
                showMsg(msgBean)
            }
        }

        override fun onPaint(pre: Char, msg: String, host: String) {
            //画画数据
            if (!netManager.meIsPainter()) {
                drawBoard.setBitmapCode(msg)
            }
        }

        override fun onExitRoom(pre: Char, msg: String, host: String) {
            refreshUsers()
        }

        override fun onJoinRoom(pre: Char, msg: String, host: String) {
            refreshUsers()
            handler.postDelayed({
                //刚加入房间的人发送一副画板图给他
                netManager.sendData("P${drawBoard.getDrawCode()}", host)
            }, 3000)

        }

        override fun onUpdateScore(pre: Char, msg: String, host: String) {
            refreshUsers()
            val user = Gson().fromJson<User>(msg, User::class.java)
            rightUsers.add(user)
        }

        override fun onNextPainter(pre: Char, msg: String, host: String) {
            hideAnswer()
            refreshUsers()
            drawBoard.clear()
            checkPlayer()
        }

        override fun onShowAnswer(pre: Char, msg: String, host: String) {
            //显示答案
            showAnswer(msg)
        }

        override fun onShowTips(pre: Char, msg: String, host: String) {
            //提示内容
            showTips(msg)
        }

        override fun onCountDown(pre: Char, msg: String, host: String) {
            //倒计时
            showCountDown(msg)
        }

        override fun onStartGame(pre: Char, msg: String, host: String) {
            if (!msg.isEmpty()) {
                word = msg
                drawBoard.clear()
            }
        }
    }
}
