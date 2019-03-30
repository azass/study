package com.example.study.app.eba

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.R
import com.example.study.model.eba.EbaWritingDrill
import kotlinx.android.synthetic.main.fragment_eba_writing_drill.view.*
import java.io.Serializable

class EbaWritingDrillQFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val KEY = "EbaWritingDrill"

        fun newInstance(ebaWritingDrill: EbaWritingDrill): EbaWritingDrillQFragment {
            val args = Bundle()
            args.putSerializable(KEY, ebaWritingDrill as Serializable)
            val fragment = EbaWritingDrillQFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val ebaWritingDrill = arguments?.getSerializable(KEY) as EbaWritingDrill
        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "100字訓練"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_eba_writing_drill, container, false)
        val ctx = context ?: return view
        // 画面のタイトル
        view.findViewById<TextView>(R.id.ebaWritingDrillQ).text = ebaWritingDrill.question

        view.ebaWritingDrillViewPager.adapter = EbaWritingDrillViewPagerAdapter(fragmentManager!!, ebaWritingDrill)

        return view
    }

    // アダプタクラス
    internal inner class EbaWritingDrillViewPagerAdapter(
        fm: FragmentManager, private val ebaWritingDrill: EbaWritingDrill) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return EbaWritingDrillAFragment.newInstance(ebaWritingDrill.answers[position])
        }

        override fun getCount(): Int {
            return ebaWritingDrill.answers.size
        }
    }
}