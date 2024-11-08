package kr.co.hconnect.snuh.mhd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.co.hconnect.snuh.mhd.bluetooth.ui.BluetoothConnectionActivity
import kr.co.hconnect.snuh.mhd.util.MyPermission

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 권한 획득
        MyPermission.registerPermissionLauncher(this)

        findViewById<Button>(R.id.testButton).setOnClickListener {
            val intent = Intent(this, BluetoothConnectionActivity::class.java)
            startActivity(intent)
        }
    }
}