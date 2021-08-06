package com.smitcoderx.learn.trippin_business.UI.Dialogs

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.databinding.FragmentBottomTypeBinding


class BottomTypeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomTypeBinding
    private lateinit var data: PassData
    private lateinit var type: String
    private lateinit var tiOtherType: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_type, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomTypeBinding.bind(view)

        tiOtherType = binding.tiTypeOther

        binding.ivCancel.setOnClickListener {
            dismiss()
        }

        getTextfromChip()

        binding.submit.setOnClickListener {
            data.onPassType(type)
            dismiss()
        }

    }

    private fun getTextfromChip() {

        val chipGroup: ChipGroup = binding.chipGroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.tiTypeOther.isEnabled = false
                    type = chip.text.toString().trim()
                    if (chip.text.toString() == "Others") {
                        tiOtherType.isEnabled = true
                        tiOtherType.editText!!.setOnFocusChangeListener { _, hasFocus ->
                            if (!hasFocus) {
                                chip.text = tiOtherType.editText!!.text.toString().trim()
                            }
                        }
                    } else {
                        binding.tiTypeOther.isEnabled = false
                    }
                }
            }
        }
    }


    interface PassData {
        fun onPassType(type: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            data = targetFragment as PassData
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PassData")
        }


    }
}