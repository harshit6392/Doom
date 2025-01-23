package com.doom.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.Serializable

fun Activity.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

inline fun <reified T : Activity> Activity.openActivity(vararg params: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(*params)
    this.startActivity(intent)
}

fun Intent.putExtras(vararg params: Pair<String, Any>): Intent {
    if (params.isEmpty()) return this
    params.forEach { (key, value) ->
        when (value) {
            is Int -> putExtra(key, value)
            is Byte -> putExtra(key, value)
            is Char -> putExtra(key, value)
            is Long -> putExtra(key, value)
            is Float -> putExtra(key, value)
            is Short -> putExtra(key, value)
            is Double -> putExtra(key, value)
            is Boolean -> putExtra(key, value)
            is Bundle -> putExtra(key, value)
            is String -> putExtra(key, value)
            is IntArray -> putExtra(key, value)
            is ByteArray -> putExtra(key, value)
            is CharArray -> putExtra(key, value)
            is LongArray -> putExtra(key, value)
            is FloatArray -> putExtra(key, value)
            is Parcelable -> putExtra(key, value)
            is ShortArray -> putExtra(key, value)
            is DoubleArray -> putExtra(key, value)
            is BooleanArray -> putExtra(key, value)
            is CharSequence -> putExtra(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<String>() ->
                        putExtra(key, value as Array<String?>)
                    value.isArrayOf<Parcelable>() ->
                        putExtra(key, value as Array<Parcelable?>)
                    value.isArrayOf<CharSequence>() ->
                        putExtra(key, value as Array<CharSequence?>)
                    else -> putExtra(key, value)
                }
            }
            is Serializable -> putExtra(key, value)
        }
    }
    return this
}

fun Activity.setStatusBarColor(color: String, isDark: Boolean) {
    this.window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (isDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.parseColor(color)
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.parseColor(color)
    }
}

fun Activity.transparentStatusBar(isDark: Boolean) {
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    this.window.statusBarColor = Color.TRANSPARENT
    if (isDark) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
    val win = activity.window
    val winParams = win.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    win.attributes = winParams
}

fun Activity.openPermissionSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

@Throws(IOException::class)
fun getFile(context: Context, uri: Uri): File? {
    val destinationFilename =
        File(context.filesDir.path + File.separatorChar.toString() + queryName(context, uri))
    try {
        context.contentResolver.openInputStream(uri).use { ins ->
            createFileFromStream(
                ins!!,
                destinationFilename
            )
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return destinationFilename
}

fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
    var result: String? = null
    val cursor = context.contentResolver.query(contentURI, null, null, null, null)
    if (cursor == null) {
        result = contentURI.path
    } else {
        cursor.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = if (idx != -1) it.getString(idx) else null
            }
        }
    }
    return result
}

/**
 * Returns a SpannableString with specified parts of the text colored.
 *param needed
 * @param fullText The complete text string.
 * @param coloredPart The part of the text to be highlighted with a constant color.
 */
fun getColoredText(fullText: String, coloredPart: String): SpannableString {
    val spannable = SpannableString(fullText)
    spannable.setSpan(
        ForegroundColorSpan(Color.WHITE),
        0,
        fullText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val startIndex = fullText.indexOf(coloredPart)
    if (startIndex != -1) {
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#D9EF3D")),
            startIndex,
            startIndex + coloredPart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannable
}

fun downloadImageAsFile(imageUrl: String, onFileReady: (File?) -> Unit,context: Context) {
    Glide.with(context)
        .downloadOnly()
        .load(imageUrl)
        .into(object : CustomTarget<File>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                // Handle clearing
                onFileReady(null)
            }
            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onFileReady(null) // Handle download failure
            }
            override fun onResourceReady(
                resource: File,
                transition: com.bumptech.glide.request.transition.Transition<in File>?
            ) {
                onFileReady(resource)
            }
        })
}

private fun queryName(context: Context, uri: Uri): String {
    val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name: String = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}

fun createFileFromStream(ins: InputStream, destination: File?) {
    try {
        FileOutputStream(destination).use { os ->
            val buffer = ByteArray(4096)
            var length: Int
            while (ins.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
            os.flush()
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path.toString())
}

