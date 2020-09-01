package ni.devotion.floaty_head.floating_chathead

import ni.devotion.floaty_head.floating_chathead.FloatingBubbleTouchListener

open class DefaultFloatingBubbleTouchListener : FloatingBubbleTouchListener {
    override fun onDown(x: Float, y: Float) {}
    override fun onTap(expanded: Boolean) {}
    override fun onRemove() {}
    override fun onMove(x: Float, y: Float) {}
    override fun onUp(x: Float, y: Float) {}
}