package com.delsart.romanizeltric

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyAdapter() : BaseQuickAdapter<SongInfo, BaseViewHolder>(R.layout.item_list) {

    override fun convert(viewHolder: BaseViewHolder, bean: SongInfo) {
        viewHolder.setText(R.id.name, bean.name)
                .setText(R.id.author, bean.author)
                .setText(R.id.resource, bean.resource)


    }
}