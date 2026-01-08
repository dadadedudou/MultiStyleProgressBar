package com.example.multistyleprogressbar.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.multistyleprogressbar.R
import com.example.multistyleprogressbar.bean.MealsFood

class FoodsAdapter: BaseQuickAdapter<MealsFood, BaseViewHolder>(R.layout.item_meals_card_foods) {
    override fun convert(
        holder: BaseViewHolder,
        item: MealsFood
    ) {
        holder.setText(R.id.food_name, item.foodName)
    }
}