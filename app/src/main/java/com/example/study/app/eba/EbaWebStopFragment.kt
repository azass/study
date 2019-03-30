package com.example.study.app.eba

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.R

class EbaWebStopFragment: Fragment() {

    companion object {
        fun newInstance(url: String, watchTime: String): EbaWebStopFragment {
            val args = Bundle()
            args.putSerializable("url", url)
            args.putSerializable("watchTime", watchTime)
            val fragment = EbaWebStopFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ebaWeb = inflater.inflate(R.layout.fragment_eba_web_stop, container,false)
        val ebaWebTitle = ebaWeb.findViewById<TextView>(R.id.ebaWebTitle)
        ebaWebTitle.setText(arguments?.getSerializable("url") as String)
        val ebaWatchTime = ebaWeb.findViewById<TextView>(R.id.ebaWatchTime)
        ebaWatchTime.setText(arguments?.getSerializable("watchTime").toString())
        return ebaWeb
    }

}