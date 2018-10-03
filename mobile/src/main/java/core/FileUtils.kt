package core

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

object FileUtils {

  private val extensions = arrayOf("avi", "3gp", "mp4", "mp3", "jpeg", "jpg", "gif", "png", "pdf", "docx", "doc", "xls", "xlsx", "csv", "ppt", "pptx",
      "txt", "zip", "rar")

  @Throws(ActivityNotFoundException::class)
  fun openFile(context: Activity, url: File) {
    val uri = FileProvider.getUriForFile(context, context.packageName, url)
    val intent = ShareCompat.IntentBuilder.from(context).setStream(uri) // uri from FileProvider
        .setType("application/pdf").intent.setAction(Intent.ACTION_VIEW) //Change if needed
        .setDataAndType(uri, "application/pdf").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
  }

  /**
   * Return Extension of given path without dot(.)
   */
  fun getExtension(path: String): String {
    return if (path.contains(".")) path.substring(path.lastIndexOf(".") + 1).toLowerCase() else ""
  }

  /**
   * Is Valid Extension
   */
  fun isValidExtension(ext: String): Boolean {
    return Arrays.asList(*extensions).contains(ext)
  }

  fun createNewPDFDocument(dest: String): Any {
    /**
     * Creating Document
     */
    var document = Document()

    // Location to save
    PdfWriter.getInstance(document, FileOutputStream(dest))
    return document
  }


  fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Image? {
    val stream = ByteArrayOutputStream()

    // GET CURRENT SIZE
    val width = bm.width
    val height = bm.height
    // GET SCALE SIZE
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)
    // "RECREATE" THE NEW BITMAP

    var newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    newBm.compress(Bitmap.CompressFormat.PNG, 100, stream)

    return Image.getInstance(stream.toByteArray())
  }
}