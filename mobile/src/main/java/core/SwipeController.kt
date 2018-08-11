package core

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.util.Log
import android.view.MotionEvent


internal enum class ButtonsState {
  GONE,
  LEFT_VISIBLE,
  RIGHT_VISIBLE
}

internal class SwipeController(private val context: Context, private val buttonsActions: SwipeControllerActions) : Callback() {

  private var swipeBack = false
  private var buttonShowedState = ButtonsState.GONE
  private var buttonInstance: RectF? = null
  private var currentItemViewHolder: RecyclerView.ViewHolder? = null


  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    return ItemTouchHelper.Callback.makeMovementFlags(0, LEFT or RIGHT)
  }

  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
    return false
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    Log.d("", "")
  }

  override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
    if (swipeBack) {
      swipeBack = buttonShowedState != ButtonsState.GONE
      return 0
    }
    return super.convertToAbsoluteDirection(flags, layoutDirection)
  }

  override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
      isCurrentlyActive: Boolean) {
    var dX = dX
    if (actionState == ACTION_STATE_SWIPE) {
      if (buttonShowedState != ButtonsState.GONE) {
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth)
        if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      } else {
        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
    }

    if (buttonShowedState == ButtonsState.GONE) {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
    currentItemViewHolder = viewHolder
  }

  private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
      isCurrentlyActive: Boolean) {
    recyclerView.setOnTouchListener { v, event ->
      swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
      if (swipeBack) {
        if (dX < -buttonWidth)
          buttonShowedState = ButtonsState.RIGHT_VISIBLE
        else if (dX > buttonWidth)
          buttonShowedState = ButtonsState.LEFT_VISIBLE

        if (buttonShowedState != ButtonsState.GONE) {
          setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
          setItemsClickable(recyclerView, false)
        }
      }
      false
    }
  }

  private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
      isCurrentlyActive: Boolean) {
    recyclerView.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
      false
    }
  }

  private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
      isCurrentlyActive: Boolean) {
    recyclerView.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        super@SwipeController.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
        recyclerView.setOnTouchListener { v, event -> false }
        setItemsClickable(recyclerView, true)
        swipeBack = false

        if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(event.x, event.y)) {
          if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonsActions.onEditClicked(viewHolder.adapterPosition)
          } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonsActions.onDeleteClicked(viewHolder.adapterPosition)
          }
        }
        buttonShowedState = ButtonsState.GONE
        currentItemViewHolder = null
      }
      false
    }
  }

  private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
    for (i in 0 until recyclerView.childCount) {
      recyclerView.getChildAt(i).isClickable = isClickable
    }
  }

  private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
    val buttonWidthWithoutPadding = buttonWidth - 20
    val corners = 16f

    val itemView = viewHolder.itemView
    val p = Paint()

    val leftButton = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left + buttonWidthWithoutPadding, itemView.bottom.toFloat())
    p.color = Color.BLUE
    c.drawRoundRect(leftButton, corners, corners, p)
    drawImage((context.resources.getDrawable(android.R.drawable.ic_menu_edit, null) as BitmapDrawable).bitmap, c, leftButton, p)

    val rightButton = RectF(itemView.right - buttonWidthWithoutPadding, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
    p.color = Color.RED
    c.drawRoundRect(rightButton, corners, corners, p)

    drawImage((context.resources.getDrawable(android.R.drawable.ic_menu_delete, null) as BitmapDrawable).bitmap, c, rightButton, p)

    buttonInstance = null
    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
      buttonInstance = leftButton
    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
      buttonInstance = rightButton
    }
  }

  private fun drawImage(bitmap: Bitmap, c: Canvas, button: RectF, p: Paint) {
    val imageRectF = RectF(button.right - 40f, button.top + 55f, button.left + 40f, button.bottom - 55f)
    c.drawBitmap(bitmap, null, imageRectF, p)
  }

  fun onDraw(c: Canvas) {
    if (currentItemViewHolder != null) {
      drawButtons(c, currentItemViewHolder!!)
    }
  }

  companion object {

    private const val buttonWidth = 300f
  }
}