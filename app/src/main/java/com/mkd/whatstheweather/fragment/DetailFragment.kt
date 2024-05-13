package com.mkd.whatstheweather.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mkd.whatstheweather.MyApplication
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.api.ApiResultHandler
import com.mkd.whatstheweather.databinding.FragmentDetailBinding
import com.mkd.whatstheweather.model.WeatherDetail
import com.mkd.whatstheweather.model.WeatherResponse
import com.mkd.whatstheweather.room.Favourite
import com.mkd.whatstheweather.utils.NetworkUtils
import com.mkd.whatstheweather.utils.PreferencesManager
import com.mkd.whatstheweather.utils.convertTimeStamp
import com.mkd.whatstheweather.utils.formatTemperatureImperial
import com.mkd.whatstheweather.utils.formatTemperatureMetric
import com.mkd.whatstheweather.utils.formatWindSpeedImperial
import com.mkd.whatstheweather.utils.formatWindSpeedMetric
import com.mkd.whatstheweather.utils.getFormattedTime
import com.mkd.whatstheweather.utils.showSnackbar
import com.mkd.whatstheweather.viewModel.DetailViewModel
import com.mkd.whatstheweather.viewModel.FavouriteViewModel
import com.mkd.whatstheweather.viewModelFactory.FavouriteViewModelFactory

class DetailFragment : Fragment() {

    //Binding
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    //Args
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var city: String

    //ViewModel
    private lateinit var viewModel: DetailViewModel
    private val favouriteViewModel: FavouriteViewModel by activityViewModels {
        FavouriteViewModelFactory((activity?.application as MyApplication).database.favouriteDao())
    }

    //Variable
    private var currentWeather: WeatherResponse? = null
    private var favouriteCity: Favourite? = null
    private lateinit var preferencesManager: PreferencesManager
    private var shareText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get city from args
        city = args.city

        //Get preferences
        preferencesManager = PreferencesManager(requireContext())

        //Init viewModel
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        //Set city name
        binding.cityTitle.text = city

        //Check internet and get weather
        checkInternet()

        //Check if city is favourite
        favouriteViewModel.retrieveFavouriteByName(city)
            .observe(this.viewLifecycleOwner) { selectedItem ->
                if (selectedItem != null) {
                    binding.addFavorite.visibility = View.GONE
                    binding.removeFavorite.visibility = View.VISIBLE
                    favouriteCity = selectedItem
                } else {
                    binding.addFavorite.visibility = View.VISIBLE
                    binding.removeFavorite.visibility = View.GONE
                    favouriteCity = null
                }
            }

        //Add to favourite
        binding.addFavorite.setOnClickListener {
            favouriteViewModel.addCity(city)
            showSnackbar(binding.root, "Added to Favourites")
        }

        //Remove from favourite
        binding.removeFavorite.setOnClickListener {
            favouriteCity?.let { it1 -> favouriteViewModel.deleteFavourite(it1) }
            favouriteCity = null
            showSnackbar(binding.root, "Removed from Favourites")
        }

        //Back button
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.noInternetView.checkInternetBtn.setOnClickListener {
            checkInternet()
        }

