package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.GalleryAdapter
import cn.leo.localnetframe.utils.put
import kotlinx.android.synthetic.main.activity_setting.*
import me.khrystal.library.widget.CircularHorizontalMode

class SettingActivity : AppCompatActivity() {
    private var iconIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
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
            iconIndex = it
            val icon = galleryAdapter.getIcon(it)
            iv_head.setImageResource(icon)
        }
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
            put("icon", iconIndex)
            put("nickname", nickname)
            ToastUtilK.show(this, "设置完成")
            finish()
        }
    }
}
