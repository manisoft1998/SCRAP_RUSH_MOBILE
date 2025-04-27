package com.manisoft.scraprushapp.ui.profile.termsandconditions

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.manisoft.scraprushapp.databinding.FragmentTermsAndConditionsBinding
import com.manisoft.scraprushapp.utils.PrivacyPolicy

class TermsAndConditionsFragment : Fragment() {
    private lateinit var binding: FragmentTermsAndConditionsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(PrivacyPolicy.getTermsAndConditions(), Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(PrivacyPolicy.getTermsAndConditions())
        }
    }
}