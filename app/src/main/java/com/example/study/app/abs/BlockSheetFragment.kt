package com.example.study.app.abs

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
class BlockSheetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //
        val view = inflater.inflate(R.layout.fragment_block_sheet, container, false)

        val ctx = context ?: return view

        val blockSheetManager = (ctx as BlockSheetListener).getBlockSheetManager()

        val blockSheet = blockSheetManager.selectedBlockSheet!!

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシート"

        view.findViewById<TextView>(R.id.blockSheetNo).text = blockSheet.blockNo
        view.findViewById<TextView>(R.id.blockSheetTitle).text = blockSheet.title

        recyclerView = view.findViewById(R.id.blockItemList)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = BlockSheetAdapter(
            ctx, blockSheetManager.getSelectedBlockItemList()
        ) { index, blockItem ->
            blockItem.blockSheet = blockSheet
            (ctx as OnBlockItemSelectListener).onBlockItemSelected(index, blockItem)
        }
        return view
    }
}