package com.example.study

import android.content.ContentValues
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
import java.io.Serializable

class WastingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var blockSheetManager: BlockSheetManager

    companion object {
        private const val BLOCK_SHEET_MANAGER = "BLOCK_SHEET_MANAGER"
        fun newInstance(blockSheetManager: BlockSheetManager): WastingsFragment {
            val args = Bundle()
            args.putSerializable(BLOCK_SHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = WastingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //
        val view = inflater.inflate(R.layout.fragment_wastings, container, false)

        val ctx = context ?: return view

        blockSheetManager = arguments?.getSerializable(WastingsFragment.BLOCK_SHEET_MANAGER) as BlockSheetManager

        (activity as AppCompatActivity).supportActionBar?.title = "ムダリスト"

        recyclerView = view.findViewById(R.id.blockItemList)

        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        val adapter = BlockItemListAdapter(ctx, getWastingList(ctx)) { blockItem ->
            (ctx as com.example.study.OnBlockItemSelectListener).onBlockItemSelected(blockItem)
        }
        recyclerView.adapter = adapter
        return view
    }

    private fun getWastingList(context: Context): List<BlockItem> {

        val wastingList = mutableListOf<BlockItem>()
        val db = BlockStudyDatabase(context).writableDatabase

        val elapsedTime = System.currentTimeMillis() - 30 * 24 * 3600000
        // 最初の正解から１ヶ月超えたアイテムのステータスを０にする
        val update = ContentValues().apply {
            put("status", 0)
        }
        db.update("RecentResults", update, "answerResult = 0 and beginTime < ?", arrayOf(elapsedTime.toString()))

        db.query(
            "RecentResults",
            arrayOf("blockNo", "itemNo"),
            "status = 0",
            null,
            null,
            null,
            null,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val blockNo = c.getString(c.getColumnIndex("blockNo"))
                val itemNo = c.getInt(c.getColumnIndex("itemNo"))
                wastingList.add(blockSheetManager.getBlockItem(blockNo, itemNo))
            }

            return wastingList
        }
    }
}