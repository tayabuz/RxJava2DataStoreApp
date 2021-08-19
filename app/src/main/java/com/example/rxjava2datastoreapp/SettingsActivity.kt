package com.example.rxjava2datastoreapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.rxjava2.rxPreferencesDataStore
import com.example.rxjava2datastoreapp.databinding.SettingsActivityBinding
import com.google.gson.Gson

class SettingsActivity : AppCompatActivity() {

    private val USER_PREFERENCES_NAME = "user_preferences"
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var binding: SettingsActivityBinding
    private val Context.rxDataStore by rxPreferencesDataStore(
        name = USER_PREFERENCES_NAME,
        produceMigrations = { context ->
            // Since we're migrating from SharedPreferences, add a migration based on the
            // SharedPreferences name
            listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        userPreferencesRepository =UserPreferencesRepository(rxDataStore)

        userPreferencesRepository.get(UserPreferencesRepository.string, "11111").subscribe({
            println("getPropertyString() ->  result: $it")
        },{
            it.printStackTrace()
        })
        userPreferencesRepository.get(UserPreferencesRepository.bool, true).subscribe({

            println("getPropertyBool() ->  result: $it")

        },{
            it.printStackTrace()
        })
        userPreferencesRepository.get(UserPreferencesRepository.int).subscribe({

            println("getPropertyInt() ->  result: $it")

        },{
            it.printStackTrace()
        })
        userPreferencesRepository.get(UserPreferencesRepository.set).subscribe({

            println("getPropertySet() ->  result: $it")

        },{
            it.printStackTrace()
        })

        var c = userPreferencesRepository.getObjectList<Cat>(UserPreferencesRepository.catsInJSONString).subscribe({

            println("getPropertyCatsJSON() ->  result: $it")

        },{
            it.printStackTrace()
        })

        userPreferencesRepository.getObject(UserPreferencesRepository.cat, Cat(1f, "Ted", "Sokoke")).subscribe({

            println("getPropertyCat() ->  result: $it")
            it.age
            var r = it
            val t = 0

        }, {
            it.printStackTrace()
        })

        var cat = Cat(2f, "Masik", "Bengal")

        var cat1 = Cat(3f, "Manya", "Korat")

        var cat2 = Cat(5f, "Shao", "Manx")

        var cats = listOf(cat, cat1, cat2)


        binding.buttonSave.setOnClickListener {
            userPreferencesRepository.set(
                UserPreferencesRepository.bool, binding.switchValue.isChecked)
            userPreferencesRepository.set( UserPreferencesRepository.string, binding.edittextValue.text.toString())
            userPreferencesRepository.set( UserPreferencesRepository.int, 55)
            userPreferencesRepository.set(UserPreferencesRepository.set, setOf("test", "qwerty"))
            userPreferencesRepository.delete(UserPreferencesRepository.int)

            userPreferencesRepository.setObject(UserPreferencesRepository.catsInJSONString, cats)
            userPreferencesRepository.setObject(UserPreferencesRepository.cat, cat)
        }

    }

    fun calcActionTime(tag: String, action : () -> Unit) {
        val startTime = System.currentTimeMillis()
        action()
        println("$tag . -> Time: ${System.currentTimeMillis() - startTime}")
    }
}