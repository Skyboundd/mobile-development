package com.skybound

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.skybound.databinding.ActivityMainBinding
import com.skybound.ui.aftersignin.AfterSignInActivity
import com.skybound.ui.settings.SettingsViewModel
import com.skybound.ui.signin.SignInActivity
import com.skybound.ui.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedCareer = intent.getStringExtra("selectedCareer")


        // Inisialisasi SettingsViewModel menggunakan ViewModelFactory
        val factory = ViewModelFactory.getInstance(this)
        settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        observeSession()

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }

    private fun observeSession() {
        mainViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                // Ambil token dari session (pastikan tersedia di model user)
                val token = user.token ?: return@observe

                // Fetch user data menggunakan token
                mainViewModel.fetchUser(token)

                mainViewModel.userResponse.observe(this) { userResponse ->
                    if (userResponse.roadmaps != "") {
                        // Jika sudah memiliki roadmap, tampilkan BottomNavigationView
                        binding.root.visibility = View.VISIBLE

                        val navView: BottomNavigationView = binding.navView
                        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
                        val navController = navHostFragment.navController
                        navView.setupWithNavController(navController)
                    } else {
                        // Jika belum memiliki roadmap, arahkan ke AfterSignInActivity
                        startActivity(Intent(this, AfterSignInActivity::class.java))
                        finish()
                    }
                }
            } else {
                // Jika user belum login, arahkan ke SignInActivity
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }
}
