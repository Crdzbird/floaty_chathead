import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import ni.devotion.floaty_head.FloatingService

class MainActivity : AppCompatActivity() {
    private var bubbleLayout: LinearLayoutCompat? = null
    private var paramsMap: HashMap<String, Any>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //startService(Intent(applicationContext, FloatingService::class.java).putExtra("message", "HEU"))
    }

}