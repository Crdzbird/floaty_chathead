package ni.devotion.floaty_head_example;

import android.os.Bundle;
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {

    /**
     * When using this plugin please set the [Application().onCreate()]
     * if this isn't setted the chathead cannot communicate with the dart-client code
     */
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        Application().onCreate()
    }
}