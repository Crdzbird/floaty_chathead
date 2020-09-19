package ni.devotion.floaty_head_example;

import androidx.annotation.NonNull
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.utils.Managment
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.app.FlutterApplication
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
import io.flutter.embedding.engine.FlutterEngine

class Application : FlutterApplication(), PluginRegistry.PluginRegistrantCallback {
      override fun onCreate() {
          super.onCreate()
          FloatyHeadPlugin().setPluginRegistrant(this)
          Managment.pluginRegistrantC = this
      }

     override fun registerWith(registry: PluginRegistry) {
        FloatyHeadPlugin().registerWith(registry.registrarFor(this.packageName))
     }
     
  }