package com.example.rxjava2datastoreapp

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.rxjava2.RxDataStore
import io.reactivex.Flowable
import io.reactivex.Single
import kotlin.reflect.KParameter

class UserPreferencesRepository(val dataStore: RxDataStore<Preferences>) {

    companion object {
        val string = stringPreferencesKey("string")
        val int = intPreferencesKey("int")
        val bool = booleanPreferencesKey("bool")
        val set = stringSetPreferencesKey("set")
    }

    fun <T> set(key: Preferences.Key<T>, value: T): Single<Preferences> =
        dataStore.updateDataAsync {
            val m = it.toMutablePreferences()
            m[key] = value
            return@updateDataAsync Single.just(m)
        }

    fun delete(key: Preferences.Key<*>): Single<Preferences> = dataStore.updateDataAsync{
        val m = it.toMutablePreferences()
        m.remove(key)
        return@updateDataAsync Single.just(m)
    }

    inline fun <reified T> get(key: Preferences.Key<T>, defaultValue: T? = null): Flowable<T> {
        return when (T::class) {
            String::class -> dataStore.data().map { it[key] ?: defaultValue ?: "" } as Flowable<T>
            Int::class -> dataStore.data().map { it[key] ?: defaultValue ?: 0 } as Flowable<T>
            Boolean::class -> dataStore.data().map { it[key] ?: defaultValue ?: false } as Flowable<T>
            Set::class -> dataStore.data().map { it[key] ?: defaultValue ?: emptySet<String>() } as Flowable<T>
            else -> throw IllegalStateException("Unsupported type")
        }
    }

}

