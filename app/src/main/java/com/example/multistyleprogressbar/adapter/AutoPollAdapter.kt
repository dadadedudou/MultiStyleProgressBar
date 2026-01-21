package com.example.multistyleprogressbar.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.multistyleprogressbar.R
import com.example.multistyleprogressbar.bean.MealsFood

class AutoPollAdapter : RecyclerView.Adapter<BaseViewHolder> {
    var context: Context
    var list: MutableList<MealsFood>

    constructor(context: Context, list: MutableList<MealsFood>) {
        this.context = context
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(
                R.layout.item_account_first_login_item_card_scrolling_list_item_layout,
                parent,
                false
            )
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setText(R.id.text, list[position % list.size].foodName)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}