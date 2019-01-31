package com.example.study

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuFragment : Fragment() {

    private lateinit var blockMenuList: List<BlockMenu>

    companion object {
        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }

    //
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val json: String = context!!.assets.open("data.json").bufferedReader().use { it.readText() }

        val gson = Gson()
        val typeToken = object : TypeToken<List<BlockMenu>>() {}
        blockMenuList = gson.fromJson<List<BlockMenu>>(json, typeToken.type)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val normalMenuButton = view.findViewById<Button>(R.id.normalMenu)
        normalMenuButton.setOnClickListener {
            getFragmentManager()!!
                .beginTransaction()
                .replace(R.id.root_layout, NormalMenuFragment.newInstance(blockMenuList), "normalMenuList")
                .addToBackStack(null)
                .commit()
        }

        val blockSheetMager = BlockSheetManager(blockMenuList)
        blockSheetMager.setupBlockSheetTable()

        val forgettingsButton = view.findViewById<Button>(R.id.forgettingMenu)
        forgettingsButton.setOnClickListener {
            getFragmentManager()!!
                .beginTransaction()
                .replace(R.id.root_layout, ForgettingsFragment.newInstance(blockSheetMager), "forgettings")
                .addToBackStack(null)
                .commit()
        }

        val wastingsButton = view.findViewById<Button>(R.id.wastingMenu)
        wastingsButton.setOnClickListener {
            getFragmentManager()!!
                .beginTransaction()
                .replace(R.id.root_layout, WastingsFragment.newInstance(blockSheetMager), "wastings")
                .addToBackStack(null)
                .commit()
        }
        return view
    }

}