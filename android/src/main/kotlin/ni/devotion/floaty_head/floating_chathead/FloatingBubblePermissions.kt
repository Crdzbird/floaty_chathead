package ni.devotion.floaty_head.floating_chathead

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object FloatingBubblePermissions {
    private const val REQUEST_CODE_ASK_PERMISSIONS = 1201
    private fun requiresPermission(context: Context?) = Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)

    fun Activity.startPermissionRequest() {
        if (Build.VERSION.SDK_INT >= 23 && requiresPermission(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.packageName))
            this.startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS)
        }
    }
}