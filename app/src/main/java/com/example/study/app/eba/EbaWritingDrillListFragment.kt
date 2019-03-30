package com.example.study.app.eba

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.R
import com.example.study.app.ItemClickListener
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.eba.EbaWritingDrill
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*


class EbaWritingDrillListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var mWritingDrillReference: DatabaseReference? = null
    private var mWritingDrillListener: ChildEventListener? = null

    private var mAdapter: FirebaseRecyclerAdapter<EbaWritingDrill, EbaWritingDrillViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "100字訓練一覧"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_eba_writing_drill_list, container, false)
        val ctx = context ?: return view

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.ebaWritingDrillList)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)) // 枠線

        val database = FirebaseDatabase.getInstance().reference
        mWritingDrillReference = database.child("EBA_WritingDrill")
        firebaseListenerInit()

        val query = mWritingDrillReference!!
        val options = FirebaseRecyclerOptions.Builder<EbaWritingDrill>()
            .setQuery(query, EbaWritingDrill::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<EbaWritingDrill, EbaWritingDrillViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbaWritingDrillViewHolder {
                val view = inflater.inflate(R.layout.eba_writing_drill_row, parent, false)
                return EbaWritingDrillViewHolder(view)
            }

            override fun onBindViewHolder(holder: EbaWritingDrillViewHolder, position: Int, model: EbaWritingDrill) {
                holder.ebaWritingDrillRowAskDate.text = model.askDate
                holder.ebaWritingDrillRowQ.text = model.question

                // 行をクリックしたら画面遷移
                val itemClickListener = object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                        (context as OnLoadFragmentListener).replaceEbaScreen(EbaWritingDrillQFragment.newInstance(model), "")
                    }
                }
                holder.itemClickListener = itemClickListener
            }
        }

        recyclerView.adapter = mAdapter
        return view
    }
    private fun firebaseListenerInit() {

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val ebaWritingDrill = p0.getValue<EbaWritingDrill>(EbaWritingDrill::class.java)!!
                Log.d("TAG", p0.toString())
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        mWritingDrillReference!!.addChildEventListener(childEventListener)

        // copy for removing at onStop()
        mWritingDrillListener = childEventListener
    }

    // ビューホルダー
    internal inner class EbaWritingDrillViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val ebaWritingDrillRowAskDate = itemView.findViewById<TextView>(R.id.ebaWritingDrillRowAskDate)
        val ebaWritingDrillRowQ = itemView.findViewById<TextView>(R.id.ebaWritingDrillRowQ)

        // FirebaseRecyclerAdapter.onBindViewHolderでセット
        lateinit var itemClickListener: ItemClickListener

        init { itemView.setOnClickListener(this) }

        override fun onClick(v: View?) {
            itemClickListener.onClick(v!!, adapterPosition, false)
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }
}