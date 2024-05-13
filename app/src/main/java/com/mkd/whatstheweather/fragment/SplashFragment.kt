package com.mkd.whatstheweather.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mkd.whatstheweather.BuildConfig
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    //Binding
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    //Animation
    private var isShowingAnimation = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Show Animation
        isShowingAnimation = true
        zoomViewWithListener(binding.splashLogo, 0.5f, 2f, listener)


        //Delay
        binding.root.postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }, 3000)
    }

    //Splash Animation
    private fun zoomViewWithListener(
        view: View,
        startScale: Float,
        endScale: Float,
        listener: Animator.AnimatorListener
    ) {
        val animatorSet = AnimatorSet()
        val scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, endScale)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, endScale)
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 1000
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.addListener(listener)
        animatorSet.start()
    }

    private val listener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            if (isShowingAnimation) {
                isShowingAnimation = false
                zoomViewWithListener(binding.splashLogo, 2f, 1f, this)
            }
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }
}