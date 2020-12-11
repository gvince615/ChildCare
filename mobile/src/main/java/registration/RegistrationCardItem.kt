package registration

import androidx.annotation.IntDef

open class RegistrationCardItem<T>(internal var `object`: T, @field:ViewType internal var viewType: Int) {
  @kotlin.annotation.Retention()
  @IntDef(PARENT, CHILD, MEDICATION, PEDIATRICIAN, BILLING)
  annotation class ViewType

  companion object {
    const val PARENT = 0
    const val CHILD = 1
    const val MEDICATION = 2
    const val PEDIATRICIAN = 3
    const val BILLING = 4
  }
}
