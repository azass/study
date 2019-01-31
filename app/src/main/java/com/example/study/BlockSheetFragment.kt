package com.example.study

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
class BlockSheetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val BLOCK_SHEET = "BLOCK_SHEET"

        fun newInstance(blockSheet: BlockSheet): BlockSheetFragment {
            val args = Bundle()
            args.putSerializable(BLOCK_SHEET, blockSheet as Serializable)
            val fragment = BlockSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //
        val view = inflater.inflate(R.layout.fragment_block_sheet, container, false)

        val ctx = context ?: return view

        val blockSheet = arguments?.getSerializable(BLOCK_SHEET) as BlockSheet

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシート"

        val blockSheetTitleView = view.findViewById<TextView>(R.id.blockSheetTitle)
        blockSheetTitleView.text = blockSheet.title

        recyclerView = view.findViewById(R.id.blockItemList)

        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        val adapter = BlockItemListAdapter(ctx, blockSheet.itemList) { blockItem ->
            blockItem.blockNo = blockSheet.blockNo
            blockItem.sheetTitle = blockSheet.title
            (ctx as OnBlockItemSelectListener).onBlockItemSelected(blockItem)
        }
        recyclerView.adapter = adapter
        return view
    }
}