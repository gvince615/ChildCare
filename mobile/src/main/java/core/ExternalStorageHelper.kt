package core

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

object ExternalStorageHelper {

  val isExternalStorageMounted: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

  fun openMusicDirectory(context: Context): File? {
    return if (isExternalStorageMounted) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
      else
        openDirectory("music")
    } else null
  }

  private fun openDirectory(dirname: String): File? {
    var f: File? = null
    if (isExternalStorageMounted) {
      val storageDir = Environment.getExternalStorageDirectory()
      f = File(storageDir, dirname)
      if (f != null && !f.exists()) {
        f.mkdirs()
      }
    }
    return f
  }

  fun openPublicPicturesDirectory(): File? {
    return if (isExternalStorageMounted) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
      else
        openPublicDirectory("Pictures")
    } else null
  }

  private fun openPublicDirectory(dirname: String): File? {
    var f: File? = null
    if (isExternalStorageMounted) {
      val storageDir = Environment.getExternalStorageDirectory()
      f = File(storageDir, dirname)
      if (f != null && !f.exists()) {
        f.mkdirs()
      }
    }
    return f
  }

  fun getCacheDir(context: Context): File? {
    return if (isExternalStorageMounted) getExternalCacheDir(context) else getCacheDirByContext(context)
  }

  private fun getExternalCacheDir(context: Context): File? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
      context.externalCacheDir
    else
      context.cacheDir
  }

  private fun getCacheDirByContext(context: Context): File {
    return context.cacheDir
  }

}
