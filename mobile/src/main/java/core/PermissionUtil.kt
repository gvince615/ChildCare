package core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by: gvincent on 8/24/17
 */

object PermissionUtil {

  // will return true if permission gets granted
  fun handlePermission(activity: Activity, intentionCode: Int, permission: Permission): Boolean {

    if (!isPermissionGranted(permission, activity)) {
      requestPermission(permission, activity, intentionCode)
    }

    return isPermissionGranted(permission, activity)
  }

  private fun isPermissionGranted(permission: Permission, activity: Activity): Boolean {
    return ContextCompat.checkSelfPermission(activity, permission.permissionStr) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestPermission(permission: Permission, activity: Activity, intentionCode: Int) {
    ActivityCompat.requestPermissions(activity, arrayOf(permission.permissionStr), intentionCode)
  }

  //public static void handlePermissionDenied(String permission, Activity activity) {
  //  if (!activity.shouldShowRequestPermissionRationale(permission)) {
  //    switch (permission) {
  //      case Manifest.permission.CAMERA:
  //        handleShowPermissionNeededDialog(activity, R.string.permission_camera_dialog_body);
  //        break;
  //
  //      case Manifest.permission.READ_EXTERNAL_STORAGE:
  //      case Manifest.permission.WRITE_EXTERNAL_STORAGE:
  //        handleShowPermissionNeededDialog(activity, R.string.permission_storage_dialog_body);
  //        break;
  //
  //      default:
  //        Log.d("Unexpected permission: ", permission);
  //    }
  //  }
  //}

  //private static void handleShowPermissionNeededDialog(final Activity activity, final int messageId) {
  //
  //  SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity.getApplicationContext());
  //
  //  //show dialog to get permission from settings because user checked never show again
  //  if (sharedPreferencesUtil.hasSavedPermissionNeededDialog()) {
  //    LaunchpadDialogUtils launchpadDialogUtils = new LaunchpadDialogUtils(activity);
  //    launchpadDialogUtils.showAlertDialogDirectingUserToPermissionSettings(
  //        activity.getString(messageId));
  //  } else {
  //    sharedPreferencesUtil.savePermissionNeededDialog(true);
  //  }
  //}

  enum class Permission(val permissionStr: String) {
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA(
        Manifest.permission.CAMERA),
    USE_FINGERPRINT(Manifest.permission.USE_FINGERPRINT)
  }
}
