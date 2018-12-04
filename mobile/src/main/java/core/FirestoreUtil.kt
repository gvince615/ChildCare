package core

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R


class FirestoreUtil(private val db: FirebaseFirestore, private val context: Context) {

  fun updateOrAddUser(firebaseUser: FirebaseUser?) {
    val user = HashMap<String, Any>()
    firebaseUser?.displayName?.let { user.put("name", it) }
    firebaseUser?.email?.let { user.put("email", it) }
    firebaseUser?.uid?.let { user.put("uid", it) }

    firebaseUser?.uid?.let {
      // define path to document here
      db.collection(COLLECTION_USER_DATA).document(user["name"].toString().replace(" ", "") + PREFIX_UID + firebaseUser.uid).set(
          user).addOnSuccessListener {
        Log.d(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_succeeded))
      }.addOnFailureListener {
        Log.e(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_failed))
      }
    }
  }
}
