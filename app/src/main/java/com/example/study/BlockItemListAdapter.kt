package com.example.study

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class BlockItemListAdapter(private val context: Context,
                           private val blockItemList: List<BlockItem>,
                           private val onBlockItemClicked: (BlockItem) -> Unit)
    : RecyclerView.Adapter<BlockItemListAdapter.BlockItemViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val view = inflater.inflate(R.layout.block_item_row, parent, false)
        val viewHolder = BlockItemViewHolder(view)

        view.setOnClickListener {
            val blockItem = blockItemList[viewHolder.adapterPosition]
            onBlockItemClicked(blockItem)
        }
        return viewHolder
    }

    override fun getItemCount() = blockItemList.size

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        val blockItem = blockItemList[position]
        holder.title.text = blockItem.subtitle
    }

    class BlockItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.blockTitle)
    }
}