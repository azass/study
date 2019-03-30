package com.example.study.app

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.R
import com.example.study.model.Quotation
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.QuotationsDao

class QuotationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_quotations, container, false)

        val ctx = context ?: return view
        val db = BlockStudyDatabase(ctx).readableDatabase
        val dao = QuotationsDao(db)
        val quotationList = dao.selectAll()
        db.close()

        recyclerView = view.findViewById(R.id.quotationList)

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = QuotationAdapter(ctx, quotationList) { quotation ->
            (ctx as OnLoadFragmentListener).loadQuotationEditor(quotation)
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            (ctx as OnLoadFragmentListener).loadQuotationEditor(Quotation(0, "", "", 0, 0, 0, 0))
        }
        return view
    }

    // Fragmentでオプションメニューを使用できるようにする
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
    // オプションメニューをクリックした時
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (activity as OnLoadFragmentListener).loadBlockItemListFragment()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    internal inner class QuotationAdapter(
        private val context: Context,
        private val quotationList: List<Quotation>,
        private val onQuotationClicked: (Quotation) -> Unit
    ) : RecyclerView.Adapter<QuotationViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationViewHolder {
            val view = inflater.inflate(R.layout.quotation_row, parent, false)
            val viewHolder = QuotationViewHolder(view)

            view.setOnClickListener {
                val quotation = quotationList[viewHolder.adapterPosition]
                onQuotationClicked(quotation)
            }
            return viewHolder
        }

        override fun getItemCount() = quotationList.size

        override fun onBindViewHolder(holder: QuotationViewHolder, position: Int) {
            val quotation = quotationList[position]
            holder.quote_id.text = quotation._id.toString()
            holder.quote.text = quotation.quote
            holder.author.text = quotation.author
            holder.likeCount.text = quotation.likeCount.toString()
        }
    }

    internal inner class QuotationViewHolder(quotationView: View) : RecyclerView.ViewHolder(quotationView) {
        val quote_id = quotationView.findViewById<TextView>(R.id.quote_id)
        val quote = quotationView.findViewById<TextView>(R.id.quote)
        val author = quotationView.findViewById<TextView>(R.id.author)
        val likeCount = quotationView.findViewById<TextView>(R.id.likeCount)
    }
}