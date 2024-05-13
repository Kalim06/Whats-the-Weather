package com.mkd.whatstheweather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.databinding.FragmentSettingsBinding
import com.mkd.whatstheweather.utils.PreferencesManager

class SettingsFragment : Fragment() {

    //Binding
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Spinner
        val units = arrayOf("Metric - °C", "Imperial - °F")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.unitSpinner.adapter = adapter

        //Get User Preference
        val preferencesManager = PreferencesManager(requireContext())
        val selectedUnit = preferencesManager.getUnit()
        val initialPosition = when (selectedUnit) {
            "metric" -> 0
            "imperial" -> 1
            else -> 0
        }

        binding.unitSpinner.setSelection(initialPosition)

        binding.unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val unit = when (position) {
                    0 -> "metric"
                    1 -> "imperial"
                    else -> return
                }
                preferencesManager.setUnit(unit)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Click Listener
        binding.favourites.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_favouriteFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}