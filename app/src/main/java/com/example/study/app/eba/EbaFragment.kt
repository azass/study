package com.example.study.app.eba

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.study.R

class EbaFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_eba, container, false)

        val navigation = view!!.findViewById<BottomNavigationView>(R.id.naviEba)
        navigation.setOnNavigationItemSelectedListener(activity as BottomNavigationView.OnNavigationItemSelectedListener)

        return view
    }
}