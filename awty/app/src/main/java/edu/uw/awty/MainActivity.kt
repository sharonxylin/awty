package edu.uw.awty

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.time.Instant
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {
    private class IntentListener : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("IntentListener", "Broadcast Message")
            Toast.makeText(context, "(425) 555-1212: Are we there yet?" , Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        val minutes = findViewById<EditText>(R.id.Minutes)
        val btnAction = findViewById<Button>(R.id.btnStart)

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("(425) 555-1212: Are we there yet?")
        val receiver = IntentListener()
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val intentFilter = IntentFilter()
        var isSending = false
        intentFilter.addAction("(425) 555-1212: Are we there yet?")
        this.registerReceiver(receiver, intentFilter)



        btnAction.setOnClickListener {
            if(btnAction.text=="Stop"){
                phoneNumber.setText("")
                minutes.setText("")
                phoneNumber.isEnabled = true
                minutes.isEnabled = true
                btnAction.text="Start"
                if(isSending){
                    alarmManager.cancel(pendingIntent)
                    isSending=false
                }

            }else {
                if (phoneNumber.getText().toString() == "") {
                    Toast.makeText(applicationContext, "phone number is empty", Toast.LENGTH_SHORT)
                        .show()
                } else if (minutes.getText().toString() == "") {
                    Toast.makeText(applicationContext, "minutes is empty", Toast.LENGTH_SHORT)
                        .show()
                } else if (Integer.parseInt(minutes.getText().toString()) < 0) {
                    Toast.makeText(
                        applicationContext,
                        "minutes must not be negative",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (Integer.parseInt(minutes.getText().toString()) == 0) {
                    Toast.makeText(
                        applicationContext,
                        "minutes must not be zero",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    phoneNumber.isEnabled = false
                    minutes.isEnabled = false
                    btnAction.text = "Stop"
                    var time = System.currentTimeMillis() + (5*1000)
                    var interval = Integer.parseInt(minutes.getText().toString())*60000
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval.toLong(), pendingIntent)
                    isSending = true
                }
            }
        }
    }
}