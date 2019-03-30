package com.example.study.app.eba

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
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.study.R
import com.example.study.app.ItemClickListener
import com.example.study.createEditDialog
import com.example.study.model.eba.EbaWebLecture
import com.example.study.model.eba.EbaWebMemo
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.eba.EbaWebMemoDao
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.eba_memo_dialog.view.*
import kotlinx.android.synthetic.main.eba_memo_title_dialog.view.*
import java.io.Serializable

class EbaMemoListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var mAdapter: FirebaseRecyclerAdapter<EbaWebMemo, EbaMemoListViewHolder>? = null
    companion object {
        fun newInstance(ebaWebLecture: EbaWebLecture): EbaMemoListFragment {
            val args = Bundle()
            args.putSerializable("EbaWebLecture", ebaWebLecture as Serializable)
            val fragment = EbaMemoListFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // データのレジーインスタンシエーション
        val ebaWebLecture = arguments?.getSerializable("EbaWebLecture") as EbaWebLecture
//        val db = BlockStudyDatabase(context!!).readableDatabase
//        val dao = EbaWebMemoDao(db)
//        val list = dao.find("url_id = ?", arrayOf(ebaWebMemo.url_id.toString()), "seekTime")

        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = ebaWebLecture.title
        // ビュー
        val view = inflater.inflate(R.layout.fragment_eba_memo_list, container, false)
        val ctx = context ?: return view

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.ebaMemoList)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)) // 枠線
//        recyclerView.adapter = EbaMemoListAdapter(ctx, ebaWebLecture.memoList) { webMemo ->
//            (context as OnLoadFragmentListener).replaceEbaScreen(EbaMemoTitlesFragment(), "")
//        }

        val database = FirebaseDatabase.getInstance().reference
        val webMemoRef = database.child("EBA_WebLecture").child(ebaWebLecture.key + "/memoList")

        val query = webMemoRef!!
        val options = FirebaseRecyclerOptions.Builder<EbaWebMemo>()
            .setQuery(query, EbaWebMemo::class.java)
            .build()

        mAdapter = object : FirebaseRecyclerAdapter<EbaWebMemo, EbaMemoListViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbaMemoListViewHolder {
                val view = inflater.inflate(R.layout.eba_memo_list_row, parent, false)
                return EbaMemoListViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: EbaMemoListViewHolder, position: Int, webMemo: EbaWebMemo) {

                holder.seekTimeOfMemo.text = webMemo.seekTime
                holder.ebaMemo.text = webMemo.memo
                holder.editMemo.setOnClickListener {
                    val popup = PopupMenu(context, holder.editMemo)
                    popup.inflate(R.menu.eba_memo_title_option)
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.eba_memo_title_edit -> {
                                val dialogView = LayoutInflater.from(context).inflate(R.layout.eba_memo_dialog, null)
                                dialogView.ebaMemoDialogTitle.text = webMemo.title
                                dialogView.ebaMemoSeekTime.setText(webMemo.seekTime, TextView.BufferType.EDITABLE)
                                dialogView.ebaMemoMemo.setText(webMemo.memo, TextView.BufferType.EDITABLE)
                                val dialog = createEditDialog(context!!, dialogView, "")
                                dialogView.saveEbaMemoDialog!!.setOnClickListener {
                                    dialog.dismiss()
                                    val seekTime = dialogView.ebaMemoSeekTime.text.toString()
                                    val memo = dialogView.ebaMemoMemo.text.toString()
                                    if (seekTime != webMemo.seekTime || memo != webMemo.memo) {
                                        val db = BlockStudyDatabase(context!!).writableDatabase
                                        val dao = EbaWebMemoDao(db)
                                        webMemo.seekTime = seekTime
                                        webMemo.memo = memo
                                        dao.update(webMemo)
                                        db.close()
                                        notifyDataSetChanged()
                                    }
                                    Toast.makeText(context, "完了", Toast.LENGTH_SHORT).show()
                                }
                            }
                            R.id.eba_memo_title_delete -> {
                                val db = BlockStudyDatabase(context!!).writableDatabase
                                val dao = EbaWebMemoDao(db)
                                dao.delete(webMemo)
                                db.close()
                                notifyItemRemoved(position)
                            }
                        }
                        true
                    }
                    popup.show()
                }
                // 行をクリックしたら画面遷移
                val itemClickListener = object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
//                        (context as OnLoadFragmentListener).replaceEbaScreen(EbaMemoListFragment.newInstance(model), "")
                    }
                }
                holder.itemClickListener = itemClickListener
            }
        }
        recyclerView.adapter = mAdapter
        return view
    }
    // アダプタクラス
    internal inner class EbaMemoListAdapter(
        private val context: Context,
        private val ebaWebMemoList: List<EbaWebMemo>,
        private val onTitleClicked: (EbaWebMemo) -> Unit) : RecyclerView.Adapter<EbaMemoListViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbaMemoListViewHolder {
            val view = inflater.inflate(R.layout.eba_memo_list_row, parent, false)
            val viewHolder = EbaMemoListViewHolder(view)

            view.setOnClickListener {
                val webMemo = ebaWebMemoList[viewHolder.adapterPosition]
                onTitleClicked(webMemo)
            }
            return viewHolder
        }

        override fun getItemCount() = ebaWebMemoList.size

        override fun onBindViewHolder(holder: EbaMemoListViewHolder, position: Int) {
            val webMemo = ebaWebMemoList[position]
            holder.seekTimeOfMemo.text = webMemo.seekTime
            holder.ebaMemo.text = webMemo.memo
            holder.editMemo.setOnClickListener {
                val popup = PopupMenu(context, holder.editMemo)
                popup.inflate(R.menu.eba_memo_title_option)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.eba_memo_title_edit -> {
                            val dialogView = LayoutInflater.from(context).inflate(R.layout.eba_memo_dialog, null)
                            dialogView.ebaMemoDialogTitle.text = webMemo.title
                            dialogView.ebaMemoSeekTime.setText(webMemo.seekTime, TextView.BufferType.EDITABLE)
                            dialogView.ebaMemoMemo.setText(webMemo.memo, TextView.BufferType.EDITABLE)
                            val dialog = createEditDialog(context!!, dialogView, "")
                            dialogView.saveTitle!!.setOnClickListener {
                                dialog.dismiss()
                                val seekTime = dialogView.ebaMemoSeekTime.text.toString()
                                val memo = dialogView.ebaMemoMemo.text.toString()
                                if (seekTime != webMemo.seekTime || memo != webMemo.memo) {
                                    val db = BlockStudyDatabase(context!!).writableDatabase
                                    val dao = EbaWebMemoDao(db)
                                    webMemo.seekTime = seekTime
                                    webMemo.memo = memo
                                    dao.update(webMemo)
                                    db.close()
                                    notifyDataSetChanged()
                                }
                                Toast.makeText(context, "完了", Toast.LENGTH_SHORT).show()
                            }
                        }
                        R.id.eba_memo_title_delete -> {
                            val db = BlockStudyDatabase(context!!).writableDatabase
                            val dao = EbaWebMemoDao(db)
                            dao.delete(webMemo)
                            db.close()
                            notifyItemRemoved(position)
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }
    // ビューホルダー
    internal inner class EbaMemoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val seekTimeOfMemo = itemView.findViewById<TextView>(R.id.seekTimeOfMemo)
        val ebaMemo = itemView.findViewById<TextView>(R.id.ebaMemo)
        val editMemo = itemView.findViewById<Button>(R.id.editMemo)

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
    }}