package com.example.study.app.eba

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.R

class EbaWritingDrillAFragment : Fragment() {
    companion object {
        private val PAGE = "PAGE"
        // PageFragment生成
        fun newInstance(page: String): EbaWritingDrillAFragment {
            val pageFragment = EbaWritingDrillAFragment()
            val bundle = Bundle()
            bundle.putString(PAGE, page)
            pageFragment.arguments = bundle
            return pageFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val page = arguments?.getSerializable(PAGE) as String
        val view = inflater.inflate(R.layout.fragment_eba_writing_drill_answer, container, false)
        val ctx = context ?: return view
        // 画面のタイトル
        view.findViewById<TextView>(R.id.ebaWritingDrillA).text = page
        return view
    }
}