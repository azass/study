package com.example.study.app.abs

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
import com.example.study.R
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.abs.AbstractionBlock
import com.example.study.model.abs.BlockSheet
import java.io.Serializable

class AbsIndexFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val ABSTRACTION_BLOCK = "ABSTRACTION_BLOCK"

        fun newInstance(abstractionBlock: AbstractionBlock): AbsIndexFragment {
            val args = Bundle()
            args.putSerializable(ABSTRACTION_BLOCK, abstractionBlock as Serializable)
            val fragment = AbsIndexFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // データのレジーインスタンシエーション
        val abstractionBlock = arguments?.getSerializable(ABSTRACTION_BLOCK) as AbstractionBlock

        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシートリスト"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_abstraction_block, container, false)
        val ctx = context ?: return view
        // 画面のタイトル
        view.findViewById<TextView>(R.id.abstractionTitle).text = abstractionBlock.title

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.abstractionBlockList)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = AbstractionBlockAdapter(ctx, abstractionBlock.blocksheetList!!) { blockSheet ->
            (context as OnLoadFragmentListener).loadBlockSheetFragment(blockSheet)
        }
        return view
    }
    // アダプタクラス
    internal inner class AbstractionBlockAdapter(
        private val context: Context,
        private val blockSheetList: List<BlockSheet>,
        private val onBlockSheetClicked: (BlockSheet) -> Unit) : RecyclerView.Adapter<AbstractionBlockViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractionBlockViewHolder {
            val view = inflater.inflate(R.layout.block_sheet_row, parent, false)
            val viewHolder = AbstractionBlockViewHolder(view)

            view.setOnClickListener {
                val block = blockSheetList[viewHolder.adapterPosition]
                onBlockSheetClicked(block)
            }
            return viewHolder
        }

        override fun getItemCount() = blockSheetList.size

        override fun onBindViewHolder(holder: AbstractionBlockViewHolder, position: Int) {
            val block = blockSheetList[position]
            holder.title.text = block.title
        }
    }

    internal inner class AbstractionBlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.blockSheetTitle)
    }
}