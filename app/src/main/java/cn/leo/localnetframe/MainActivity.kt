package cn.leo.localnetframe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.leo.localnetframe.view.DrawBoard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, DrawBoard.OnDrawListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenAp.setOnClickListener(this)
        drawBoard.onDrawListener = this
    }

    override fun onClick(v: View?) {
        drawBoard.undo()
        /*if (ApManager.canOpenAp(this)) {
            ApManager.openAp(this, "Test", "123456")
        } else {
            val connectedSSID = WifiLManager.getConnectedSSID(this)
            Toast.makeText(this, connectedSSID, Toast.LENGTH_SHORT).show()

        }*/
    }

    override fun onDraw(code: String) {
        drawBoard1.setBitmapCode(code)
    }
}
