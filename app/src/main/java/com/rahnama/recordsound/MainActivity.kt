package com.rahnama.recordsound

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rahnama.recordsound.databinding.ActivityMainBinding
import java.io.IOException
import android.media.MediaPlayer
import android.view.View
import androidx.core.net.toUri
import java.util.concurrent.TimeUnit
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.media.AudioManager
import android.media.ToneGenerator.MAX_VOLUME
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi


private  lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    private var medPlayer: MediaPlayer? = null
    private var timeInt = 0
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /*******************************************************/
        mediaRecorder = MediaRecorder()
        output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
        /******************************************************/
        val mHandler = Handler(Looper.getMainLooper())
        runOnUiThread(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (medPlayer != null) {
                    val mCurrentPosition: Int = medPlayer!!.currentPosition / 1000
                    binding.seekBar.progress = mCurrentPosition
                    binding.min.text= "$mCurrentPosition s"

                }
                mHandler.postDelayed(this, 1000)
            }
        })
        /***********************************************************/
        /*************************************************************/
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
             
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    timeInt = medPlayer!!.currentPosition
                    audioTime()
                    medPlayer!!.seekTo(progress * 1000)

                }
            }
        })
        /****************************************/
        binding.buttonStartRecording.setOnClickListener {
            //گرفتن مجوز ضبط صدا
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                   Manifest.permission.RECORD_AUDIO,
                   Manifest.permission.WRITE_EXTERNAL_STORAGE,
                   Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                startRecording()
            }
        }
        /*******************************************************/
        binding.buttonStopRecording.setOnClickListener {
            stopRecording()
        }
        /*******************************************************/

        binding.play.setOnClickListener(View.OnClickListener {
            if (medPlayer == null) {

                medPlayer = MediaPlayer.create(this, output!!.toUri())
                binding.max.text=(medPlayer!!.duration/1000).toString()+" s"
            }
                medPlayer!!.isLooping = false
               binding.seekBar.max=medPlayer!!.duration/1000
                medPlayer!!.start()
                if (medPlayer!!.isPlaying) {
                    binding.showtxt.text = "موزیک در حال پخش است"
                } else {
                    binding.showtxt.text = "موزیک در حال پخش نیست"
                }

        })
        /***************************************************/

        binding.pauseBtnPlay.setOnClickListener(View.OnClickListener {
            medPlayer!!.pause()
            binding.showtxt.text="موزیک موقتا متوقف شد"
        })
        /***************************************************/
        binding.stopBtnPlay.setOnClickListener(View.OnClickListener {
            medPlayer!!.stop()
            medPlayer!!.release()
            medPlayer=null
            binding.showtxt.text="موزیک متوقف شد"
        })
        /***************************************************/
        binding.seektoBtnPlay.setOnClickListener(View.OnClickListener {
            if(medPlayer!!.duration/1000>=20) {
                medPlayer!!.seekTo(20000)
                Toast.makeText(this, "موزیک به ثانیه ۲۰ منتقل شد", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                Toast.makeText(this, "مدت زمان موزیک کم است", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        /***************************************************/
    }
/*******************************************************/
@SuppressLint("DefaultLocale", "SetTextI18n")
open fun audioTime() {
    binding.min.text =TimeUnit.MILLISECONDS.toSeconds(timeInt.toLong()).toString()+" s"
}
    /********************************************/
private fun startRecording() {
    try {
        mediaRecorder?.prepare()
        mediaRecorder?.start()
        state = true
        Toast.makeText(this, "شروع ضبط", Toast.LENGTH_SHORT).show()
        binding.buttonStartRecording.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24)
    } catch (e: IllegalStateException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
/*************************************************/

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this,"ادامه", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
        binding.buttonStartRecording.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24)

    recordingStopped = false
    }
/*********************************************************/
    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
            binding.buttonStartRecording.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }else{
        Toast.makeText(this, "فعلا در حال ضبط نیستید!", Toast.LENGTH_SHORT).show()
        }
    }
}