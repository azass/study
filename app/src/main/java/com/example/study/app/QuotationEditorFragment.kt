package com.example.study.app

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.study.R
import com.example.study.closeKeyboard
import com.example.study.model.Quotation
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.QuotationsDao
import java.io.Serializable

class QuotationEditorFragment: Fragment() {

    private lateinit var quotation: Quotation

    companion object {
        fun newInstance(quotation: Quotation): QuotationEditorFragment {
            val args = Bundle()
            args.putSerializable("quotation", quotation as Serializable)
            val fragment = QuotationEditorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        quotation = arguments?.getSerializable("quotation") as Quotation

        val view = inflater.inflate(R.layout.fragment_quotation_editor, container, false)
        view.findViewById<TextView>(R.id.quoteIdLabel).setText("id:" + quotation._id)
        view.findViewById<EditText>(R.id.editQuote).setText(quotation.quote, TextView.BufferType.SPANNABLE)
        view.findViewById<EditText>(R.id.editAuthor).setText(quotation.author, TextView.BufferType.SPANNABLE)
        view.findViewById<EditText>(R.id.quoteStatus1).setText(quotation.status1.toString(), TextView.BufferType.EDITABLE)
        view.findViewById<EditText>(R.id.quoteStatus2).setText(quotation.status2.toString(), TextView.BufferType.EDITABLE)
        view.findViewById<EditText>(R.id.quoteStatus3).setText(quotation.status3.toString(), TextView.BufferType.EDITABLE)

        view.findViewById<Button>(R.id.saveQuotation).setOnClickListener {
            closeKeyboard(activity!!)
            quotation.quote = view.findViewById<EditText>(R.id.editQuote).text.toString()
            quotation.author = view.findViewById<EditText>(R.id.editAuthor).text.toString()
            val quoteStatus1 = view.findViewById<EditText>(R.id.quoteStatus1).text.toString()
            quotation.status1 = Integer.parseInt(quoteStatus1)
            quotation.status2 = Integer.parseInt(view.findViewById<EditText>(R.id.quoteStatus2).text.toString())
            quotation.status3 = Integer.parseInt(view.findViewById<EditText>(R.id.quoteStatus3).text.toString())

            val db = BlockStudyDatabase(context!!).writableDatabase
            val quotationDao = QuotationsDao(db)
            if (quotation._id == 0) {
                quotationDao.insert(quotation)
                quotation = quotationDao.select(quotation.quote)!!
                view.findViewById<TextView>(R.id.quoteIdLabel).setText("id:" + quotation._id)
            } else {
                quotationDao.update(quotation)
            }
            db.close()
            Toast.makeText(activity, "完了しました", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.deleteQuotation).setOnClickListener {
            closeKeyboard(activity!!)
            val db = BlockStudyDatabase(context!!).writableDatabase
            val quotationDao = QuotationsDao(db)
            quotationDao.delete(quotation)
            Toast.makeText(activity, "削除しました", Toast.LENGTH_SHORT).show()
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

}