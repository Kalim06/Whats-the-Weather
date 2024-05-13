package com.mkd.whatstheweather.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.adapter.SearchAdapter
import com.mkd.whatstheweather.api.ApiResultHandler
import com.mkd.whatstheweather.databinding.FragmentSearchBinding
import com.mkd.whatstheweather.model.City
import com.mkd.whatstheweather.model.CityResponse
import com.mkd.whatstheweather.utils.NetworkUtils
import com.mkd.whatstheweather.utils.hideKeyboard
import com.mkd.whatstheweather.utils.showKeyboard
import com.mkd.whatstheweather.utils.showSnackbar
import com.mkd.whatstheweather.viewModel.SearchViewModel

class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener {

    //Binding
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    //ViewModel
    private lateinit var viewModel: SearchViewModel

    //Adapter
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        //Adapter
        adapter = SearchAdapter(this)

        //RecyclerView
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter = adapter

        //Set Focus
        binding.searchCityInput.requestFocus()
        // Show the keyboard
        showKeyboard(binding.searchCityInput)

        //Search Input Listener
        binding.searchCityInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && s.toString().length > 2) {
                    checkInternet(s.toString())
                } else if (s.toString().isEmpty() || s.toString().length <= 2) {
                    adapter.clearData()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.noInternetView.checkInternetBtn.setOnClickListener {
            checkInternet(binding.searchCityInput.text.toString())
        }
    }

    private fun checkInternet(cityName: String) {
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            searchCities(cityName.trimEnd())
            binding.noInternetView.root.visibility = View.GONE
            binding.searchRecyclerView.visibility = View.VISIBLE
            binding.searchCityInput.isEnabled = true
            showKeyboard(binding.searchCityInput)
        } else {
            binding.noInternetView.root.visibility = View.VISIBLE
            binding.searchRecyclerView.visibility = View.GONE
            binding.searchCityInput.isEnabled = false
            hideKeyboard(binding.searchCityInput)
        }
    }

    private fun searchCities(cityName: String) {

        viewModel.getCities(cityName).observe(viewLifecycleOwner) { result ->

            val apiResultHandler = ApiResultHandler<CityResponse?>(onSuccess = {

                if (it != null) {
                    if (it.count > 0) {
                        if (it.list.isNullOrEmpty()) {
                            showSnackbar(
                                binding.root,
                                getString(R.string.something_went_wrong_pls_try_again)
                            )
                        } else {
                            binding.searchRecyclerView.visibility = View.VISIBLE
                            binding.noCityFound.visibility = View.GONE

                            val citiesList = it.list
                            adapter.setData(citiesList)
                        }
                    } else {
                        binding.searchRecyclerView.visibility = View.GONE
                        binding.noCityFound.visibility = View.VISIBLE
                    }
                }
            }, onFailure = {
                showSnackbar(
                    binding.root,
                    getString(R.string.something_went_wrong_pls_try_again)
                )
            })

            if (result != null) {
                apiResultHandler.handleApiResult(result)
                apiResultHandler.loading.observe(viewLifecycleOwner) {
                    //show loading
                    binding.loader.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onItemClick(city: City) {
        val cityName = "${city.name}, ${city.sys?.country}"
        val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(cityName)
        findNavController().navigate(action)
    }
}