        binding.shareBtn.setOnClickListener {
            shareWeather()
        }
    }

    //Check internet connection
    private fun checkInternet() {
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            getWeatherByCityName(city)
            binding.noInternetView.root.visibility = View.GONE
            binding.backBtn.isEnabled = true
            binding.weatherDetailView.visibility = View.VISIBLE
        } else {
            binding.noInternetView.root.visibility = View.VISIBLE
            binding.backBtn.isEnabled = false
            binding.weatherDetailView.visibility = View.GONE
        }
    }

    private fun getWeatherByCityName(cityName: String) {

        viewModel.getWeatherByCityName(cityName).observe(viewLifecycleOwner) { result ->

            val apiResultHandler = ApiResultHandler<WeatherResponse?>(onSuccess = {
                if (it != null) {
                    currentWeather = it
                    val selectedUnit = preferencesManager.getUnit()
                    when (selectedUnit) {
                        "metric" -> setDataInMetric(it)
                        "imperial" -> setDataInImperial(it)
                        else -> {
                            setDataInMetric(it)
                        }
                    }
                } else {
                    showSnackbar(
                        binding.root,
                        getString(R.string.something_went_wrong_pls_try_again)
                    )
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

    private fun setDataInMetric(weatherResponse: WeatherResponse) {

        //Show Loading
        binding.loader.visibility = View.VISIBLE

        //Set Values
        val time = weatherResponse.dt?.let { it1 -> convertTimeStamp(it1.toLong()) }
        val name = weatherResponse.name
        val iconCode = weatherResponse.weather?.get(0)?.icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        val temp = weatherResponse.main?.temp
        val feelsLike = weatherResponse.main?.feels_like
        val description = weatherResponse.weather?.get(0)?.main
        val humidity = weatherResponse.main?.humidity.toString()
        val windSpeed = weatherResponse.wind?.speed.toString()

        //Set Values based on unit used
        val celsiusTemp = formatTemperatureMetric(temp as Double)
        val feelsLikeTemp = formatTemperatureMetric(feelsLike as Double)
        val windSpeedUnit = formatWindSpeedMetric(windSpeed.toDouble())

        //Set sunrise and sunset
        val sunrise = weatherResponse.sys?.sunrise
        val sunriseTime = sunrise?.let { it1 -> getFormattedTime(it1.toLong()) }
        val sunset = weatherResponse.sys?.sunset
        val sunsetTime = sunset?.let { it1 -> getFormattedTime(it1.toLong()) }

        //Set weather detail
        val weatherDetail = WeatherDetail(
            time!!,
            name!!,
            iconUrl,
            description!!,
            celsiusTemp,
            feelsLikeTemp,
            humidity,
            windSpeedUnit,
            sunriseTime!!,
            sunsetTime!!
        )

        //Hide Loading
        binding.loader.visibility = View.GONE

        //Set weather detail
        binding.weather = weatherDetail

        //Set Share Text
        shareText =
            "Hey! I just checked the weather in $name is $celsiusTemp although it feels like $feelsLikeTemp. The weather is $description. \n-Data by What's the Weather App by Kalim Dalwai"
    }

    private fun setDataInImperial(weatherResponse: WeatherResponse) {

        //Show Loading
        binding.loader.visibility = View.VISIBLE

        //Set Values
        val time = weatherResponse.dt?.let { it1 -> convertTimeStamp(it1.toLong()) }
        val name = weatherResponse.name
        val iconCode = weatherResponse.weather?.get(0)?.icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"
        val temp = weatherResponse.main?.temp
        val feelsLike = weatherResponse.main?.feels_like
        val description = weatherResponse.weather?.get(0)?.main
        val humidity = weatherResponse.main?.humidity.toString()
        val windSpeed = weatherResponse.wind?.speed.toString()

        //Set Values based on unit used
        val fahrenheitTemp = formatTemperatureImperial(temp as Double)
        val feelsLikeTemp = formatTemperatureImperial(feelsLike as Double)
        val windSpeedUnit = formatWindSpeedImperial(windSpeed.toDouble())

        //Set sunrise and sunset
        val sunrise = weatherResponse.sys?.sunrise
        val sunriseTime = sunrise?.let { it1 -> getFormattedTime(it1.toLong()) }
        val sunset = weatherResponse.sys?.sunset
        val sunsetTime = sunset?.let { it1 -> getFormattedTime(it1.toLong()) }

        //Set weather detail
        val weatherDetail = WeatherDetail(
            time!!,
            name!!,
            iconUrl,
            description!!,
            fahrenheitTemp,
            feelsLikeTemp,
            humidity,
            windSpeedUnit,
            sunriseTime!!,
            sunsetTime!!
        )

        //Hide Loading
        binding.loader.visibility = View.GONE

        //Set weather detail
        binding.weather = weatherDetail

        //Set Share Text
        shareText =
            getString(R.string.share_text, name, fahrenheitTemp, feelsLikeTemp, description)
    }

    private fun shareWeather() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)

        // Start the activity with the share intent
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}