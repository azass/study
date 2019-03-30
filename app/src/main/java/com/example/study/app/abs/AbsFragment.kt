package com.example.study.app.abs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.study.R

class AbsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // FrameLayout + BottomNavigationView
        val view = inflater.inflate(R.layout.fragment_abs, container, false)
        return view
    }
}