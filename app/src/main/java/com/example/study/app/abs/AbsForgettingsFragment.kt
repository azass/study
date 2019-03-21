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
import com.example.study.BLOCKSHEET_MANAGER
import com.example.study.R
import com.example.study.model.abs.BlockSheetManager
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.abs.AbsRecentResultsDao
import java.io.Serializable

class AbsForgettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var blockSheetManager: BlockSheetManager

    companion object {
        fun newInstance(blockSheetManager: BlockSheetManager): AbsForgettingsFragment {
            val args = Bundle()
            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = AbsForgettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //
        val view = inflater.inflate(R.layout.fragment_foregettings, container, false)

        val ctx = context ?: return view
        val db = BlockStudyDatabase(ctx).readableDatabase
        val dao = AbsRecentResultsDao(db)
        dao.selectAll()
        db.close()

        blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager

        (activity as AppCompatActivity).supportActionBar?.title = "忘却リスト"

        recyclerView = view.findViewById(R.id.blockItemList)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        recyclerView.adapter =
            BlockSheetAdapter(
                ctx,
                blockSheetManager.getForgettingList(ctx)
            ) { index, blockItem ->
                (ctx as OnBlockItemSelectListener).onBlockItemSelected(index, blockItem)
            }
        return view
    }
}