package com.manisoft.scraprushapp.utils.attachmentutils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.manisoft.scraprushapp.utils.FileSelectedListener
import com.manisoft.scraprushapp.utils.RealPathUtil
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.filePathToUri
import com.manisoft.scraprushapp.utils.getMimeType
import com.manisoft.scraprushapp.utils.getName
import com.manisoft.scraprushapp.utils.showToast
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileChooser(private val context: Context, activity: AppCompatActivity? = null, fragment: Fragment? = null) {

    private var fileChooserLauncher: ActivityResultLauncher<Intent>
    private var requestStoragePermission: ActivityResultLauncher<String>?
    private var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>?
    private var cameraLauncher: ActivityResultLauncher<Intent>?

    private lateinit var callBack: FileSelectedListener

    private var filePhoto: File? = null

    init {
        if (activity == null && fragment == null) {
            throw IllegalArgumentException("Either activity or fragment must be provided.")
        }

        fileChooserLauncher = activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleActivityResult(it)
        } ?: fragment!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleActivityResult(it)
        }

        requestStoragePermission = activity?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            handlePermissionResult(isGranted ?: false)
        } ?: fragment!!.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            handlePermissionResult(isGranted ?: false)
        }

        multiplePermissionLauncher = activity?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handleMultiplePermission(permissions)
        } ?: fragment!!.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handleMultiplePermission(permissions)
        }

        cameraLauncher = activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleCameraResult(it)
        } ?: fragment!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleCameraResult(it)
        }
    }


    private fun handleMultiplePermission(permissions: Map<String, @JvmSuppressWildcards Boolean>) {
        if (permissions.all { it.value }) {
            // All permissions are granted, proceed with your logic
            openFiles()
        } else {
            // Permissions are not granted, handle accordingly (e.g., show an explanation)
            context.showToast("Storage permission denied")
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            // Permission granted, show attachment dialog
            openFiles()
        } else {
            // Permission denied, show a toast
            context.showToast("Storage permission denied")
        }
    }

    private fun handleActivityResult(it: ActivityResult?) {
        it?.let { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                val selectedFileURI = res.data?.data
                val mimeType = selectedFileURI?.getMimeType(context) ?: ""
                val fileName = selectedFileURI?.getName(context) ?: ""
                if (Utils.getFileSize(selectedFileURI!!, context) <= 5.0) {

                    when {
                        mimeType.startsWith("image/") -> {
                            val imageFilePath = RealPathUtil.getRealPath(context, selectedFileURI) ?: ""

                            if (fileName.isNotEmpty() && imageFilePath.isNotEmpty()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Log.d("TAG", "Compress before: " + Utils.getFileSize(imageFilePath.filePathToUri(), context))
                                    val compressedImage = compressImage(imageFilePath)
                                    callBack.onFileSelected(true, compressedImage.absolutePath, fileName, false)
                                    Log.d("TAG", "Compress after: " + Utils.getFileSize(compressedImage.absolutePath.filePathToUri(), context))
                                }
                            }

                        }

                        else -> {
                            val documentFilePath = RealPathUtil.getFile(context, selectedFileURI).path

                            if (fileName.isNotEmpty() && documentFilePath.isNotEmpty()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Log.d("TAG", "Compress before: " + Utils.getFileSize(documentFilePath.filePathToUri(), context))
                                    val compressedImage = compressImage(documentFilePath)
                                    callBack.onFileSelected(false, compressedImage.absolutePath, fileName, false)
                                    Log.d("TAG", "Compress after: " + Utils.getFileSize(compressedImage.absolutePath.filePathToUri(), context))
                                }
                            }
                        }
                    }

                } else {
                    context.showToast("Please select file less than 5MB")
                }
            } else if (res.resultCode == Activity.RESULT_CANCELED) {
                // Handle the case when the activity is canceled
                context.showToast("File selection canceled")
            }

        }
    }

    private fun handleCameraResult(it: ActivityResult?) {
        it?.let { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                if (filePhoto != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val path = filePhoto?.absolutePath ?: ""
                        Log.d("TAG", "Compress before: " + Utils.getFileSize(path.filePathToUri(), context))
                        val compressedImage = compressImage(filePhoto?.absolutePath ?: "")
                        callBack.onFileSelected(true, compressedImage.absolutePath, filePhoto?.name ?: "", false)
                        Log.d("TAG", "Compress after: " + Utils.getFileSize(compressedImage.absolutePath.filePathToUri(), context))
                    }
//                    compressImage(filePhoto)
                }
            }

        }
    }


    fun showFileChooser(fileChooserCallback: FileSelectedListener, isFileOnly: Boolean = false) {
        callBack = fileChooserCallback
        if (isFileOnly) {
            checkStoragePermission()
        } else {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Take a picture", "Select from file")
            pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                when (which) {
                    0 -> takePhotoFromCamera()
                    1 -> checkStoragePermission()

                }
                dialog.dismiss()
            }
            pictureDialog.show()
        }
    }

    private fun takePhotoFromCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = null
        filePhoto = getPhotoFile()
        val providerFile = FileProvider.getUriForFile(context, com.manisoft.scraprushapp.BuildConfig.APPLICATION_ID, filePhoto!!)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        takePhotoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        if (takePhotoIntent.resolveActivity(context.packageManager) != null) {
            cameraLauncher?.launch(takePhotoIntent)
        } else {
            context.showToast("Camera could not open")
        }
    }

    private fun openFiles() {
        val mimeTypes = arrayOf("image/*")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        var mimeTypesStr = ""
        for (mimeType in mimeTypes) {
            mimeTypesStr += "$mimeType|"
        }
        intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        fileChooserLauncher.launch(Intent.createChooser(intent, "Choose File"))
    }

    private fun checkStoragePermission() {
        // Check if the device is running on Android version is equal or higher than TIRAMISU or has sufficient permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Utils.checkStoragePermission13(context)) {
                openFiles()
            } else {
                multiplePermissionLauncher?.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Request storage permission for Android version M and above
            if (Utils.checkStoragePermission(context)) {
                openFiles()
            } else {
                requestStoragePermission?.launch(Utils.APP_PERMISSION)
            }
        }
    }


    private fun compressImage(filePhoto: File?) {
        val compressedFile = File(context.cacheDir, "${filePhoto?.name}.jpg")

        val bitmap = BitmapFactory.decodeFile(filePhoto?.absolutePath)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        val outputStreamToFile = FileOutputStream(compressedFile)
        outputStreamToFile.write(outputStream.toByteArray())
        outputStreamToFile.close()

        //delete original image file
        filePhoto?.delete()
        callBack.onFileSelected(true, compressedFile.absolutePath ?: "", compressedFile.name ?: "", true)
    }

    private fun getPhotoFile(): File {
        val directoryStorage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("HAP_SFA_", "", directoryStorage)
    }

    private suspend fun compressImage(filePath: String): File {
        return withContext(Dispatchers.Main) {
            Compressor.compress(context, File(filePath)) {
                resolution(1280, 720)
                quality(40)
                format(Bitmap.CompressFormat.JPEG)
            }
        }
    }
}

