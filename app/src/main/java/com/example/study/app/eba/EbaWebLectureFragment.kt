package com.example.study.app.eba

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
import com.example.study.R
import com.example.study.app.ItemClickListener
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.eba.EbaWebLecture
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EbaWebLectureFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var mWebLectureReference: DatabaseReference? = null
    private var mWebLectureListener: ChildEventListener? = null

    private var mAdapter: FirebaseRecyclerAdapter<EbaWebLecture, EbaWebLectureViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "講義一覧"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_eba_memo_titles, container, false)
        val ctx = context ?: return view

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.ebaMemoTitles)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)) // 枠線

        val database = FirebaseDatabase.getInstance().reference
        mWebLectureReference = database.child("EBA_WebLecture")

        val query = mWebLectureReference!!
        val options = FirebaseRecyclerOptions.Builder<EbaWebLecture>()
            .setQuery(query, EbaWebLecture::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<EbaWebLecture, EbaWebLectureViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbaWebLectureViewHolder {
                val view = inflater.inflate(R.layout.eba_memo_title_row, parent, false)
                return EbaWebLectureViewHolder(view)
            }

            override fun onBindViewHolder(holder: EbaWebLectureViewHolder, position: Int, model: EbaWebLecture) {
                holder.title.text = model.title

                // 行をクリックしたら画面遷移
                val itemClickListener = object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                        (ctx as OnLoadFragmentListener).replaceEbaScreen(EbaWebFragment.newInstance(model), "")
                    }
                }
                holder.itemClickListener = itemClickListener
            }
        }

        recyclerView.adapter = mAdapter
        return view
    }

    // ビューホルダー
    internal inner class EbaWebLectureViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val title = itemView.findViewById<TextView>(R.id.eba_memo_title)

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