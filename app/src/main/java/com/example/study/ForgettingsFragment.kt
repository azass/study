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
import java.io.Serializable

class ForgettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var blockSheetManager: BlockSheetManager

    companion object {
        private const val BLOCK_SHEET_MANAGER = "BLOCK_SHEET_MANAGER"
        fun newInstance(blockSheetManager: BlockSheetManager): ForgettingsFragment {
            val args = Bundle()
            args.putSerializable(BLOCK_SHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = ForgettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //
        val view = inflater.inflate(R.layout.fragment_foregettings, container, false)

        val ctx = context ?: return view

        blockSheetManager = arguments?.getSerializable(ForgettingsFragment.BLOCK_SHEET_MANAGER) as BlockSheetManager

        (activity as AppCompatActivity).supportActionBar?.title = "忘却リスト"

        recyclerView = view.findViewById(R.id.blockItemList)

        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        val adapter = BlockItemListAdapter(ctx, getForgettingList(ctx)) { blockItem ->
            (ctx as com.example.study.OnBlockItemSelectListener).onBlockItemSelected(blockItem)
        }
        recyclerView.adapter = adapter
        return view
    }


    private fun getForgettingList(context: Context): List<BlockItem> {

        val forgettingList = mutableListOf<BlockItem>()

        val db = BlockStudyDatabase(context).writableDatabase
        db.query(
            "RecentResults",
            arrayOf("blockNo", "itemNo", "status", "answerResult", "beginTime", "updateTime"),
            "status > 0 and status < ?",
            arrayOf("4"),
            null,
            null,
            null,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val blockNo = c.getString(c.getColumnIndex("blockNo"))
                val itemNo = c.getInt(c.getColumnIndex("itemNo"))
                val status = c.getInt(c.getColumnIndex("status"))
                val answerResult = c.getInt(c.getColumnIndex("answerResult"))
                val beginTime = c.getLong(c.getColumnIndex("beginTime"))
                val updateTime = c.getLong(c.getColumnIndex("updateTime"))
                if (!(answerResult == 1 && !outOfTerm(status, updateTime - beginTime))) {
                    forgettingList.add(blockSheetManager.getBlockItem(blockNo, itemNo))
                }
            }
        }

//        params.forEach {
//            val cursor = db.rawQuery(FORGETTING_SQL, it)
//
//            cursor.use {
//                while(cursor.moveToNext()) {
//                    val blockNo = cursor.getString(cursor.getColumnIndex("blockNo"))
//                    val itemNo = cursor.getInt(cursor.getColumnIndex("itemNo"))
//                    val answerResult = cursor.getInt(cursor.getColumnIndex("answerResult"))
//                    if (answerResult == 1) {
//                        forgettingList.add(getBlockItem(blockNo, itemNo))
//                    } else {
//                        val blockItem = getBlockItem(blockNo, itemNo)
//                        if (!wastingTable.containsKey(blockNo)) {
//                            val blockSheet = BlockSheet(blockNo, blockItem.sheetTitle, mutableListOf<BlockItem>())
//                            wastingTable.put(blockNo, blockSheet)
//                        }
//                        wastingTable[blockNo]?.itemList!!.add(blockItem)
//                    }
//                }
//            }
//        }
//
//        wastingTable.keys.forEach {
//            val blockNo = it
//            val blockSheet = wastingTable[blockNo]
//            blockSheet?.itemList!!.forEach {
//                val itemNo = it.itemNo
//                val update = ContentValues().apply {
//                    put("status", 0)
//                }
//                db.update("Records", update, "blockNo = ? and itemNo = ?", arrayOf(blockNo, itemNo))
//            }
//        }

        db.close()

        return forgettingList
    }
}