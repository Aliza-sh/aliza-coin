package com.aliza.alizacoin.features.marketActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aliza.alizacoin.R
import com.aliza.alizacoin.apiManager.BASE_URL_IMAGE
import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.databinding.ItemMarketBinding
import com.bumptech.glide.Glide

class MarketAdapter(
    private var data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    lateinit var binding: ItemMarketBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindViews(dataCoin: CoinsData.Data) {

            binding.txtCoinName.text = dataCoin.coinInfo.fullName
            binding.txtPrice.text = dataCoin.dISPLAY.uSD.pRICE

            val change = dataCoin.rAW.uSD.cHANGEPCT24HOUR
            if (change > 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                //dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 4) + "%"
                binding.txtChange.text = dataCoin.dISPLAY.uSD.cHANGEPCT24HOUR  + "%"
            } else if (change < 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                //dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
                binding.txtChange.text = dataCoin.dISPLAY.uSD.cHANGEPCT24HOUR  + "%"
            } else {
                binding.txtChange.text = "0%"
            }

            /*val marketCap = dataCoin.rAW.uSD.mKTCAP / 1000000000
            val indexDot = marketCap.toString().indexOf('.')
            "$" + marketCap.toString().substring(0 , indexDot + 3) + " B"*/
            binding.txtMarketCap.text = dataCoin.dISPLAY.uSD.mKTCAP

            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl)
                .into(binding.imgCoin)



            itemView.setOnClickListener {
                recyclerCallback.onCoinItemClicked(dataCoin)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        binding = ItemMarketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                binding.root.context,
                R.anim.anim_recycler_item
            )
        )
    }

    override fun getItemCount(): Int = data.size

    interface RecyclerCallback {
        fun onCoinItemClicked(dataCoin: CoinsData.Data)
    }

}