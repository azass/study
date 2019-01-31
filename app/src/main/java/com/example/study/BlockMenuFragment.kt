package com.example.study

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable

class BlockMenuFragment : Fragment() {

    interface OnBlockSheetSelectListener {
        fun onBlockSheetSelected(block: BlockSheet)
    }

    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val BLOCKS = "BLOCKS"

        fun newInstance(blockList: List<BlockSheet>): BlockMenuFragment {
            val args = Bundle()
            args.putSerializable(BLOCKS, blockList as Serializable)
            val fragment = BlockMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシートリスト"
        //
        val view = inflater.inflate(R.layout.fragment_block_menu, container, false)

        recyclerView = view.findViewById(R.id.blockMenu)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        //
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val ctx = context ?: return view

        val blockList = arguments?.getSerializable(BLOCKS) as List<BlockSheet>
        val adapter = BlockListAdapter(ctx, blockList) { block ->
            (context as BlockMenuFragment.OnBlockSheetSelectListener).onBlockSheetSelected(block)
        }
        recyclerView.adapter = adapter
        return view
    }

    internal inner class BlockListAdapter(
        private val context: Context,
        private val blockList: List<BlockSheet>,
        private val onBlockListClicked: (BlockSheet) -> Unit) : RecyclerView.Adapter<BlockSheetViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockSheetViewHolder {
            val view = inflater.inflate(R.layout.block_sheet_row, parent, false)
            val viewHolder = BlockSheetViewHolder(view)

            view.setOnClickListener {
                val block = blockList[viewHolder.adapterPosition]
                onBlockListClicked(block)
            }
            return viewHolder
        }

        override fun getItemCount() = blockList.size

        override fun onBindViewHolder(holder: BlockSheetViewHolder, position: Int) {
            val block = blockList[position]
            holder.title.text = block.title
        }
    }

    internal inner class BlockSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.blockTitle)
    }
}