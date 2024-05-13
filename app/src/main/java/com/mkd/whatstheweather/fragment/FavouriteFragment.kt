package com.mkd.whatstheweather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkd.whatstheweather.MyApplication
import com.mkd.whatstheweather.adapter.FavouriteAdapter
import com.mkd.whatstheweather.databinding.FragmentFavouriteBinding
import com.mkd.whatstheweather.viewModel.FavouriteViewModel
import com.mkd.whatstheweather.viewModelFactory.FavouriteViewModelFactory

class FavouriteFragment : Fragment() {

    //Binding
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    //ViewModel
    private val favouriteViewModel: FavouriteViewModel by activityViewModels {
        FavouriteViewModelFactory((activity?.application as MyApplication).database.favouriteDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setup RecyclerView
        val favouriteAdapter = FavouriteAdapter {
            val action =
                FavouriteFragmentDirections.actionFavouriteFragmentToDetailFragment(it.city)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = favouriteAdapter

        favouriteViewModel.favouriteCities.observe(this.viewLifecycleOwner) { cities ->
            cities.let {
                favouriteAdapter.submitList(it)
                binding.noFavourites.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}