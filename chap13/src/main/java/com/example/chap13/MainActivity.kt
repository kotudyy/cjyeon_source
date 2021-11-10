package com.example.chap13

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap13.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var datas: MutableList<String>? = null
    lateinit var adapter: MyAdapter

    @SuppressLint("NotifyDataSetChanged")
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val value = result.data?.getStringExtra("result")
            datas?.add(value.toString())
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            //startActivityForResult(intent, 10)      // deprecated
            requestActivity.launch(intent)
        }

        datas = savedInstanceState?.let {
            it.getStringArrayList("datas")?.toMutableList()
        } ?: let {
            mutableListOf<String>()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager=layoutManager
        adapter=MyAdapter(datas)
        binding.mainRecyclerView.adapter=adapter
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private  val getResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK) {
//                val value = it.data?.getStringExtra("result")
//                datas?.add(value.toString())
//                adapter.notifyDataSetChanged()
//            }
//        }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val getResult =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (resultCode == 10 && resultCode == Activity.RESULT_OK) {
//                    val value = it.data?.getStringExtra("result")
//                    datas?.add(value.toString())
//                    adapter.notifyDataSetChanged()
//                }
//            }
////        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
////            data!!.getStringArrayExtra("result")?.let {
////                datas?.add(it.toString())
////                adapter.notifyDataSetChanged()
////            }
//        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("datas", ArrayList(datas))
    }
}