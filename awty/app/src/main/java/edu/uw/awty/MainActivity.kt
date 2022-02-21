package edu.uw.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    class SMS : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val number = intent?.getStringExtra("number")
            val message = intent?.getStringExtra("message")
            Toast.makeText(context, "Texting{$number}: $message", Toast.LENGTH_SHORT).show()
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        val minutes = findViewById<EditText>(R.id.Minutes)
        val btnAction = findViewById<Button>(R.id.btnStart)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var isSending = false
        val smsIntent = Intent(this, SMS::class.java)


        btnAction.setOnClickListener {
            if (phoneNumber.getText().toString() == "") {
                Toast.makeText(applicationContext, "phone number is empty", Toast.LENGTH_SHORT).show()
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
                if(btnAction.text=="Stop"){
                    phoneNumber.setText("")
                    minutes.setText("")
                    phoneNumber.isEnabled = true
                    minutes.isEnabled = true
                    btnAction.text="Start"
                    if(isSending){
                        val pendingIntent = PendingIntent.getBroadcast(this, 0, smsIntent, 0)
                        alarmManager.cancel(pendingIntent)
                        isSending=false
                    }

                }else {
                    phoneNumber.isEnabled = false
                    minutes.isEnabled = false
                    btnAction.text = "Stop"
                    var time = System.currentTimeMillis() + (5*1000)
                    var interval = Integer.parseInt(minutes.getText().toString()) * 60000
                    var destination = phoneNumber.getText().toString()
                    var message = destination + ": Are we there yet?"
                    smsIntent.putExtra("destination", destination)
                    smsIntent.putExtra("message", message)

                    val pendingIntent = PendingIntent.getBroadcast(this, 0, smsIntent,  0)
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time, interval.toLong(), pendingIntent)
                    isSending = true
                    Log.i("MAIN",
                        "Sending message'$message' to  $destination.")
                }
            }
        }
    }
}

