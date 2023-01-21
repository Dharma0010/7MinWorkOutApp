package com.example.a7minworkout

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkout.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityExerciseBinding
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? =null
    private var currentExercisePositions = -1

    private lateinit var tts: TextToSpeech
    private lateinit var player: MediaPlayer

    private lateinit var exerciseAdapter: RestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO MENAMPILKAN CUSTOM ACTIONBAR
        setSupportActionBar(binding.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        binding.toolbarExercise.setNavigationOnClickListener {
            onBackPressed()
        }
        tts = TextToSpeech(this,this)

        setRestProgressBar()
        setupRestView()
        recycleViewRestView()


    }

    private fun setupRestView() {

        try {
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player.isLooping = false
            player.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.flRestView.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.VISIBLE
        binding.tvExerciseName.visibility = View.INVISIBLE
        binding.flExerciseView.visibility = View.INVISIBLE
        binding.ivImageExercise.visibility = View.INVISIBLE
        binding.tvUpcomingExerciseLevel.visibility = View.VISIBLE
        binding.tvUpcomingExerciseName.visibility = View.VISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding.tvUpcomingExerciseName.text = exerciseList!!.get(currentExercisePositions + 1).getName()

        setRestProgressBar()
    }

    private fun setupExerciseView() {
        binding.flRestView.visibility = View.INVISIBLE
        binding.tvTitle.visibility = View.INVISIBLE
        binding.tvExerciseName.visibility = View.VISIBLE
        binding.flExerciseView.visibility = View.VISIBLE
        binding.ivImageExercise.visibility = View.VISIBLE
        binding.tvUpcomingExerciseLevel.visibility = View.INVISIBLE
        binding.tvUpcomingExerciseName.visibility = View.INVISIBLE

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        setExerciseProgressBar()

        speakOut(exerciseList!![currentExercisePositions].getName())
        binding.ivImageExercise.setImageResource(exerciseList!![currentExercisePositions].getImage())
        binding.tvExerciseName.text = exerciseList!![currentExercisePositions].getName()
    }

    private fun setRestProgressBar() {
        binding.progressBarr.progress = restProgress

        restTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding.progressBarr.progress = 10 - restProgress
                binding.tvTimer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePositions++

                exerciseList!![currentExercisePositions].setSelected(true)
                exerciseAdapter.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        binding.progressBarrExercise.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding.progressBarrExercise.progress = 30 - exerciseProgress
                binding.tvTimerExercise.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePositions].setSelected(false)
                exerciseList!![currentExercisePositions].setCompleted(true)
                exerciseAdapter.notifyDataSetChanged()

                if (currentExercisePositions < exerciseList?.size!! - 1) {
                    setupRestView()
                } else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Conratultions! You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language Specified Is Not Supported")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }
    private fun recycleViewRestView() {

        binding.rcRestView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = RestAdapter(exerciseList!!)
        binding.rcRestView.adapter = exerciseAdapter
    }

    override fun onDestroy() {
        super.onDestroy()

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }

}