package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.AdContainerListBinding
import com.xeniac.warrantyroster_manager.databinding.ListItemWarrantyBinding
import com.xeniac.warrantyroster_manager.util.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.util.Constants.APPLOVIN_WARRANTIES_NATIVE_UNIT_ID
import com.xeniac.warrantyroster_manager.util.Constants.TAPSELL_WARRANTIES_NATIVE_ZONE_ID
import com.xeniac.warrantyroster_manager.util.Constants.VIEW_TYPE_AD
import com.xeniac.warrantyroster_manager.util.Constants.VIEW_TYPE_WARRANTY
import com.xeniac.warrantyroster_manager.util.DateHelper.getDaysUntilExpiry
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.ListItemType
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

class WarrantyAdapter @Inject constructor(
    private val imageLoader: ImageLoader,
    private val dateFormat: SimpleDateFormat
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var activity: Activity
    lateinit var context: Context
    lateinit var warrantiesViewModel: WarrantiesViewModel

    private lateinit var warrantyClickInterface: WarrantyListClickInterface

    private val diffCallback = object : DiffUtil.ItemCallback<Warranty>() {
        override fun areItemsTheSame(oldItem: Warranty, newItem: Warranty): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Warranty, newItem: Warranty): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var warrantiesList: List<Warranty>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_WARRANTY) {
            val warrantyBinding = ListItemWarrantyBinding.inflate(inflater, parent, false)
            WarrantyViewHolder(warrantyBinding)
        } else {
            val adContainerBinding = AdContainerListBinding.inflate(inflater, parent, false)
            AdViewHolder(adContainerBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (warrantiesList[position].itemType == ListItemType.WARRANTY) {
            VIEW_TYPE_WARRANTY
        } else {
            VIEW_TYPE_AD
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_WARRANTY) {
            (holder as WarrantyViewHolder).bindView(warrantiesList[position])
        } else {
            (holder as AdViewHolder).bindView()
        }
    }

    override fun getItemCount(): Int = warrantiesList.size

    inner class WarrantyViewHolder(val binding: ListItemWarrantyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(warranty: Warranty) = CoroutineScope(Dispatchers.Main).launch {
            val isLifetime = warranty.isLifetime ?: false
            if (isLifetime) {
                val expiryDate = context.getString(R.string.warranties_list_is_lifetime)
                binding.expiryDate = expiryDate
                binding.statusColor = ContextCompat.getColor(context, R.color.green)
                binding.statusTitle = context.getString(R.string.warranties_list_status_valid)
            } else {
                warranty.expiryDate?.let { date ->
                    val expiryCalendar = Calendar.getInstance().apply {
                        dateFormat.parse(date)?.let { time = it }

                        val dayWithSuffix = context.resources.getStringArray(
                            R.array.warranties_list_day_with_suffix
                        )[get(Calendar.DAY_OF_MONTH) - 1]
                        val monthName = context.resources.getStringArray(
                            R.array.warranties_list_month_name
                        )[get(Calendar.MONTH)]
                        val year = get(Calendar.YEAR)

                        val expiryDate = context.getString(
                            R.string.warranties_list_format_date,
                            monthName, dayWithSuffix, year
                        )

                        binding.expiryDate = expiryDate
                    }

                    val daysUntilExpiry = getDaysUntilExpiry(expiryCalendar)
                    binding.statusTitle = when {
                        daysUntilExpiry < 0 -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.red)
                            context.getString(R.string.warranties_list_status_expired)
                        }
                        daysUntilExpiry <= 30 -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.orange)
                            context.getString(R.string.warranties_list_status_soon)
                        }
                        else -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.green)
                            context.getString(R.string.warranties_list_status_valid)
                        }
                    }
                }
            }

            binding.title = warranty.title
            binding.executePendingBindings()

            delay(1) // Delay fixed issue #22
            val category = warranty.categoryId?.let {
                warrantiesViewModel.getCategoryById(it)
            }

            category?.let {
                binding.categoryTitle = it.title[warrantiesViewModel.getCategoryTitleMapKey()]
                loadCategoryImage(context, it.icon, imageLoader, binding.ivIcon, binding.cpiIcon)
            }

            binding.cvWarranty.setOnClickListener { warrantyClickInterface.onItemClick(warranty) }
        }
    }

    fun setOnWarrantyItemClickListener(clickInterface: WarrantyListClickInterface) {
        this.warrantyClickInterface = clickInterface
    }

    inner class AdViewHolder(val binding: AdContainerListBinding) :
        RecyclerView.ViewHolder(binding.root), MaxAdRevenueListener {

        private lateinit var appLovinNativeAdContainer: ViewGroup
        private lateinit var appLovinAdLoader: MaxNativeAdLoader
        private var appLovinNativeAd: MaxAd? = null

        fun bindView() {
            requestAppLovinNativeAd()
        }

        private fun requestAppLovinNativeAd() {
            appLovinNativeAdContainer = binding.cvAdContainer
            appLovinAdLoader = MaxNativeAdLoader(
                APPLOVIN_WARRANTIES_NATIVE_UNIT_ID,
                context
            ).apply {
                setRevenueListener(this@AdViewHolder)
                setNativeAdListener(AppLovinNativeAdListener())
                loadAd(createNativeAdView())
            }
        }

        private fun createNativeAdView(): MaxNativeAdView {
            val nativeAdBinder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.ad_banner_list_applovin).apply {
                    setIconImageViewId(R.id.iv_banner_list_icon)
                    setTitleTextViewId(R.id.tv_banner_list_title)
                    setBodyTextViewId(R.id.tv_banner_list_body)
                    setCallToActionButtonId(R.id.btn_banner_list_action)
                }.build()
            return MaxNativeAdView(nativeAdBinder, context)
        }

        private inner class AppLovinNativeAdListener : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd?) {
                super.onNativeAdLoaded(nativeAdView, nativeAd)
                Timber.i("AppLovin onNativeAdLoaded")

                appLovinNativeAd?.let {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    appLovinAdLoader.destroy(it)
                }

                showNativeAdContainer()
                appLovinNativeAd = nativeAd
                appLovinNativeAdContainer.removeAllViews()
                appLovinNativeAdContainer.addView(nativeAdView)
            }

            override fun onNativeAdLoadFailed(adUnitId: String?, error: MaxError?) {
                super.onNativeAdLoadFailed(adUnitId, error)
                Timber.e("AppLovin onNativeAdLoadFailed: ${error?.message}")
                initTapsellAdHolder()
            }

            override fun onNativeAdClicked(nativeAd: MaxAd?) {
                super.onNativeAdClicked(nativeAd)
                Timber.i("AppLovin onNativeAdClicked")
            }
        }

        override fun onAdRevenuePaid(ad: MaxAd?) {
            Timber.i("AppLovin onAdRevenuePaid")
        }

        private fun initTapsellAdHolder() {
            val adHolder = TapsellPlus
                .createAdHolder(activity, binding.cvAdContainer, R.layout.ad_banner_list_tapsell)
            adHolder?.let { requestTapsellNativeAd(it) }
        }

        private fun requestTapsellNativeAd(adHolder: AdHolder) {
            TapsellPlus.requestNativeAd(
                activity,
                TAPSELL_WARRANTIES_NATIVE_ZONE_ID,
                object : AdRequestCallback() {
                    override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                        super.response(tapsellPlusAdModel)
                        Timber.i("requestTapsellNativeAd onResponse")
                        tapsellPlusAdModel?.let {
                            showNativeAd(adHolder, it.responseId)
                        }
                    }

                    override fun error(error: String?) {
                        super.error(error)
                        Timber.e("requestTapsellNativeAd onError: $error")
                    }
                }
            )
        }

        private fun showNativeAd(adHolder: AdHolder, responseId: String) {
            showNativeAdContainer()
            TapsellPlus.showNativeAd(activity, responseId, adHolder, object : AdShowListener() {
                override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.onOpened(tapsellPlusAdModel)
                }

                override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.onClosed(tapsellPlusAdModel)
                }
            })
        }

        private fun showNativeAdContainer() {
            binding.cvAdContainer.visibility = VISIBLE
        }
    }
}