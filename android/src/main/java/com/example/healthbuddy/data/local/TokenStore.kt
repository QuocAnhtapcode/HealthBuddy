package com.example.healthbuddy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val KEY_TOKEN = stringPreferencesKey("token")

    val tokenFlow: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }

    suspend fun saveToken(token: String?) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token?:""
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
    }
}
