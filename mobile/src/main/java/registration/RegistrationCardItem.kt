package registration

import android.support.annotation.IntDef

open class RegistrationCardItem<T>(internal var `object`: T, @field:ViewType internal var viewType: Int) {
  @kotlin.annotation.Retention()
  @IntDef(PARENT, CHILD)
  annotation class ViewType

  companion object {
    const val PARENT = 0
    const val CHILD = 1
  }
}
