package com.example.study.app.abs

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.study.BLOCKSHEET_MANAGER
import com.example.study.R
import com.example.study.app.OnLoadFragmentListener
import com.example.study.model.abs.BlockSheetManager
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.Serializable

class AbsMenuFragment : Fragment() {

    private lateinit var blockSheetManager: BlockSheetManager

    companion object {
        fun newInstance(blockSheetManager: BlockSheetManager): AbsMenuFragment {
            val args = Bundle()
            args.putSerializable(BLOCKSHEET_MANAGER, blockSheetManager as Serializable)
            val fragment = AbsMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_abs_menu, container, false)
        blockSheetManager = arguments?.getSerializable(BLOCKSHEET_MANAGER) as BlockSheetManager

        val normalMenuButton = view.findViewById<Button>(R.id.normalMenu)
        normalMenuButton.setOnClickListener {
            (activity as OnLoadFragmentListener).loadAbstractionBlockMenu()
        }

        val forgettingsButton = view.findViewById<Button>(R.id.forgettingMenu)
        forgettingsButton.setOnClickListener {
            (activity as OnLoadFragmentListener).loadForgettingFragment()
        }

        val wastingsButton = view.findViewById<Button>(R.id.wastingMenu)
        wastingsButton.setOnClickListener {
            (activity as OnLoadFragmentListener).loadWastingFragment()
        }

        val navigation = activity!!.findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(activity as BottomNavigationView.OnNavigationItemSelectedListener)
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as OnLoadFragmentListener).loadActionBarToggle()
    }

    override fun onPause() {
        super.onPause()
        val toolbar = (activity as AppCompatActivity).toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
    }
}