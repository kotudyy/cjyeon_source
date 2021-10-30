package com.e.a13_activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.a13_activity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding   //activity_main.xml
    var datas: MutableList<String>? = null      //리싸이클러를 위한 뮤터블리스트 데이터
    lateinit var adapter: MyAdapter             //뷰 홀더의 뷰에, 데이터로 각 항목을 만들어주는 역할을 하는 어뎁터

    override fun onCreate(savedInstanceState: Bundle?) {        //데이터 저장을 위한 savedInstanceState
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)    //activity_main.xml
        setContentView(binding.root)

        binding.mainFab.setOnClickListener {            //todo 리스트 추가 버튼 클릭시
            val intent = Intent(this, AddActivity::class.java)      // 액티비티 클래스를 구현하는 컨텍스트(메인)와 호출될 액티비티(서브 액티비티)의 클래스 정보가 인수로 전달됨
            startActivityForResult(intent, 10)      //새 액티비티(서브엑티비티)를 열고, 결과를 받기위함 requestCode : 액티비티 코드 식별 값 - 임의
        }

        datas=savedInstanceState?. let {        //저장 값이 있으면,
            it.getStringArrayList("datas")?.toMutableList() //메인 엑티비티에서 todo 리스트의 스트링 값들을 뮤터블리스트로 받아옴
        } ?: let {
            mutableListOf<String>()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager=layoutManager  //activity_main의 recyclerview를 메인 엑티비티에 리니어레이아웃으로 선언
        adapter=MyAdapter(datas)
        binding.mainRecyclerView.adapter=adapter    //어탭터 지정
        binding.mainRecyclerView.addItemDecoration(     //리사이클러뷰 아이템 꾸미기
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)   //아이템의 구분선 출력
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && resultCode == Activity.RESULT_OK){      //sub액티비티인 AddActivity에서 넘어온 값이 10이고, 잘 넘어오면
            data!!.getStringExtra("result")?.let {
                datas?.add(it)                      //값을 메인엑티비티에 추가하고,
                adapter.notifyDataSetChanged()      //리사이클러 뷰 업데이트
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putStringArrayList("datas", ArrayList(datas))  //"키", "값" 형태로 저장
    }
}