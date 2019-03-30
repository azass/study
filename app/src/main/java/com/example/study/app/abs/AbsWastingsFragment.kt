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
import com.example.study.R
import com.example.study.model.abs.BlockSheetManager

class AbsWastingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var blockSheetManager: BlockSheetManager

//    companion object {
//        fun newInstance(blockSheetManager: BlockSheetManager): AbsWastingsFragment {
//            val args = Bundle()
//            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
//            val fragment = AbsWastingsFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //
        val view = inflater.inflate(R.layout.fragment_wastings, container, false)

        val ctx = context ?: return view

        (ctx as AppCompatActivity).supportActionBar?.title = "ムダリスト"

//        blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager
        blockSheetManager = (ctx as BlockSheetListener).getBlockSheetManager()

        recyclerView = view.findViewById(R.id.blockItemList)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        // 枠線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        recyclerView.adapter =
            BlockSheetAdapter(ctx, blockSheetManager.getWastingList(ctx)
            ) { index, blockItem ->
                (ctx as OnBlockItemSelectListener).onBlockItemSelected(index, blockItem)
            }
        return view
    }
}