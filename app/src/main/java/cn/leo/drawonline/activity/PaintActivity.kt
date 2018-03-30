package cn.leo.drawonline.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import cn.leo.drawonline.R
import cn.leo.drawonline.adapter.MsgListAdapter
import cn.leo.drawonline.adapter.ScoreListAdapter
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.bean.RoomBean
import cn.leo.drawonline.constant.MsgCode
import cn.leo.drawonline.constant.MsgType
import cn.leo.drawonline.net.NetManager
import cn.leo.drawonline.utils.Config
import cn.leo.drawonline.utils.SoundsUtil
import cn.leo.drawonline.utils.get
import cn.leo.drawonline.utils.put
import cn.leo.drawonline.view.*
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.nio_client.core.ClientListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.layout_color_palette.*
import java.nio.charset.Charset

class PaintActivity : AppCompatActivity(),
        DrawBoard.OnDrawListener,
        ColorCircle.OnColorClickListener,
        AnswerDialog.OnOpinionClickListener,
        ClientListener {

    private val netManager = NetManager()
    private var widthPixels: Int = 0
    private var density: Float = 0f
    private var chatAdapter: MsgListAdapter? = null
    private var userAdapter: ScoreListAdapter? = null
    private var countDownTimer: CountDownTimer? = null
    private var word: String = "测试"
    private var tips: String = "提示"
    private var isMePaint: Boolean = false
    private val handler = Handler()
    private var popupTips: PopTips? = null
    private var answerDialog: AnswerDialog = AnswerDialog()
    private var soundsUtil: SoundsUtil? = null
    private var meName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        drawBoard.onDrawListener = this
        initView()
        Looper.myQueue().addIdleHandler { initData(); false }
    }

    private fun initView() {
        //保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //初始聊天信息和玩家列表
        chatAdapter = MsgListAdapter()
        userAdapter = ScoreListAdapter()
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
            if (isMePaint) {
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
        //聊天气泡
        popupTips = PopTips(this)
        //显示答案界面
        answerDialog.setOnOpinionClickListenrt(this)
    }

    //送鲜花
    override fun onFLower() {
        netManager.sendFlower()
        showGift(1)
    }

    //丢拖鞋
    override fun onSlipper() {
        netManager.sendSlipper()
        showGift(2)
    }

    private fun initData() {
        meName = get("nickname", "")
        soundsUtil = SoundsUtil(this)
        refreshUsers()
    }

    //倒计时
    private fun countDown(countTime: Int) {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        countDownTimer = object : CountDownTimer(
                countTime * 1000L, 1000) {
            override fun onFinish() {
                showCountDown("0")
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
        var tips = "${word.length}个字"
        when (sec) {
            in 56L..Long.MAX_VALUE -> {
                //发送第一个提示，几个字
                tips = "${word.length}个字"
            }
            in 6L..55L -> {
                //发送第二个提示
                tips = "${word.length}个字,${this.tips}"
            }
            5L -> {
                //发送答案
                tips = word
            }
        }
        if (isMePaint) {
            //展示词汇
            tvTitle.text = word
        } else {
            //展示提示
            tvTitle.text = tips
        }
        //倒计时
        val time = sec - 5
        //预留5秒显示答案
        if (time >= 0L) {
            showCountDown(time.toString())
            if (time == 0L) {
                if (isMePaint) {
                    //公布结果
                    drawBoard.lock = true
                    soundsUtil?.playSound(R.raw.aoao, false)
                    ToastUtilK.show(this, "时间到,5秒后下一个人开始画画")
                } else {
                    showAnswer(word)
                }
            }
        } else {
            showCountDown(sec.toString())
        }
    }

    //下一个开始复位数据
    private fun nextPlayer() {
        tvTitle.text = resources.getText(R.string.app_name)
        countDownTimer?.cancel()
        hideAnswer()
        refreshUsers()
        drawBoard.clear()
        hideColorLens()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        countDownTimer?.cancel()
        netManager.stopGame()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    //检查是不是自己作画
    private fun checkPlayer() {
        if (isMePaint) {
            //提示我开始画画
            ToastUtilK.show(this, "轮到我开始画画了")
            //播放提示音
            soundsUtil?.playSound(R.raw.gogogo, false)
        }
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
            //发送聊天信息
            netManager.sendChat(str)
            //清空输入框
            etMsg.setText("")
        }
    }

    //检查聊天的答案
    private fun checkAnswer(msgBean: MsgBean) {
        //我答对了声音
        if (msgBean.code == 100 && msgBean.senderName == meName) {
            soundsUtil?.playSound(R.raw.right, false)
            val score = get(Config.SCORE, 0)
            put(Config.SCORE, score + 1)
        }
        allPlayerAnswerRight(msgBean)
    }

    //全部玩家答对
    private fun allPlayerAnswerRight(msgBean: MsgBean) {
        if (msgBean.id != 200) {
            return
        }
        if (isMePaint) {
            //公布结果
            drawBoard.lock = true
            soundsUtil?.playSound(R.raw.aoao, false)
            ToastUtilK.show(this, "全部人答对了,5秒后下一个人开始画画")
            countDownTimer?.cancel()
        } else {
            showAnswer(word)
        }
        //handler.postDelayed({ nextPlayer() }, 5000)
    }

    //刷新用户分数
    private fun refreshUsers() {
        netManager.getRoomInfo()
    }

    //显示消息
    private fun showMsg(msg: MsgBean) {
        chatAdapter?.addData(msg)
        rvMsgList.smoothScrollToPosition(chatAdapter?.itemCount!!)
        val findPositionForName = userAdapter?.findPositionForName(msg.senderName!!)
        val viewHolder = rvUserScoreList.findViewHolderForAdapterPosition(findPositionForName!!)
        if (viewHolder != null) {
            popupTips?.showAsDropDown(viewHolder.itemView, msg)
        }
        handler.postDelayed({ popupTips?.dismiss() }, 2000)
        checkAnswer(msg)
    }

    //显示答案
    private fun showAnswer(answer: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        answerDialog.show(transaction, answer)
        handler.postDelayed({ hideAnswer() }, 5000)
    }

    //隐藏答案
    private fun hideAnswer() {
        answerDialog.dismiss()
    }

    //显示提示信息
    private fun showTips(tips: String) {
        tvTitle.text = tips
    }

    //显示倒计时
    private fun showCountDown(time: String) {
        tvTimer.text = time
    }

    //显示第几轮
    private fun showRound(round: Int) {
        tvRound.text = "第${round}轮"
    }

    //发送画板数据
    override fun onDraw(code: String) {
        if (isMePaint) {
            netManager.sendPaint(code)
        }
    }

    override fun onConnectSuccess() {

    }

    override fun onConnectFailed() {

    }

    override fun onIntercept() {
        ToastUtilK.show(this, "服务器断开连接")
    }

    override fun onDataArrived(data: ByteArray?) {
        val msg = String(data!!, Charset.forName("utf-8"))
        if (msg.isEmpty()) return
        if (msg.first() == 'P') {
            val code = msg.substring(1)
            Log.d("paint", code)
            drawBoard.setBitmapCode(code)
            return
        }
        val msgBean = Gson().fromJson<MsgBean>(msg, MsgBean::class.java)
        if (msgBean.type == MsgType.PAINT.getType()) {
            val paint = msgBean.msg
            //val uncompress = StringZipUtil.uncompress(paint)
            drawBoard.setBitmapCode(paint!!)
        } else if (msgBean.type == MsgType.GAME.getType()) {
            if (msgBean.code == MsgCode.ROOM_INFO.code ||
                    msgBean.code == MsgCode.ROOM_JOIN_SUC.code) {
                //房间信息同步
                val json = msgBean.msg
                val roomBean = Gson().fromJson<RoomBean>(json, RoomBean::class.java)
                refreshRoom(roomBean!!)
            } else if (msgBean.code == MsgCode.GAME_CHAT.code) {
                showMsg(msgBean)
            } else if (msgBean.code == MsgCode.GAME_GIFT_FLOWER.code) {
                //收到鲜花
                showGift(1)
            } else if (msgBean.code == MsgCode.GAME_GIFT_SLIPPER.code) {
                //收到拖鞋
                showGift(2)
            }
        }
    }

    private fun refreshRoom(roomBean: RoomBean) {
        val users = roomBean.users
        //更新用户信息
        userAdapter?.setDatas(users!!)
        //判断自己身份
        val roomPainter = roomBean.roomPainter
        val userName = roomPainter?.userName
        val switch = userName == meName
        if (isMePaint != switch) {
            isMePaint = switch
            checkPlayer()
        }
        //画板锁定和解锁
        drawBoard.lock = !isMePaint
        //更新界面文字
        word = roomBean.word!!
        tips = roomBean.wordTips!!
        val countDown = roomBean.paintCountDown
        countDown(countDown)
        showRound(roomBean.roomState)
    }

    private fun showGift(type: Int) {
        val giftDialog = GiftDialog()
        giftDialog.show(supportFragmentManager.beginTransaction(), type, soundsUtil!!)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("退出游戏")
                .setCancelable(true)
                .setMessage("退出游戏会清空积分，确定退出吗？")
                .setPositiveButton("退出") { _, _ -> finish() }
                .setNegativeButton("取消") { dialog, _ -> dialog?.dismiss() }
                .show()

    }
}
