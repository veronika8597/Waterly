package com.example.waterly

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


private val Context.dataStore by preferencesDataStore(name = "water_history")

object WaterDataStore {
    private val HISTORY_KEY = stringPreferencesKey("waterHistory")

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
}
