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
import com.example.study.R
import com.example.study.app.ItemClickListener
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.abs.AbstractionBlock
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference


class AbsGridFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var mAdapter: FirebaseRecyclerAdapter<AbstractionBlock, AbsGridFragment.MenuViewHolder>? = null
    private var mAbstractionBlockReference: DatabaseReference? = null
    private var mAbstractionBlockListener: ChildEventListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (activity as AppCompatActivity).supportActionBar?.title = "抽象化リスト"
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val view = inflater.inflate(R.layout.fragment_abstraction_block_menu, container, false)
        val ctx = context ?: return view
        val blockSheetManager = (ctx as BlockSheetListener).getBlockSheetManager()

        recyclerView = view.findViewById(R.id.abstractionBlock)

        // グリッド表示
        recyclerView.layoutManager = GridLayoutManager(ctx, 3)

//        val database = FirebaseDatabase.getInstance().reference
//        mAbstractionBlockReference = database.child("AbstractionBlock")
//
//        firebaseListenerInit()
//
//        val query = mAbstractionBlockReference!!
//        val options = FirebaseRecyclerOptions.Builder<AbstractionBlock>()
//            .setQuery(query, AbstractionBlock::class.java)
//            .build()
//        mAdapter = object : FirebaseRecyclerAdapter<AbstractionBlock, AbsGridFragment.MenuViewHolder>(options) {
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsGridFragment.MenuViewHolder {
//                val view = inflater.inflate(R.layout.abstraction_block_row, parent, false)
//                return MenuViewHolder(view)
//            }
//
//            override fun onBindViewHolder(
//                holder: AbsGridFragment.MenuViewHolder,
//                position: Int,
//                abstractionBlock: AbstractionBlock
//            ) {
//                holder.title.text = abstractionBlock.title
//
//                //
//
//                // 行をクリックしたら画面遷移
//                val itemClickListener = object : ItemClickListener {
//                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
//                        blockSheetManager.selectedAbstractionBlockIndex = position
//                        (ctx as OnLoadFragmentListener).loadAbsIndexFragment(abstractionBlock)
//                    }
//                }
//                holder.itemClickListener = itemClickListener
//            }
//        }
//
//        recyclerView.adapter = mAdapter

        recyclerView.adapter = BlockMenuAdapter(ctx, blockSheetManager.abstractionBlockList!!) { abstractionBlock ->
            (ctx as OnLoadFragmentListener).loadAbsIndexFragment(abstractionBlock)
        }
        return view
    }

//    private fun firebaseListenerInit() {
//
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                Log.d("TAG", dataSnapshot.toString())
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//                // ...
//            }
//        }
//        mAbstractionBlockReference!!.addValueEventListener(postListener)
//
//        val childEventListener = object : ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                Log.d("TAG", p0.toString())
//            }
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val ebaWritingDrill = p0.getValue<EbaWritingDrill>(EbaWritingDrill::class.java)!!
//                Log.d("TAG", p0.toString())
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        }
//
//        mAbstractionBlockReference!!.addChildEventListener(childEventListener)
//
//        // copy for removing at onStop()
//        mAbstractionBlockListener = childEventListener
//    }

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

    internal inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title = itemView.findViewById<TextView>(R.id.title)
        // FirebaseRecyclerAdapter.onBindViewHolderでセット
        lateinit var itemClickListener: ItemClickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.onClick(v!!, adapterPosition, false)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        mAdapter!!.startListening()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mAdapter!!.stopListening()
//    }
}