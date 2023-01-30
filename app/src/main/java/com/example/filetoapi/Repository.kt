package com.example.filetoapi

import com.example.filetoapi.Api.ApiHelper

import okhttp3.MultipartBody

class Repository() {


    suspend fun  uploadImage(image: MultipartBody.Part)= ApiHelper.apiService().uploadImage(image)





}