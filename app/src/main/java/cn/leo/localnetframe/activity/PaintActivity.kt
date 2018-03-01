package cn.leo.localnetframe.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.net.NetManager
import cn.leo.localnetframe.view.DrawBoard
import kotlinx.android.synthetic.main.activity_main.*

class PaintActivity : AppCompatActivity(), DrawBoard.OnDrawListener, NetManager.OnMsgArrivedListener {


    private var netManager: NetManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawBoard.onDrawListener = this
        netManager = MyApplication.getNetManager(this)
        initView()
        checkPlayer()
    }

    //检查谁在作画
    private fun checkPlayer() {
        drawBoard.lock = !netManager?.isMePlaying()!!
    }

    private fun initView() {
        btnUndo.setOnClickListener { drawBoard.undo() }
        changeColor.setOnClickListener { drawBoard.setColor(Color.RED) }
        changeSize.setOnClickListener { drawBoard.setStrokeWidth(15f) }
    }

    //发送数据
    override fun onDraw(code: String) {
        netManager?.sendData(("P" + code).toByteArray())
    }

    //接收数据
    override fun onMsgArrived(data: String) {
        when (data.first()) {
            'C' -> {
                //聊天数据
            }
            'P' -> {
                //画画数据
                drawBoard.setBitmapCode(data)
            }
            else -> {

            }
        }

    }
}
