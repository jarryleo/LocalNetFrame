package cn.leo.drawonline.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.drawonline.MyApplication
import cn.leo.drawonline.R
import cn.leo.drawonline.adapter.GalleryAdapter
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.constant.MsgCode
import cn.leo.drawonline.constant.MsgType
import cn.leo.drawonline.net.NetManager
import cn.leo.drawonline.utils.Config
import cn.leo.drawonline.utils.get
import cn.leo.drawonline.utils.put
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.nio_client.core.Client
import cn.leo.nio_client.core.ClientListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_setting.*
import me.khrystal.library.widget.CircularHorizontalMode

class SettingActivity : AppCompatActivity(), ClientListener {
    private val netManager = NetManager()
    private var iconIndex = -1
    private val icons = Icons.getIconList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
        initEvent()
    }

    private fun initView() {
        title = "设置头像和昵称"
        val galleryAdapter = GalleryAdapter()
        setting_gallery.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        setting_gallery.setViewMode(CircularHorizontalMode())
        setting_gallery.setNeedCenterForce(true)
        setting_gallery.adapter = galleryAdapter
        setting_gallery.setOnCenterSelectedListener {
            val index = it % icons.size
            iconIndex = index
            iv_head.setImageResource(icons[index])
        }
        val ic = get(Config.ICON, 3)
        val nickname = get(Config.NICKNAME, "")
        val icon = icons[ic]
        iv_head.setImageResource(icon)
        et_nickname.setText(nickname)
    }

    private fun initEvent() {
        btn_save.setOnClickListener {
            if (iconIndex == -1) {
                ToastUtilK.show(this, "请选择头像")
                return@setOnClickListener
            }
            val nickname = et_nickname.text.toString()
            if (nickname.isEmpty()) {
                ToastUtilK.show(this, "请输入昵称")
                return@setOnClickListener
            }
            if (MyApplication.getUser() == null) {
                ToastUtilK.show(this, "没有登录，无法设置")
                return@setOnClickListener
            }
            val localName = get(Config.NICKNAME, "")
            if (localName.isEmpty()) {
                //注册
                netManager.reg(nickname, iconIndex)
            } else {
                //修改
                netManager.update(nickname, iconIndex)
            }
        }

    }

    private fun regSuccess() {
        ToastUtilK.show(this, "设置成功")
        startActivity(Intent(this, RoomListActivity::class.java))
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        ToastUtilK.show(this, "连接状态" + Client.getConnectStatus())
    }

    override fun onConnectSuccess() {
        ToastUtilK.show(this, "连接服务器成功")
    }

    override fun onConnectFailed() {
        ToastUtilK.show(this, "连接服务器失败")
    }

    override fun onIntercept() {
        ToastUtilK.show(this, "被服务器拒绝")
    }

    override fun onDataArrived(data: ByteArray?) {
        val json = String(data!!)
        val msgBean = Gson().fromJson<MsgBean>(json, MsgBean::class.java)
        if (msgBean.type == MsgType.SYS.getType()) {
            if (msgBean.code == MsgCode.REG_FAI.code ||
                    msgBean.code == MsgCode.EDIT_FAI.code) {
                //注册失败
                ToastUtilK.show(this, "昵称已被占用，请换个试试")
            } else if (msgBean.code == MsgCode.REG_SUC.code ||
                    msgBean.code == MsgCode.EDIT_SUC.code) {
                //注册成功
                val nickname = et_nickname.text.toString()
                put(Config.ICON, iconIndex)
                put(Config.NICKNAME, nickname)
                regSuccess()
            }
        }
    }
}
