package core;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by: gvincent on 8/24/17
 */

public final class PermissionUtil {

  private PermissionUtil() {

  }

  // will return true if permission gets granted
  public static boolean handlePermission(Activity activity, int intentionCode, Permission permission) {

    if (!isPermissionGranted(permission, activity)) {
      requestPermission(permission, activity, intentionCode);
    }

    return isPermissionGranted(permission, activity);
  }

  private static boolean isPermissionGranted(Permission permission, Activity activity) {
    return ContextCompat.checkSelfPermission(activity, permission.getPermissionStr()) == PackageManager.PERMISSION_GRANTED;
  }

  private static void requestPermission(Permission permission, Activity activity, int intentionCode) {
    ActivityCompat.requestPermissions(activity, new String[] {
        permission.getPermissionStr()
    }, intentionCode);
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

  public enum Permission {
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA(
        Manifest.permission.CAMERA), USE_FINGERPRINT(Manifest.permission.USE_FINGERPRINT);

    private final String permissionStr;

    Permission(String permissionStr) {
      this.permissionStr = permissionStr;
    }

    public String getPermissionStr() {
      return permissionStr;
    }
  }
}
