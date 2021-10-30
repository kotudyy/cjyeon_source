package com.e.a13_activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.e.a13_activity.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBinding.inflate(layoutInflater)     //activity_add.xml
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)     //menu_add.xml
        return super.onCreateOptionsMenu(menu)      //메뉴객체 생성
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId){
        R.id.menu_add_save -> {     //저장버튼 누를 경우
            val intent = intent
            intent.putExtra("result", binding.addEditView.text.toString())      //에디트텍스트의 스트링을 네임 : result에 저장
            setResult(Activity.RESULT_OK, intent)       //돌려줄 결과 저장
            finish()    //AddActivity 끝내기
            true
        }
        else -> true
    }
    
}