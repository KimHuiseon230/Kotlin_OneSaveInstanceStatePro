package com.example.onesaveinstancestatepro

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.onesaveinstancestatepro.databinding.ActivityAddBinding
import com.example.onesaveinstancestatepro.databinding.ActivityMainBinding

class AddActivity : AppCompatActivity() {
    val binding: ActivityAddBinding by lazy { ActivityAddBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //메뉴 화면을 가져와야함. 객체로 ..
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //2.메뉴 이벤트 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_add_save -> {
                intent.putExtra("result", binding.addEditView.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish() // 창 죽이기
            }
        }
        return super.onOptionsItemSelected(item)
    }
}