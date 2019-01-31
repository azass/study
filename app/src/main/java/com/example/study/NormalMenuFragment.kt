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
import android.widget.TextView
import java.io.Serializable

class NormalMenuFragment : Fragment() {

    interface OnBlockMenuSelectListener {
        fun onBlockMenuSelected(menu: BlockMenu)
    }

    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val BLOCK_MENUS = "BLOCK_MENUS"
        fun newInstance(blockMenuList: List<BlockMenu>): NormalMenuFragment {
            val args = Bundle()
            args.putSerializable(NormalMenuFragment.BLOCK_MENUS, blockMenuList as Serializable)
            val fragment = NormalMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //
        val view = inflater.inflate(R.layout.fragment_normal_menu, container, false)

        val ctx = context ?: return view
        (activity as AppCompatActivity).supportActionBar?.title = "抽象化リスト"

        recyclerView = view.findViewById(R.id.menu)

        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        val blockMenuList = arguments?.getSerializable(BLOCK_MENUS) as List<BlockMenu>
        val adapter = BlockMenuAdapter(ctx, blockMenuList) { blockMenu ->
            (ctx as OnBlockMenuSelectListener).onBlockMenuSelected(blockMenu)
        }
        recyclerView.adapter = adapter
        return view
    }

    internal inner class BlockMenuAdapter(
        private val context: Context,
        private val menus: List<BlockMenu>,
        private val onMenuClicked: (BlockMenu) -> Unit
    ) : RecyclerView.Adapter<MenuViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = inflater.inflate(R.layout.block_menu_row, parent, false)
            val viewHolder = MenuViewHolder(view)

            view.setOnClickListener {
                val menu = menus[viewHolder.adapterPosition]
                onMenuClicked(menu)
            }
            return viewHolder
        }

        override fun getItemCount() = menus.size

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            val menu = menus[position]
            holder.title.text = menu.title
        }
    }

    internal inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
    }
}