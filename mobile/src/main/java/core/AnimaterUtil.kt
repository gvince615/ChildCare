package core

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator


public class AnimateUtil(){
  fun expand(v: View, duration: Int, targetHeight: Int) {

    val prevHeight = v.getHeight()

    v.visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
      v.layoutParams.height = animation.animatedValue as Int
      v.requestLayout()
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
  }

  fun collapse(v: View, duration: Int, targetHeight: Int) {
    val prevHeight = v.height
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.addUpdateListener { animation ->
      v.layoutParams.height = animation.animatedValue as Int
      v.requestLayout()
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
  }

}