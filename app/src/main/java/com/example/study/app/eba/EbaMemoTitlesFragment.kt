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
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.study.R
import com.example.study.app.ItemClickListener
import com.example.study.app.OnLoadFragmentListener
import com.example.study.createEditDialog
import com.example.study.model.eba.EbaWebLecture
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.eba.EbaWebMemoDao
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.eba_memo_title_dialog.view.*

class EbaMemoTitlesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private var mWebLectureReference: DatabaseReference? = null
    private var mWebLectureListener: ChildEventListener? = null

    private var mAdapter: FirebaseRecyclerAdapter<EbaWebLecture, EbaWebLectureViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // データのレジーインスタンシエーション
//        val db = BlockStudyDatabase(context!!).readableDatabase
//        val dao = EbaWebMemoDao(db)
//        val list = dao.selectAllUrl()

        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "講義一覧"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_eba_memo_titles, container, false)
        val ctx = context ?: return view

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.ebaMemoTitles)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)) // 枠線
//        recyclerView.adapter = EbaMemoTitlesAdapter(ctx, list) { webMemo ->
//            (context as OnLoadFragmentListener).replaceEbaScreen(EbaMemoListFragment.newInstance(webMemo), "")
//        }

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
                holder.titleOption.setOnClickListener {
                    val popup = PopupMenu(context, holder.titleOption)
                    popup.inflate(R.menu.eba_memo_title_option)
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.eba_memo_title_edit -> {
                                editMemoTitle(model)
                            }
                            R.id.eba_memo_title_delete -> {
                                deleteMemoTitle(position, model)
                            }
                        }
                        true
                    }
                    popup.show()
                }
                // 行をクリックしたら画面遷移
                val itemClickListener = object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                        (context as OnLoadFragmentListener).replaceEbaScreen(EbaMemoListFragment.newInstance(model), "")
                    }
                }
                holder.itemClickListener = itemClickListener
            }
        }

        recyclerView.adapter = mAdapter
        return view
    }

    private fun editMemoTitle(model: EbaWebLecture) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.eba_memo_title_dialog, null)
        dialogView.urlOfTitleEditor.text = model.url
        dialogView.editUrlTitle.setText(model.title, TextView.BufferType.EDITABLE)

        val dialog = createEditDialog(context!!, dialogView, "")
        dialogView.saveTitle!!.setOnClickListener {
            dialog.dismiss()
            val title = dialogView.editUrlTitle.text.toString()
            if (title != model.title) {
                val db = BlockStudyDatabase(context!!).writableDatabase
                val dao = EbaWebMemoDao(db)
                model.title = title
                dao.updateUrl(model._id, title)
                db.close()
                mAdapter!!.notifyDataSetChanged()
            }
            Toast.makeText(context, "完了", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMemoTitle(position: Int, model: EbaWebLecture) {
        val db = BlockStudyDatabase(context!!).writableDatabase
        val dao = EbaWebMemoDao(db)
        dao.deleteUrl(model._id)
        db.close()
        mAdapter!!.notifyItemRemoved(position)
    }

    // アダプタクラス
//    internal inner class EbaMemoTitlesAdapter(
//        private val context: Context,
//        private val ebaWebMemoList: List<EbaWebMemo>,
//        private val onTitleClicked: (EbaWebMemo) -> Unit) : RecyclerView.Adapter<EbaMemoTitleViewHolder>() {
//
//        private val inflater = LayoutInflater.from(context)
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbaMemoTitleViewHolder {
//            val view = inflater.inflate(R.layout.eba_memo_title_row, parent, false)
//            val viewHolder = EbaMemoTitleViewHolder(view)
//
//            view.setOnClickListener {
//                val webMemo = ebaWebMemoList[viewHolder.adapterPosition]
//                onTitleClicked(webMemo)
//            }
//            return viewHolder
//        }
//
//        override fun getItemCount() = ebaWebMemoList.size
//
//        override fun onBindViewHolder(holder: EbaMemoTitleViewHolder, position: Int) {
//            val webMemo = ebaWebMemoList[position]
//            holder.title.text = webMemo.title
//            holder.titleOption.setOnClickListener {
//                val popup = PopupMenu(context, holder.titleOption)
//                popup.inflate(R.menu.eba_memo_title_option)
//                popup.setOnMenuItemClickListener {
//                    when (it.itemId) {
//                        R.id.eba_memo_title_edit -> {
//                            val dialogView = LayoutInflater.from(context).inflate(R.layout.eba_memo_title_dialog, null)
//                            dialogView.urlOfTitleEditor.text = webMemo.url
//                            dialogView.editUrlTitle.setText(webMemo.title, TextView.BufferType.EDITABLE)
//
//                            val dialog = createEditDialog(context!!, dialogView, "")
//                            dialogView.saveTitle!!.setOnClickListener {
//                                dialog.dismiss()
//                                val title = dialogView.editUrlTitle.text.toString()
//                                if (title != webMemo.title) {
//                                    val db = BlockStudyDatabase(context!!).writableDatabase
//                                    val dao = EbaWebMemoDao(db)
//                                    webMemo.title = title
//                                    dao.updateUrl(webMemo.url_id, title)
//                                    db.close()
//                                    notifyDataSetChanged()
//                                }
//                                Toast.makeText(context, "完了", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        R.id.eba_memo_title_delete -> {
//                            val db = BlockStudyDatabase(context!!).writableDatabase
//                            val dao = EbaWebMemoDao(db)
//                            dao.deleteUrl(webMemo.url_id)
//                            db.close()
//                            notifyItemRemoved(position)
//                        }
//                    }
//                    true
//                }
//                popup.show()
//            }
//        }
//    }
    // ビューホルダー
//    internal inner class EbaMemoTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title = itemView.findViewById<TextView>(R.id.eba_memo_title)
//        val titleOption = itemView.findViewById<TextView>(R.id.eba_memo_title_option)
//    }

    // ビューホルダー
    internal inner class EbaWebLectureViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val title = itemView.findViewById<TextView>(R.id.eba_memo_title)
        val titleOption = itemView.findViewById<TextView>(R.id.eba_memo_title_option)

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