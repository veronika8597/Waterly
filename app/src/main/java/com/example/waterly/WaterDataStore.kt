package com.example.waterly

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


private val Context.dataStore by preferencesDataStore(name = "water_history")

object WaterDataStore {
    private val HISTORY_KEY = stringPreferencesKey("waterHistory")
    private val GOAL_KEY = intPreferencesKey("dailyGoal")
    private val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminderEnabled")
    private val REMINDER_INTERVAL_KEY = intPreferencesKey("reminderInterval")
    private val QUIET_HOURS_START = intPreferencesKey("quietHoursStart") // hour (0-23)
    private val QUIET_HOURS_END = intPreferencesKey("quietHoursEnd")



    fun saveWaterHistory(context: Context, history: Map<String,Int>) {
        val json = Json.encodeToString(history)
        runBlocking {
            context.dataStore.edit { it[HISTORY_KEY] = json }
        }
    }

    fun loadWaterHistory(context: Context): Map<String, Int> {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[HISTORY_KEY]?.let { Json.decodeFromString(it) } ?: emptyMap()
        }
    }

    fun saveDailyGoal(context: Context, goal: Int) {
        runBlocking {
            context.dataStore.edit { it[GOAL_KEY] = goal }
        }
    }

    fun loadDailyGoal(context: Context): Int {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[GOAL_KEY] ?: 2000 // default to 2000 ml
        }
    }

    fun saveReminderEnabled(context: Context, isEnabled: Boolean) {
        runBlocking {
            context.dataStore.edit { it[REMINDER_ENABLED_KEY] = isEnabled }
        }
    }

    fun loadReminderEnabled(context: Context): Boolean {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[REMINDER_ENABLED_KEY] ?: false // default to ON
        }
    }

    fun saveReminderInterval(context: Context, intervalHours: Int) {
        runBlocking {
            context.dataStore.edit { it[REMINDER_INTERVAL_KEY] = intervalHours }
        }
    }

    fun loadReminderInterval(context: Context): Int {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[REMINDER_INTERVAL_KEY] ?: 2 // default = 2 hrs
        }
    }

    fun saveQuietHours(context: Context, startHour: Int, endHour: Int) {
        runBlocking {
            context.dataStore.edit {
                it[QUIET_HOURS_START] = startHour
                it[QUIET_HOURS_END] = endHour
            }
        }
    }

    fun loadQuietHours(context: Context): Pair<Int, Int> {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            val start = prefs[QUIET_HOURS_START] ?: 22
            val end = prefs[QUIET_HOURS_END] ?: 7
            start to end
        }
    }

    fun clearAllData(context: Context) {
        runBlocking {
            context.dataStore.edit { it.clear() }
        }
    }

}
