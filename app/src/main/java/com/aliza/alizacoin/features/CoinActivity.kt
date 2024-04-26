package com.aliza.alizacoin.features

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.aliza.alizacoin.base.BaseActivity
import com.aliza.alizacoin.databinding.ActivityCoinBinding

class CoinActivity : BaseActivity<ActivityCoinBinding>() {
    override fun inflateBinding() = ActivityCoinBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
}