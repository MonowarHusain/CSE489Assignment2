package com.example.myapplication

import android.net.Uri
import android.os.Bundle
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

    companion object {
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

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(Uri.parse(VIDEO_URL))

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            binding.progressBar.visibility = View.GONE
            mediaPlayer.isLooping = false
            binding.videoView.start()
        }

        binding.videoView.setOnErrorListener { _, _, _ ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                getString(R.string.error_video_playback),
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
        }
    }

    override fun onDestroyView() {
        binding.videoView.stopPlayback()
        super.onDestroyView()
        _binding = null
    }
}