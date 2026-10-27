package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private var stopPosition = 0

    companion object {
        private const val TAG = "VideoFragment"
        private const val VIDEO_URL =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        setupVideoPlayer()
    }

    private fun setupVideoPlayer() {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.apply {
            setMediaController(mediaController)

            // Example for loading a local resource:
            // setVideoURI(Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.sample_video))

            setVideoURI(Uri.parse(VIDEO_URL))

            setOnPreparedListener { mediaPlayer ->
                binding.progressBar.visibility = View.GONE
                mediaPlayer.isLooping = false
                if (stopPosition > 0) {
                    seekTo(stopPosition)
                }
                start()
            }

            setOnErrorListener { _, what, extra ->
                binding.progressBar.visibility = View.GONE
                Log.e(TAG, "VideoView Error occurred. what: $what, extra: $extra")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_video_playback),
                    Toast.LENGTH_LONG
                ).show()
                true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isPlaying) {
            stopPosition = binding.videoView.currentPosition
            binding.videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (stopPosition > 0 && !binding.videoView.isPlaying) {
            binding.videoView.seekTo(stopPosition)
            binding.videoView.start()
        }
    }

    override fun onDestroyView() {
        binding.videoView.stopPlayback()
        super.onDestroyView()
        _binding = null
    }
}