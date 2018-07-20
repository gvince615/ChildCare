package com.vince.childcare.core

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import com.vince.childcare.core.registration.ChildData
import java.util.*

class FirestoreUtil(private val db: FirebaseFirestore, private val context: Context) {

  fun updateOrAddUser(firebaseUser: FirebaseUser?) {
    val user = HashMap<String, Any>()
    firebaseUser?.displayName?.let { user.put("name", it) }
    firebaseUser?.email?.let { user.put("email", it) }
    firebaseUser?.uid?.let { user.put("uid", it) }

    firebaseUser?.uid?.let {
      // define path to document here
      db.collection("user_data").document("uid_" + firebaseUser.uid).set(user).addOnSuccessListener {
        Log.d(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_succeeded))
      }.addOnFailureListener {
        Log.e(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_failed))
      }
    }
  }

  fun saveChildData(firebaseUser: FirebaseUser?, childData: ChildData) {
    db
    .collection("user_data").document("uid_" + firebaseUser?.uid)
    .collection("child_registration_data").document("child_data")
    .set(childData).addOnSuccessListener {
      Log.d(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_succeeded))
    }.addOnFailureListener {
      Log.e(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_update_failed))
    }
  }
}
