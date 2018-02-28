package cn.leo.localnetframe.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import cn.leo.localnet.manager.WifiLManager
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.net.NetManager
import cn.leo.localnetframe.view.DrawBoard
import kotlinx.android.synthetic.main.activity_main.*

class PaintActivity : AppCompatActivity(), View.OnClickListener, DrawBoard.OnDrawListener, NetManager.OnMsgArrivedListener {


    private var netManager: NetManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenAp.setOnClickListener(this)
        drawBoard.onDrawListener = this

        changeColor.setOnClickListener { drawBoard.setColor(Color.RED) }
        changeSize.setOnClickListener { drawBoard.setStrokeWidth(15f) }

        netManager = MyApplication.getNetManager(this)
        netManager?.startNet()

        val localIpAddress = WifiLManager.getLocalIpAddress(this)
        Toast.makeText(this, localIpAddress, Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        drawBoard.undo()
    }

    //发送数据
    override fun onDraw(code: String) {
        netManager?.sendData(("P" + code).toByteArray())
        //drawBoard1.setBitmapCode(code)
    }

    //接收数据
    override fun onMsgArrived(data: String) {
        runOnUiThread { drawBoard.setBitmapCode(data) }
    }
}
