package edu.coe.hughes.location21

import android.content.Context
import android.util.Log
class Prefs {
    companion object {
        val PREFS_NAME = "edu.coe.richmond.location21.MainActivity"
        val LOGKEY = "Prefs"

        @JvmStatic
        fun savePref(context: Context, lat: Double, lon: Double) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString("Lat", lat.toString())
            prefs.putString("Long", lon.toString())
            Log.i(LOGKEY, lat.toString())
            Log.i(LOGKEY, lon.toString())
            prefs.apply()
        }

        @JvmStatic
        fun loadLat(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val latValue = prefs.getString("Lat", "EXAMPLE")
            if (latValue != null) {
                Log.i(LOGKEY, latValue)
            }
            return latValue
        }

        @JvmStatic
        fun loadLong(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val longValue = prefs.getString("Long", "EXAMPLE")
            if (longValue != null) {
                Log.i(LOGKEY, longValue)
            }
            return longValue
        }

        @JvmStatic
        fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove("Lat")
            prefs.remove("Long")
            prefs.apply()
        }
    }
}