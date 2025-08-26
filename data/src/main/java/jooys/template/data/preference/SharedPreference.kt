package jooys.template.data.preference

import android.app.Activity
import android.content.Context

/**
 * Created by icelancer on 15. 9. 5..
 * Fix to Kotlin by choedeb on 20.09.24
 */
class SharedPreference(private val context: Context, private val preferenceName: String) {

    fun put(key: String, value: String?) {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun put(key: String, value: Boolean) {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun put(key: String, value: Int) {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun put(key: String, value: Float) {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getValue(key: String, dftValue: String?): String? {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        return try {
            pref.getString(key, dftValue)!!
        } catch (e: Exception) {
            dftValue
        }
    }

    fun getValue(key: String, dftValue: Int): Int {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        return try {
            pref.getInt(key, dftValue)
        } catch (e: Exception) {
            dftValue
        }
    }

    fun getValue(key: String, dftValue: Float): Float {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        return try {
            pref.getFloat(key, dftValue)
        } catch (e: Exception) {
            dftValue
        }
    }

    fun getValue(key: String, dftValue: Boolean): Boolean {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        return try {
            pref.getBoolean(key, dftValue)
        } catch (e: Exception) {
            dftValue
        }
    }

    fun clear() {
        val pref = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.clear()
        editor.apply()
    }

}
