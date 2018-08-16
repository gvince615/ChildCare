package core

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.vince.childcare.R


class CustomSnackbar
/**
 * Constructor for the transient bottom bar.
 *
 * @param parent The parent for this transient bottom bar.
 * @param content The content view for this transient bottom bar.
 * @param callback The content view callback for this transient bottom bar.
 */
private constructor(parent: ViewGroup, content: View, callback: ContentViewCallback) : BaseTransientBottomBar<CustomSnackbar>(parent, content,
    callback) {

  fun setText(text: CharSequence): CustomSnackbar {
    val textView = view.findViewById<TextView>(R.id.snackbar_text)
    textView.text = text
    return this
  }

  fun setAction1(text: CharSequence, listener: View.OnClickListener): CustomSnackbar {
    val actionView = view.findViewById<Button>(R.id.snackbar_action_1)
    actionView.text = text
    actionView.visibility = View.VISIBLE
    actionView.setOnClickListener { view ->
      listener.onClick(view)
      dismiss()
    }
    return this
  }

  fun setAction2(text: CharSequence, listener: View.OnClickListener): CustomSnackbar {
    val actionView = view.findViewById<Button>(R.id.snackbar_action_2)
    actionView.text = text
    actionView.visibility = View.VISIBLE
    actionView.setOnClickListener { view ->
      listener.onClick(view)
      dismiss()
    }
    return this
  }

  private class ContentViewCallback(private val content: View) : BaseTransientBottomBar.ContentViewCallback {

    override fun animateContentIn(delay: Int, duration: Int) {
      ViewCompat.setScaleY(content, 0f)
      ViewCompat.animate(content).scaleY(1f).setDuration(duration.toLong()).startDelay = delay.toLong()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
      ViewCompat.setScaleY(content, 1f)
      ViewCompat.animate(content).scaleY(0f).setDuration(duration.toLong()).startDelay = delay.toLong()
    }
  }

  companion object {

    fun make(parent: ViewGroup, @Duration duration: Int): CustomSnackbar {
      val inflater = LayoutInflater.from(parent.context)
      val content = inflater.inflate(R.layout.snackbar_view, parent, false)
      val viewCallback = ContentViewCallback(content)
      val customSnackbar = CustomSnackbar(parent, content, viewCallback)
      customSnackbar.duration = Snackbar.LENGTH_INDEFINITE
      return customSnackbar
    }
  }
}