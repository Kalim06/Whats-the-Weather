package com.mkd.whatstheweather.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.api.ApiResultHandler
import com.mkd.whatstheweather.databinding.FragmentHomeBinding
import com.mkd.whatstheweather.model.WeatherResponse
import com.mkd.whatstheweather.utils.NetworkUtils
import com.mkd.whatstheweather.utils.PreferencesManager
import com.mkd.whatstheweather.utils.formatTemperatureImperial
import com.mkd.whatstheweather.utils.formatTemperatureMetric
import com.mkd.whatstheweather.utils.showSnackbar
import com.mkd.whatstheweather.utils.showSnackbarWithDismiss
import com.mkd.whatstheweather.viewModel.HomeViewModel


class HomeFragment : Fragment(), OnMapReadyCallback {

    //Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //Request Codes
    private val locationPermissionRequestCode = 1001
    private val requestCheckSettings = 1002

    //Map Variables
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var lastLocation: Location
    private lateinit var currentCity: String
    private var previousMarker: Marker? = null

    //ViewModel
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check permissions
        checkLocationPermissions()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = SupportMapFragment.newInstance()
        parentFragmentManager.beginTransaction().add(R.id.mapView, mapFragment).commit()
        mapFragment.getMapAsync(this)
        binding.mapLoader.visibility = View.VISIBLE

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize viewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        //Set click listeners
        binding.permissionBtn.setOnClickListener {

            if (isLocationPermissionGranted()) {
                checkLocationSettings()
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

        // Move camera to user location
        binding.mapFocus.setOnClickListener {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, 15f
                )
            )
            checkInternet(currentLocation.latitude.toString(), currentLocation.longitude.toString())
            previousMarker?.remove()
        }

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.userWeatherView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(currentCity)
            findNavController().navigate(action)
        }

        binding.noInternetView.checkInternetBtn.setOnClickListener {
            checkInternet(currentLocation.latitude.toString(), currentLocation.longitude.toString())
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Check Location permission
    private fun checkLocationPermissions() {
        launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    //Request Location permission
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            if (locationGranted) {
                //Check GPS settings
                checkLocationSettings()
            } else {
                // Permission denied, handle accordingly
                binding.locationPermissionCard.visibility = View.VISIBLE
            }
        }

    //Location permission result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if the request code matches the location permission request code
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, check GPS settings
                checkLocationSettings()
            } else {
                // Permission denied, handle accordingly
                binding.locationPermissionCard.visibility = View.VISIBLE
            }
        }
    }

    //Check GPS settings
    private fun checkLocationSettings() {

        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is enabled, hide the permission card
            binding.locationPermissionCard.visibility = View.GONE
            // Get user location
            getUserLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    @Suppress("DEPRECATION")
                    startIntentSenderForResult(
                        exception.resolution.intentSender, requestCheckSettings, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    //Show Toast
                    showSnackbar(binding.root, "Something went wrong please try again with GPS.")
                }
            }
        }
    }

    //GPS settings result
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCheckSettings) {
            if (resultCode == Activity.RESULT_OK) {
                // User enabled location, handle accordingly
                binding.locationPermissionCard.visibility = View.GONE
                // Get user location
                getUserLocation()
            } else {
                binding.locationPermissionCard.visibility = View.VISIBLE
            }
        }
    }

    //Map Ready
    override fun onMapReady(googleMap: GoogleMap) {
        // Initialize map
        map = googleMap
        map.uiSettings.setAllGesturesEnabled(false)
        // Make search view unclickable
        binding.searchView.isEnabled = false
        binding.searchView.isClickable = false
        binding.settings.isEnabled = false
        binding.settings.isClickable = false

        //Add long click listener
        map.setOnMapLongClickListener { latLng ->

            // Clear previous marker
            previousMarker?.remove()

            // Add new marker
            addMarker(latLng)
        }
    }

    //Add marker
    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        previousMarker = map.addMarker(markerOptions)

        val latitude = latLng.latitude.toString()
        val longitude = latLng.longitude.toString()

        //Get Added Marker Weather Conditions
        checkInternet(latitude, longitude)
    }

    //Get User Location
    private fun getUserLocation() {
        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 20000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.lastLocation != null) {
                    val location = locationResult.lastLocation
                    currentLocation = location!!
                    updateMap(location)
                    checkInternet(location.latitude.toString(), location.longitude.toString())
                    getLastLocation()
                } else {
                    showSnackbar(
                        binding.root,
                        getString(R.string.something_went_wrong_pls_try_again)
                    )
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lastLocation = location
                updateMap(location)
                compareLocation()
            } else {
                showSnackbar(
                    binding.root,
                    getString(R.string.something_went_wrong_pls_try_again)
                )
            }
        }
    }

    private fun updateMap(location: Location) {
        // Enable map interaction
        map.uiSettings.setAllGesturesEnabled(true)
        binding.mapLoader.visibility = View.GONE
        binding.mapFocus.visibility = View.VISIBLE

        // Move camera to user location
        val latLng = LatLng(location.latitude, location.longitude)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15f
            )
        )

        // Add a marker for user location
        val marker = MarkerOptions().position(latLng).title("My Location")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        map.addMarker(marker)
    }

    private fun compareLocation() {
        if (currentLocation.latitude == lastLocation.latitude && currentLocation.longitude == lastLocation.longitude) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkInternet(latitude: String, longitude: String) {
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            getWeatherByLocation(
                latitude,
                longitude
            )
            binding.noInternetView.root.visibility = View.GONE
        } else {
            binding.noInternetView.root.visibility = View.VISIBLE
        }
    }

    private fun getWeatherByLocation(latitude: String, longitude: String) {

        //Update Views
        binding.userWeatherView.visibility = View.VISIBLE
        binding.userWeatherView.isEnabled = false
        binding.weatherTitle.visibility = View.GONE
        binding.weatherDescription.visibility = View.GONE
        binding.weatherIcon.visibility = View.GONE

        viewModel.getWeatherByLocation(latitude, longitude)
            .observe(this.viewLifecycleOwner) { result ->

                val apiResultHandler = ApiResultHandler<WeatherResponse?>(onSuccess = {
                    if (it != null) {
                        setData(it)
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
                    apiResultHandler.loading.observe(this.viewLifecycleOwner) {
                        //show loading
                        binding.weatherLoader.visibility = if (it) View.VISIBLE else View.GONE
                    }
                }
            }
    }

    private fun setData(weatherResponse: WeatherResponse) {

        // Make search view clickable
        binding.searchView.isEnabled = true
        binding.searchView.isClickable = true
        binding.settings.isEnabled = true
        binding.settings.isClickable = true

        //Set data
        val name = weatherResponse.name
        val country = weatherResponse.sys?.country
        val iconCode = weatherResponse.weather?.get(0)?.icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"
        val temp = weatherResponse.main?.temp
        val feelsLike = weatherResponse.main?.feels_like
        val description = weatherResponse.weather?.get(0)?.main
        currentCity = "$name, $country"

        //Get preferences
        val preferencesManager = PreferencesManager(requireContext())
        val selectedUnit = preferencesManager.getUnit()

        //Set Unit Based on User Selection
        val tempValue = temp as Double
        val feelsLikeValue = feelsLike as Double
        val (convertedTemp, convertedFeelsLike) = when (selectedUnit) {
            "metric" -> formatTemperature(tempValue, true) to formatTemperature(
                feelsLikeValue,
                true
            )

            "imperial" -> formatTemperature(tempValue, false) to formatTemperature(
                feelsLikeValue,
                false
            )

            else -> formatTemperature(tempValue, true) to formatTemperature(feelsLikeValue, true)
        }

        //Set UI
        binding.weatherTitle.text = getString(R.string.home_title, name, convertedTemp)
        binding.weatherDescription.text =
            getString(R.string.home_description, convertedFeelsLike, description)
        Glide.with(this).load(iconUrl).into(binding.weatherIcon)

        //Enable user interaction with the view
        binding.userWeatherView.isEnabled = true
        binding.weatherTitle.visibility = View.VISIBLE
        binding.weatherDescription.visibility = View.VISIBLE
        binding.weatherIcon.visibility = View.VISIBLE

        //Give User Tip
        if (!preferencesManager.getHintShown()) {
            showSnackbarWithDismiss(binding.mapFocus, getString(R.string.map_hint))
        }
    }

    private fun formatTemperature(value: Double, metric: Boolean): String {
        return if (metric) {
            formatTemperatureMetric(value)
        } else {
            formatTemperatureImperial(value)
        }
    }
}