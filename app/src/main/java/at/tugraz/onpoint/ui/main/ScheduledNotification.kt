package at.tugraz.onpoint.ui.main

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import at.tugraz.onpoint.MainTabbedActivity
import at.tugraz.onpoint.R


class ScheduledNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intentToOpenTheApp = Intent(context, MainTabbedActivity::class.java)
        intentToOpenTheApp.putExtra("tabToOpen", TAB_INDEX_ASSIGNMENT)
        val pendingIntentToOpenApp = PendingIntent.getActivity(
            context,
            intent.getIntExtra("notificationId", 0),
            intentToOpenTheApp,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Build notification based on Intent
        val notification = NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_baseline_uni_24)
            .setContentTitle(intent.getStringExtra("title"))
            .setContentText(intent.getStringExtra("text"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntentToOpenApp)
            .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(intent.getIntExtra("notificationId", 0), notification)
    }
}
