package com.example.filetoapi

import android.content.ContentResolver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.filetoapi.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var repo: Repository=Repository()

    lateinit var binding: ActivityMainBinding
    private val previewImage by lazy { binding.ivImage }


    private var latestTemproaryUri: Uri? = null


    private val myContentFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture())
        { isSucces ->
            if (isSucces) {
                latestTemproaryUri?.let {
//                    previewImage.setImageURI(it)
//
//                }

                    val file = uriToFile(it, contentResolver, applicationContext.externalCacheDir)
                    val requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    println(body.toString() + "!!!!!!!!!!!!")




                    GlobalScope.launch(Dispatchers.Main) {


                        var s = repo.uploadImage(body)
                        println("!!!!!!!!!!!!!!${s.toString()}")

//
                    }
                }
            }
        }
    private val myContentFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                previewImage.setImageURI(it)
            }

        }


    fun uriToFile(uri: Uri, contentResolver: ContentResolver, cacheDir: File?): File {
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(uri, "r") // или openAssetFileDescriptor
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val inputStream = FileInputStream(fileDescriptor)

        val timestamp: String = SimpleDateFormat(
            "yyyyMMdd-HHmmss-",
            Locale.US
        ).format(
            Date()
        )

        val file = File(cacheDir, "$timestamp.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        parcelFileDescriptor?.close()
        inputStream.close()
        outputStream.close()
        return file
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonCapturePhoto.setOnClickListener {
            takeImage()
        }
        binding.buttonOpenGallery.setOnClickListener {
            openImage()
        }

    }

    private fun openImage() {
        myContentFromGallery.launch("image/*")
    }


    fun takeImage() {
        lifecycleScope.launch {
            getTemproaryFile().let {
                latestTemproaryUri = it
                myContentFromCamera.launch(it)

            }
        }

    }


    fun getTemproaryFile(): Uri {
        val timestamp: String = SimpleDateFormat(
            "yyyyMMdd-HHmmss-",
            Locale.US
        ).format(
            Date()
        )
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val temproaryFile = File.createTempFile(
            "PHOTO_${timestamp}",
            "jpg",

            storageDirectory
        ).apply {
            createNewFile()
            deleteOnExit()


        }
        return FileProvider.getUriForFile(
            this,
            "com.example.filetoapi.fileprovider",
            temproaryFile
        )
    }


//    private fun createFileFromBitmap(bitmap: Bitmap): File {
//        val file = File(this.cacheDir, "image.jpeg")
//        file.createNewFile()
//
//        val bitmapCompress = Bitmap.createBitmap(bitmap)
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmapCompress.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val bytes = byteArrayOutputStream.toByteArray()
//        val outputStream = FileOutputStream(file)
//        outputStream.write(bytes)
//        outputStream.close()
//
//        return file
//    }

}