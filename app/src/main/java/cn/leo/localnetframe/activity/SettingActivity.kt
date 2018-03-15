package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.GalleryAdapter
import kotlinx.android.synthetic.main.activity_setting.*
import me.khrystal.library.widget.CircularHorizontalMode

class SettingActivity : AppCompatActivity() {

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
            val icon = galleryAdapter.getIcon(it)
            iv_head.setImageResource(icon)
        }
    }
}
