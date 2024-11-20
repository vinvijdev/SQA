package com.adiuxm.genaisqa.app

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.adiuxm.genaisqa.R
import com.adiuxm.genaisqa.app.ui.SQAMainFragment


class MainActivity : AppCompatActivity() {

    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mIsListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fl, SQAMainFragment.newInstance())
                .commitNow()
        }

        setToolbar()

//        val i = intent
//        if (i != null) {
//            saveData(i)
//        } else {
//            goBack()
//        }

//        verifyAudioPermissions()

        createSpeechRecognizer(this)

    }

    private fun saveData(i: Intent) {
        DataManager.tokenUrl = checkIntentData(Constants.TOKEN_URL, i)
        DataManager.clientId = checkIntentData(Constants.CLIENT_ID, i)
        DataManager.clientSecret = checkIntentData(Constants.CLIENT_SECRET, i)
        DataManager.baseUrl = checkIntentData(Constants.BASE_URL, i)
        DataManager.credentialType = checkIntentData(Constants.CREDENTIAL_TYPE, i)
        DataManager.authCode = checkIntentData(Constants.AUTH_CODE, i)
    }

    private fun checkIntentData(key: String, i: Intent): String {
        if (i.getStringExtra(key) == null) {
            goBack()
        } else {
            return i.getStringExtra(key)!!
        }
        return ""
    }

    private fun setToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.app_name_main)
    }

    override fun onSupportNavigateUp(): Boolean {
        goBack()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun goBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (checkIfEmpty(DataManager.clientId) || checkIfEmpty(DataManager.clientSecret) || checkIfEmpty(
                DataManager.tokenUrl
            ) || checkIfEmpty(DataManager.baseUrl) || checkIfEmpty(DataManager.credentialType)
        ) {
            goBack()
        }
    }

    private fun checkIfEmpty(key: String): Boolean {
        if (key.isEmpty()) {
            return true
        }
        return false
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        super.onOptionsItemSelected(item)
//        return when (item.itemId) {
////            R.id.speak -> {
////                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
////                intent.putExtra(
////                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
////                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
////                )
////                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
////                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Enter your query!")
////                activityResultLauncher.launch(intent)
////                true
////            }
//
//            R.id.about -> {
//                true
//            }
//
//            R.id.help -> {
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        // if result is not empty
//        if (result.resultCode === RESULT_OK && result.data != null) {
//            // get data and append it to editText
//            val d: ArrayList<String>? =
//                result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            Toast.makeText(this, d?.get(0), Toast.LENGTH_LONG).show()
//        }
//    }

//    private fun verifyAudioPermissions() {
//        if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.RECORD_AUDIO), 123
//            )
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 123) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // audio permission granted
//                Toast.makeText(this, "You can now use voice commands!", Toast.LENGTH_LONG).show()
//            } else {
//                // audio permission denied
//                Toast.makeText(
//                    this,
//                    "Please provide microphone permission to use voice.",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun createSpeechRecognizer(ctx : Context) {
//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//        mSpeechRecognizer?.setRecognitionListener(object : RecognitionListener {
//            override fun onReadyForSpeech(params: Bundle) {}
//            override fun onBeginningOfSpeech() {}
//            override fun onRmsChanged(rmsdB: Float) {}
//            override fun onBufferReceived(buffer: ByteArray) {}
//            override fun onEndOfSpeech() {
//                handleSpeechEnd()
//            }
//
//            override fun onError(error: Int) {
//                handleSpeechEnd()
//            }
//
//            override fun onResults(results: Bundle) {
//                // Called when recognition results are ready. This callback will be called when the
//                // audio session has been completed and user utterance has been parsed.
//                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                if (matches != null && matches.size > 0) {
//                    // The results are added in decreasing order of confidence to the list
//                    val command = matches[0]
//                    handleCommand(command)
//                }
//            }
//
//            override fun onPartialResults(partialResults: Bundle) {
//                // Called when partial recognition results are available, this callback will be
//                // called each time a partial text result is ready while the user is speaking.
//                val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                if (matches != null && matches.size > 0) {
//                    // handle partial speech results
//                    val partialText = matches[0]
//                }
//            }
//
//            override fun onEvent(eventType: Int, params: Bundle) {}
//        })
//    }
//
//    private fun handleCommand(command: String) {
//        Toast.makeText(this, command, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun createIntent(): Intent {
//        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
//        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
//        return i
//    }
//    private fun handleSpeechBegin() {
//        // start audio session
//        mIsListening = true
//        mSpeechRecognizer!!.startListening(createIntent())
//    }
//
//    private fun handleSpeechEnd() {
//        // end audio session
//        mIsListening = false
//        mSpeechRecognizer!!.cancel()
//    }

//    val speechText = remember { mutableStateOf("Your speech will appear here.") }
//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == Activity.RESULT_OK) {
//            val data = it.data
//            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            speechText.value = result?.get(0) ?: "No speech detected."
//        } else {
//            speechText.value = "[Speech recognition failed.]"
//        }
//    }
}