package com.example.myapplication

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentAudioBinding

class AudioFragment : Fragment() {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private val updateHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val AUDIO_URL =
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            _binding?.let { b ->
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        b.seekBarProgress.progress = mp.currentPosition
                        updateHandler.postDelayed(this, 500)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
            if (mediaPlayer == null) {
                initAndPlay()
            } else if (isPrepared) {
                mediaPlayer?.start()
                binding.tvAudioStatus.text = getString(R.string.audio_status_playing)
                updateButtons(isPlaying = true)
                startProgressUpdate()
            }
        }

        binding.btnPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.tvAudioStatus.text = getString(R.string.audio_status_paused)
                updateButtons(isPlaying = false)
            }
        }

        binding.btnStop.setOnClickListener {
            stopAndReset()
        }

        updateButtons(isPlaying = false)
    }

    private fun initAndPlay() {
        binding.progressBarLoading.visibility = View.VISIBLE
        binding.tvAudioStatus.text = getString(R.string.audio_status_buffering)
        binding.btnPlay.isEnabled = false
        binding.btnPause.isEnabled = false
        binding.btnStop.isEnabled = false

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                // To load a local audio file instead, place your audio in `res/raw/sample_audio.mp3`
                // and use: val afd = resources.openRawResourceFd(R.raw.sample_audio); setDataSource(afd.fileDescriptor, afd.startOffset, afd.length); afd.close()
                setDataSource(AUDIO_URL)

                setOnPreparedListener { mp ->
                    isPrepared = true
                    binding.progressBarLoading.visibility = View.GONE
                    binding.tvAudioStatus.text = getString(R.string.audio_status_playing)
                    binding.seekBarProgress.max = mp.duration
                    mp.start()
                    updateButtons(isPlaying = true)
                    startProgressUpdate()
                }

                setOnCompletionListener {
                    binding.tvAudioStatus.text = getString(R.string.audio_status_completed)
                    binding.seekBarProgress.progress = 0
                    updateButtons(isPlaying = false)
                }

                setOnErrorListener { _, _, _ ->
                    binding.progressBarLoading.visibility = View.GONE
                    binding.tvAudioStatus.text = getString(R.string.audio_status_error)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_audio_playback),
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                prepareAsync()
            } catch (e: Exception) {
                binding.progressBarLoading.visibility = View.GONE
                binding.tvAudioStatus.text = getString(R.string.audio_status_error)
            }
        }
    }

    private fun startProgressUpdate() {
        updateHandler.removeCallbacks(updateProgressRunnable)
        updateHandler.post(updateProgressRunnable)
    }

    private fun updateButtons(isPlaying: Boolean) {
        binding.btnPlay.isEnabled = !isPlaying
        binding.btnPause.isEnabled = isPlaying
        binding.btnStop.isEnabled = isPrepared || isPlaying
    }

    private fun stopAndReset() {
        updateHandler.removeCallbacks(updateProgressRunnable)
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPrepared = false
        binding.seekBarProgress.progress = 0
        binding.tvAudioStatus.text = getString(R.string.audio_status_stopped)
        binding.progressBarLoading.visibility = View.GONE
        binding.btnPlay.isEnabled = true
        binding.btnPause.isEnabled = false
        binding.btnStop.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            binding.tvAudioStatus.text = getString(R.string.audio_status_paused)
            updateButtons(isPlaying = false)
        }
    }

    override fun onDestroyView() {
        updateHandler.removeCallbacksAndMessages(null)
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPrepared = false
        super.onDestroyView()
        _binding = null
    }
}