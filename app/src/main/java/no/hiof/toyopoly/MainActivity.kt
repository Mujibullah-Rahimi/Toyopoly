package no.hiof.toyopoly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val legoActivityBtn = findViewById<Button>(R.id.legoBtn)

        legoActivityBtn.setOnClickListener(View.OnClickListener {
            val legoActivityIntent = Intent(this, LegoActivity::class.java)
            startActivity(legoActivityIntent)
        })
    }
}