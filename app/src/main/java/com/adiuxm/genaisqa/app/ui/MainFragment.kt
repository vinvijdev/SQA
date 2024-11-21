package com.adiuxm.genaisqa.app.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adiuxm.genaisqa.app.Constants
import com.adiuxm.genaisqa.app.adapter.ChatAdapter
import com.adiuxm.genaisqa.app.utils.FileUtils
import com.adiuxm.genaisqa.app.utils.Utils
import com.adiuxm.genaisqa.data.remote.DataConstants
import com.adiuxm.genaisqa.data.remote.RecyclerViewDataModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.tajchert.waitingdots.DotsTextView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var dotView: DotsTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var et: EditText
    private lateinit var ll: LinearLayout
    private lateinit var rv: RecyclerView
    private lateinit var send: AppCompatImageView
    private lateinit var attach: AppCompatImageView
    private lateinit var selectedImageFile: File
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(com.adiuxm.genaisqa.R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setAdapter(view)
        bindObservers()
    }

    private fun initViews(view: View) {
        et = view.findViewById(com.adiuxm.genaisqa.R.id.query)
        ll = view.findViewById(com.adiuxm.genaisqa.R.id.welcome_ll)
        rv = view.findViewById(com.adiuxm.genaisqa.R.id.rv)
        send = view.findViewById(com.adiuxm.genaisqa.R.id.send)

        et.addTextChangedListener(textWatcher)
        et.setOnEditorActionListener(imeActionWatcher)

        send.isEnabled = false
        send.setOnClickListener {
            sendMessage(et.text.toString(), it, Constants.CHAT_TYPE_TEXT)
        }

        dotView = view.findViewById(com.adiuxm.genaisqa.R.id.dots)
        dotView.bringToFront()
        dotView.visibility = View.GONE

        attach = view.findViewById(com.adiuxm.genaisqa.R.id.attach_btn)
        attach.setOnClickListener {
            attachFile()
        }
    }

    private fun attachFile() {
        showBottomSheetDialog()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(com.adiuxm.genaisqa.R.layout.attachment_bottom_sheet_layout)
        val camera =
            bottomSheetDialog.findViewById<LinearLayout>(com.adiuxm.genaisqa.R.id.bs_camera_button)
        camera?.setOnClickListener {
//            launchCamera()
            dispatchTakePictureIntent()
            bottomSheetDialog.dismiss()
        }
        val gallery =
            bottomSheetDialog.findViewById<LinearLayout>(com.adiuxm.genaisqa.R.id.bs_gallery_button)
        gallery?.setOnClickListener {
            launchGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            selectedImageFile = createNewImageFile(requireContext())
            val uri = FileProvider.getUriForFile(
                requireContext(), "com.adiuxm.genaisqa.file.provider.one", selectedImageFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(takePictureIntent, 100)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    @Throws(IOException::class)
    fun createNewImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            100 -> {
                sendMessage(
                    selectedImageFile.absolutePath, et, Constants.CHAT_TYPE_IMAGE
                )
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap?, mimeType: String, absolutePath: String?): File? {
        if (absolutePath == null || bitmap == null) {
            return null
        }
        val file = File(absolutePath)
        val stream = FileOutputStream(file)
        if (mimeType.contains("jpg", true) || mimeType.contains("jpeg", true)) bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream
        )
        else if (mimeType.contains("png", true)) bitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            stream
        )
        stream.close()
        return file
    }

    private fun launchGallery() {
        galleryLauncher.launch("image/*")
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent(), fun(it: Uri?) {
            try {
                selectedImageFile = FileUtils.getFileFromUri(it, requireContext())!!
                sendMessage(
                    it.toString(), et, Constants.CHAT_TYPE_IMAGE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

    private fun clearEdittext(it: EditText?) {
        it!!.text.clear()
    }

//    @SuppressLint("MissingPermission")
//    private fun getLocation() {
//        fusedLocationClient.lastLocation.addOnSuccessListener {
//            // Got last known location. In some rare situations this can be null.
//            if (it != null) {
//                latitude = it.latitude
//                longitude = it.longitude
//                // Use the location coordinates
//            }
//        }.addOnFailureListener {
//            // Handle failure
//            Log.e("LocationError", "Failed to get location: " + it.message)
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun sendImage() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getImageUploadResult(
                selectedImageFile, latitude.toString(), longitude.toString()
            )
        }
//        if (!LocationUtils.isLocationPermissionGranted(requireContext())) {
//            getLocation()
//            CoroutineScope(Dispatchers.IO).launch {
//                viewModel.getImageUploadResult(
//                    selectedImageFile, latitude.toString(), longitude.toString()
//                )
//            }
//        } else {
//            // Before you perform the actual permission request, check whether your app
//            // already has the permissions, and whether your app needs to show a permission
//            // rationale dialog. For more details, see Request permissions.
//            locationPermissionRequest.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        }
    }

    private fun sendText(query: String) {
        viewModel.getQueryResult(query)
    }

    private fun sendMessage(query: String, view: View, chatType: String) {
        if (Utils.checkForInternet(requireContext())) {
            ll.visibility = View.GONE
            rv.visibility = View.VISIBLE

            if (chatType == Constants.CHAT_TYPE_IMAGE) {
                notifyRecyclerView(
                    RecyclerViewDataModel(
                        query, DataConstants.USER_TYPE_SENDER, chatType, selectedImageFile
                    )
                )
                sendImage()
            } else {
                notifyRecyclerView(
                    RecyclerViewDataModel(
                        query, DataConstants.USER_TYPE_SENDER, chatType
                    )
                )
                sendText(query)
            }

            dotView?.visibility = View.VISIBLE
            hideKeyboard(view)
            clearEdittext(et)
        } else {
            Toast.makeText(
                requireContext(),
                getString(com.adiuxm.genaisqa.R.string.no_internet_toast_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun notifyRecyclerView(recyclerViewDataModel: RecyclerViewDataModel) {
        adapter.addItems(recyclerViewDataModel)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(adapter.itemCount - 1)

    }

    private fun setAdapter(view: View) {
        recyclerView = view.findViewById<RecyclerView>(com.adiuxm.genaisqa.R.id.rv)
        var layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(requireContext())
        recyclerView.adapter = adapter
    }

    private fun bindObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.queryResponse.collectLatest {
                        if (it.isNotEmpty()) {
                            // setting data in recycler view
                            dotView.visibility = View.GONE
                            notifyRecyclerView(
                                RecyclerViewDataModel(
                                    it, DataConstants.USER_TYPE_JOULE, "chatType"
                                )
                            )
                        }
                    }
                }

                launch {
                    viewModel.errorMessage.collectLatest {
                        if (it.isNotEmpty()) {
                            dotView.visibility = View.GONE
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            //manage error handling
                        }
                    }
                }

//                launch {
//                    viewModel.isLoading.collectLatest {
//                        if(isLoading) {
//                            // show progress dialog while loading
//                        } else {
//                            // hide progress dialog when data is loaded
//                        }
//                    }
//                }

            }
        }
    }

    // Hide keyboard
    private fun hideKeyboard(view: View) =
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())

    // implement the TextWatcher callback listener
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            send.isEnabled = s.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val imeActionWatcher: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                sendMessage(et.text.toString(), v!!, Constants.CHAT_TYPE_TEXT)
            }
            false;
        }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(com.adiuxm.genaisqa.R.id.speak)
        item.setOnMenuItemClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Enter your query!")
            activityResultLauncher.launch(intent)
            true
        }
    }

    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // if result is not empty
        if (result.resultCode === AppCompatActivity.RESULT_OK && result.data != null) {
            // get data and append it to editText
            val d: ArrayList<String>? =
                result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            et.setText(d?.get(0))
        }
    }

//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        when {
//            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                // Precise location access granted.
//                sendImage()
//            }
//
//            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                // Only approximate location access granted.
//                sendImage()
//            }
//
//            else -> {
//                // No location access granted.
//                Toast.makeText(
//                    requireContext(),
//                    "Location access is required to upload image",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
//        getLocation() // Start location updates
    }
}