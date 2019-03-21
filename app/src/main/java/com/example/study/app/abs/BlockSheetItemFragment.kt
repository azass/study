package com.example.study.app.abs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.example.study.BLOCKSHEET_MANAGER
import com.example.study.DateUtils
import com.example.study.R
import com.example.study.StudyUtils
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.abs.BlockItem
import com.example.study.model.abs.BlockSheet
import com.example.study.model.abs.BlockSheetManager
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.abs.AbsAnswerLogDao
import com.example.study.persistence.abs.AbsCardAnswerLogDao
import com.example.study.persistence.abs.AbsRecentResultsDao
import java.io.Serializable

class BlockSheetItemFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blockSheet: BlockSheet
    private lateinit var blockItem: BlockItem
    private lateinit var blockSheetManager: BlockSheetManager

    companion object {

        fun newInstance(blockSheetManager: BlockSheetManager): BlockSheetItemFragment {
            val args = Bundle()
            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = BlockSheetItemFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager
        blockItem = blockSheetManager.getSelectedBlockItem()
        blockSheet = blockItem.blockSheet!!

        val db = BlockStudyDatabase(context!!).readableDatabase
        val answerLogDao = AbsAnswerLogDao(db)
        val recentResultsDao = AbsRecentResultsDao(db)
        val answerLog = answerLogDao.selectLast(blockItem.blockNo, blockItem.itemNo)
        val recentResult = recentResultsDao.select(blockItem.blockNo, blockItem.itemNo)
        val dao = AbsCardAnswerLogDao(db)
        blockItem.contents!!.forEach {
            blockItem.cardAnswerLogs.add(dao.selectLast(it.blockNo, it.itemNo, it.seq)!!)
        }
        // title of action bar
        (activity as AppCompatActivity).supportActionBar?.title = blockSheet.title

        val fragmentBlockItemLayout = inflater.inflate(R.layout.fragment_block_item, container,false)

        val ctx = context ?: return fragmentBlockItemLayout

        // block_sheetのシートタイトル
        fragmentBlockItemLayout.findViewById<TextView>(R.id.sheetTitle).text = blockSheet.title
        // block_sheetのサブタイトル
        fragmentBlockItemLayout.findViewById<TextView>(R.id.itemTitle).text = blockItem.subtitle
        // 前回解答日
        fragmentBlockItemLayout.findViewById<TextView>(R.id.lastRecordDate).text =
            if (answerLog != null) answerLog.endTime.substring(0, 10) else "-"
        // 前回解答時間
        fragmentBlockItemLayout.findViewById<TextView>(R.id.lastLapTime).text =
            if (answerLog != null) DateUtils.formatTime(answerLog.elapsedTime) else "-"
        // 前回結果
        fragmentBlockItemLayout.findViewById<TextView>(R.id.lastAccuracyRate).text =
            if (answerLog != null) answerLog.accuracyRate.toString() + "%" else "-"
        //
        fragmentBlockItemLayout.findViewById<TextView>(R.id.absStatus).text =
            if (recentResult != null) StudyUtils.getStatusLabel(recentResult.status) else "-"

        recyclerView = fragmentBlockItemLayout.findViewById(R.id.blockItem)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        // RecyclerView区切線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        // fragment_block_item と block_content を結びつける
        recyclerView.adapter =
            BlockSheetItemAdapter(activity as AppCompatActivity, ctx, blockItem)

        // 前ボタン
        val previousItem = fragmentBlockItemLayout.findViewById<Button>(R.id.previousItem)
        previousItem.setOnClickListener {
            if (blockSheetManager.selectedBlockItemListIndex == 0) {
                // 抽象化ブロックシートへ遷移
                loadBlockItemListFragment()
            } else {
                val previousIndex = blockSheetManager.selectedBlockItemListIndex -1
                // 前のアイテム画面へ遷移
                loadBlockItemFragment(previousIndex, blockSheetManager.blockItemList!![previousIndex])
            }
        }
        // 次ボタン
        val nextItem = fragmentBlockItemLayout.findViewById<Button>(R.id.nextItem)
        nextItem.setOnClickListener{
            if (blockSheetManager.selectedBlockItemListIndex == blockSheetManager.blockItemList!!.size - 1) {
                // 抽象化ブロックシートへ遷移
                loadBlockItemListFragment()
            } else {
                val nextIndex = blockSheetManager.selectedBlockItemListIndex + 1
                // 次のアイテム画面へ遷移
                loadBlockItemFragment(nextIndex, blockSheetManager.blockItemList!![nextIndex])
            }
        }

        return fragmentBlockItemLayout
    }

    // Fragmentでオプションメニューを使用できるようにする
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
    // オプションメニュー追加
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.actionbar, menu)
        // res/menu/actionbar.xmlから取得
        menu?.findItem(R.id.navigation_block_sheet)?.setVisible(true)
    }
    // オプションメニューをクリックした時
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navigation_block_sheet -> {
                loadBlockItemListFragment()
            }
            android.R.id.home -> {
                loadBlockItemListFragment()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadBlockItemListFragment() {
        (activity as OnLoadFragmentListener).loadBlockItemListFragment()
    }

    private fun loadBlockItemFragment(index: Int, toBlockItem: BlockItem) {
        (activity as OnBlockItemSelectListener).onBlockItemSelected(index, toBlockItem)
    }
}