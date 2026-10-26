package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.myapplication.databinding.FragmentImageScaleBinding

class ImageScaleFragment : Fragment() {

    private var _binding: FragmentImageScaleBinding? = null
    private val binding get() = _binding!!

    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageScaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show progress bar and load high-resolution image using Coil
        binding.progressBar.visibility = View.VISIBLE
        binding.imageView.load("https://picsum.photos/1000/1500") {
            crossfade(true)
            listener(
                onStart = {
                    binding.progressBar.visibility = View.VISIBLE
                },
                onSuccess = { _, _ ->
                    binding.progressBar.visibility = View.GONE
                },
                onError = { _, _ ->
                    binding.progressBar.visibility = View.GONE
                }
            )
        }

        // Setup pinch-to-zoom using ScaleGestureDetector
        scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    // Clamp scale factor between 1.0f and 5.0f
                    scaleFactor = scaleFactor.coerceIn(1.0f, 5.0f)

                    binding.imageView.pivotX = detector.focusX
                    binding.imageView.pivotY = detector.focusY
                    binding.imageView.scaleX = scaleFactor
                    binding.imageView.scaleY = scaleFactor

                    return true
                }
            }
        )

        binding.imageView.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        // Reset zoom button
        binding.btnResetZoom.setOnClickListener {
            scaleFactor = 1.0f
            binding.imageView.scaleX = 1.0f
            binding.imageView.scaleY = 1.0f
            binding.imageView.pivotX = binding.imageView.width / 2f
            binding.imageView.pivotY = binding.imageView.height / 2f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}