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


            if (dataCoin.coinInfo?.fullName != null) {
                binding.txtCoinName.text = dataCoin.coinInfo.fullName

            } else {
                binding.txtPrice.text = ""
            }


            if (dataCoin.dISPLAY?.uSD?.pRICE != null) {
                binding.txtPrice.text = dataCoin.dISPLAY.uSD.pRICE

            } else {
                binding.txtPrice.text = ""
            }
            coinChange(dataCoin)
            marketcap(dataCoin)
            coinImage(dataCoin, itemView)

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

    private fun coinChange(dataCoin: CoinsData.Data) {
        if (dataCoin.rAW?.uSD?.cHANGEPCT24HOUR != null) {
            val changed = dataCoin.rAW.uSD.cHANGEPCT24HOUR
            if (changed > 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.txtChange.text =
                    dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 4)
            } else if (changed < 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.txtChange.text =
                    dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5)
            } else {
                binding.txtChange.text = "0.0"
            }
        } else {
            binding.txtChange.text = ""
        }
    }
    private fun marketcap(dataCoin: CoinsData.Data) {
        if (dataCoin.rAW?.uSD?.mKTCAP != null) {
            val marketCap = dataCoin.rAW.uSD.mKTCAP.div(1000000000)
            val indexDot = marketCap.toString().indexOf('.')
            binding.txtMarketCap.text =
                "$" + marketCap.toString().substring(0, indexDot + 3) + " B"
        } else {
            binding.txtMarketCap.text = ""
        }
    }
    private fun coinImage(dataCoin: CoinsData.Data, itemView: View) {
        if (dataCoin.coinInfo?.imageUrl != null) {
            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl)
                .into(binding.imgCoin)
        } else {
            Glide
                .with(itemView)
                .load(R.drawable.img_logo)
                .into(binding.imgCoin)
        }
    }

    interface RecyclerCallback {
        fun onCoinItemClicked(dataCoin: CoinsData.Data)
    }

}