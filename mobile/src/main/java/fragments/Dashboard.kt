package fragments

import activities.MessageBoardActivity
import activities.RegistrationActivity
import activities.SetupActivity
import activities.TodoActivity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class Dashboard : Fragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment

    val view: View = inflater.inflate(R.layout.fragment_dashboard, container,
        false)

    view.registration_cardview.setOnClickListener { startRegistrationActivity() }
    view.msg_board_cardview.setOnClickListener { startMessageBoardActivity() }
    view.todo_cardview.setOnClickListener { startTodoActivity() }
    view.setup_cardview.setOnClickListener { startSetupActivity() }

    return view
  }

  private fun startMessageBoardActivity() {
    val intent = Intent(activity, MessageBoardActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it, msg_board_view,
          ViewCompat.getTransitionName(msg_board_view))
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startTodoActivity() {
    val intent = Intent(activity, TodoActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          todo_view,
          ViewCompat.getTransitionName(todo_view))
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startSetupActivity() {
    val intent = Intent(activity, SetupActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          setup_view,
          ViewCompat.getTransitionName(setup_view))
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startRegistrationActivity() {
    val intent = Intent(activity, RegistrationActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          registration_view,
          ViewCompat.getTransitionName(registration_view))
    }
    startActivity(intent, options?.toBundle())
  }

}
