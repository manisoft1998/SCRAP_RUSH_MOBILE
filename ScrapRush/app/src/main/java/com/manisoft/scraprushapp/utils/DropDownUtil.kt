package com.manisoft.scraprushapp.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.adapters.DropDownAdapter
import com.manisoft.scraprushapp.databinding.DropDownLayoutBinding
import kotlin.collections.ArrayList

class DropDownUtil(private val context: Context) {
    private lateinit var adapter: DropDownAdapter
    private var myDialog: Dialog = Dialog(context)
    private lateinit var binding: DropDownLayoutBinding

    init {
        myDialog.setCancelable(true)
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("SetTextI18n")
    fun showDropdown(selectedString: String, stringList: ArrayList<String>, dropDownType: String, listener: DropDownAdapter.DropDownItemClickListener) {
        val layoutInflater = LayoutInflater.from(context)
        binding = DropDownLayoutBinding.inflate(layoutInflater)
        myDialog.setContentView(binding.root)

        binding.dropDownTitleTv.text = "Select $dropDownType"

        if (stringList.isNotEmpty()) {
            binding.animationView.gone()
            binding.dropDownRv.visible()
            initAdapter(selectedString, stringList, listener, dropDownType)
        } else {
            showAnimation()
        }

        binding.closeAlertBox.setOnClickListener {
            myDialog.dismiss()
        }
        myDialog.show()
    }

    private fun initAdapter(selectedString: String, stringList: ArrayList<String>, listener: DropDownAdapter.DropDownItemClickListener, dropDownType: String) {
        adapter = DropDownAdapter(listener, dropDownType, stringList, selectedString)
        binding.dropDownRv.layoutManager = LinearLayoutManager(context)
        binding.dropDownRv.setHasFixedSize(true)
        binding.dropDownRv.adapter = adapter
    }


    fun dismiss() {
        myDialog.dismiss()
    }

    private fun showAnimation() {
        binding.animationView.visible()
        binding.dropDownRv.gone()
        binding.animationView.setAnimation(R.raw.no_data)
        binding.animationView.repeatCount = LottieDrawable.INFINITE
        binding.animationView.playAnimation()
    }

}


