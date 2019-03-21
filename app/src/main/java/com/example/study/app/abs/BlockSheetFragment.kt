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
import com.example.study.BLOCKSHEET_MANAGER
import com.example.study.R
import com.example.study.model.abs.BlockSheetManager
import java.io.Serializable
class BlockSheetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(blockSheetManager: BlockSheetManager): BlockSheetFragment {
            val args = Bundle()
            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = BlockSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager
        //
        val view = inflater.inflate(R.layout.fragment_block_sheet, container, false)

        val ctx = context ?: return view

        val blockSheet = blockSheetManager.selectedBlockSheet!!

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシート"

        val blockSheetTitleView = view.findViewById<TextView>(R.id.blockSheetTitle)
        blockSheetTitleView.text = blockSheet.title

        recyclerView = view.findViewById(R.id.blockItemList)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = BlockSheetAdapter(
            ctx,
            blockSheetManager.getSelectedBlockItemList()
        ) { index, blockItem ->
            (ctx as OnBlockItemSelectListener).onBlockItemSelected(index, blockItem)
        }
        return view
    }
}