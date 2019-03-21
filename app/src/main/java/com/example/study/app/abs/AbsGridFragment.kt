package com.example.study.app.abs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.BLOCKSHEET_MANAGER
import com.example.study.R
import com.example.study.model.abs.AbstractionBlock
import com.example.study.model.abs.BlockSheetManager
import java.io.Serializable

class AbsGridFragment : Fragment() {

    interface OnAbstractionBlockSelectListener {
        fun onAbstractionBlockSelected(abstractionBlock: AbstractionBlock)
    }

    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(blockSheetManager: BlockSheetManager): AbsGridFragment {
            val args = Bundle()
            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = AbsGridFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化リスト"
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager

        val view = inflater.inflate(R.layout.fragment_abstraction_block_menu, container, false)

        val ctx = context ?: return view

        recyclerView = view.findViewById(R.id.abstractionBlock)

        // グリッド表示
        recyclerView.layoutManager = GridLayoutManager(ctx, 3)

        recyclerView.adapter = BlockMenuAdapter(ctx, blockSheetManager.abstractionBlockList!!) { abstractionBlock ->
            (ctx as OnAbstractionBlockSelectListener).onAbstractionBlockSelected(abstractionBlock)
        }
        return view
    }

    internal inner class BlockMenuAdapter(
        private val context: Context,
        private val abstractionBlockList: List<AbstractionBlock>,
        private val onMenuClicked: (AbstractionBlock) -> Unit
    ) : RecyclerView.Adapter<MenuViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = inflater.inflate(R.layout.abstraction_block_row, parent, false)
            val viewHolder = MenuViewHolder(view)

            view.setOnClickListener {
                val menu = abstractionBlockList[viewHolder.adapterPosition]
                onMenuClicked(menu)
            }
            return viewHolder
        }

        override fun getItemCount() = abstractionBlockList.size

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            val menu = abstractionBlockList[position]
            holder.title.text = menu.title
        }
    }

    internal inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
    }
}