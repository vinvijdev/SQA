package com.adiuxm.genaisqa.app.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adiuxm.genaisqa.data.remote.ApiModule
import com.adiuxm.genaisqa.data.remote.Resource
import com.adiuxm.genaisqa.domain.repository.ImageUploadResponseRepository
import com.adiuxm.genaisqa.domain.repository.QueryResponseRepository
import com.adiuxm.genaisqa.domain.repository.TokenResponseRepository
//import com.adiuxm.genaimdk.domain.repository.TokenResponseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _queryResponse = MutableStateFlow("")
    val queryResponse = _queryResponse.asStateFlow()

    private val _tokenResponse = MutableStateFlow("")
    val tokenResponse = _tokenResponse.asStateFlow()

    private val _imageUploadResponse = MutableStateFlow("")
    val imageUploadResponse = _imageUploadResponse.asStateFlow()

    private val queryResponseRepository: QueryResponseRepository
        get() {
            return ApiModule.providesQueryResponseRepository(getApplication<Application>().applicationContext)
        }

    private val tokenResponseRepository: TokenResponseRepository
        get() {
            return ApiModule.providesTokenResponseRepository(getApplication<Application>().applicationContext)
        }

    private val imageUploadResponseRepository: ImageUploadResponseRepository
        get() {
            return ApiModule.providesImageUploadResponseRepository(getApplication<Application>().applicationContext)
        }


    fun getQueryResult(query: String) {

        viewModelScope.launch {
            queryResponseRepository.getResult(query).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            val data = it.data
                            _isLoading.emit(false)
                            _queryResponse.emit(data)
                        }
                    }

                    is Resource.Error -> {
                        if (resource.message.isNullOrEmpty()) {
                            _errorMessage.emit("Error")
                        } else {
                            resource.message.let {
                                _isLoading.emit(false)
                                _errorMessage.emit(resource.message)
                            }
                        }
                    }

                    is Resource.Loading -> {
                        _isLoading.emit(true)
                    }
                }
            }
        }
    }

    fun getImageUploadResult(file: File, latitude: String, longitude: String) {

        viewModelScope.launch {
            imageUploadResponseRepository.getResult(file).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            val data = it.response
                            _isLoading.emit(false)
                            _queryResponse.emit(data)
                        }
                    }

                    is Resource.Error -> {
                        if (resource.message.isNullOrEmpty()) {
                            _errorMessage.emit("Error")
                        } else {
                            resource.message.let {
                                _isLoading.emit(false)
                                _errorMessage.emit(resource.message)
                            }
                        }
                    }

                    is Resource.Loading -> {
                        _isLoading.emit(true)
                    }
                }
            }
        }
    }

    fun getTokenResult(query: String) {
        viewModelScope.launch {
            tokenResponseRepository.getResult().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            val data = it.accessToken
                            Log.d("TOKEN", data.toString())
                            _isLoading.emit(false)
                            _tokenResponse.emit(data!!)
                        }
                    }

                    is Resource.Error -> {
                        resource.message?.let {
                            _errorMessage.emit(it)
                        }
                    }

                    is Resource.Loading -> {
                        _isLoading.emit(true)
                    }
                }
            }
        }
    }
}