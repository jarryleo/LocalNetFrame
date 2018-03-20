package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.GalleryAdapter
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.net.NetInterFace
import cn.leo.localnetframe.utils.Config
import cn.leo.localnetframe.utils.get
import cn.leo.localnetframe.utils.put
import kotlinx.android.synthetic.main.activity_setting.*
import me.khrystal.library.widget.CircularHorizontalMode

class SettingActivity : AppCompatActivity() {
    private var iconIndex = -1
    private var netManager: NetImpl? = null
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
        netManager = MyApplication.getNetManager(
                object : NetInterFace.OnDataArrivedListener() {})
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
            put(Config.ICON, iconIndex)
            put(Config.NICKNAME, nickname)
            netManager?.setMeIcon(iconIndex)
            netManager?.setMeName(nickname)
            ToastUtilK.show(this, "设置完成")
            startActivity(Intent(this, RoomListActivity::class.java))
            finish()
        }

    }
}
