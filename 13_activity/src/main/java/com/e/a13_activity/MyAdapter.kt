    package com.e.a13_activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.a13_activity.databinding.ItemRecyclerviewBinding

class MyViewHolder(val binding: ItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)     //item_recyclerview.xml의 뷰 홀더

class MyAdapter(val datas: MutableList<String>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){  //todo 데이터의 값을 리싸이클러의 뷰에 바인딩 하기 위한 어댑터
    override fun getItemCount(): Int{           //todo 데이터의 갯수를 세는 함수
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            //item_recyclerview.xml 뷰홀더

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]        //텍스트에 데이터 값 대입
    }
}
