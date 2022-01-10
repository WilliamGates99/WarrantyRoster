package com.xeniac.warrantyroster_manager.mainactivity.warrantiesfragment

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.database.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.databinding.ListAdContainerBinding
import com.xeniac.warrantyroster_manager.databinding.ListWarrantyBinding
import com.xeniac.warrantyroster_manager.model.ListItemType
import com.xeniac.warrantyroster_manager.model.Warranty
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WarrantyAdapter(
    private val mActivity: Activity,
    private val mContext: Context,
    private val database: WarrantyRosterDatabase,
    private val warrantyList: List<Warranty>,
    private val titleMapKey: String,
    private val clickInterface: WarrantyListClickInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var requestAdCounter = 0

    companion object {
        private const val VIEW_TYPE_WARRANTY = 0
        private const val VIEW_TYPE_AD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_WARRANTY) {
            val warrantyBinding = ListWarrantyBinding.inflate(inflater, parent, false)
            WarrantyViewHolder(warrantyBinding)
        } else {
            val adContainerBinding = ListAdContainerBinding.inflate(inflater, parent, false)
            AdViewHolder(adContainerBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (warrantyList[position].itemType == ListItemType.WARRANTY) {
            VIEW_TYPE_WARRANTY
        } else {
            VIEW_TYPE_AD
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_WARRANTY) {
            (holder as WarrantyViewHolder).bindView(mContext, warrantyList[position])
        } else {
            (holder as AdViewHolder).bindView()
        }
    }

    override fun getItemCount(): Int = warrantyList.size

    inner class WarrantyViewHolder(val binding: ListWarrantyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(mContext: Context, warranty: Warranty) {
            try {
                val imageLoader = ImageLoader.Builder(mContext)
                    .componentRegistry { add(SvgDecoder(mContext)) }.build()

                val categoryTitle = database
                    .getCategoryDao().getCategoryById(warranty.categoryId).title[titleMapKey]
                val categoryIcon = database
                    .getCategoryDao().getCategoryById(warranty.categoryId).icon

                val expiryCalendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
                dateFormat.parse(warranty.expiryDate)?.let { expiryCalendar.time = it }
                val daysUntilExpiry = getDaysUntilExpiry(expiryCalendar)

                val expiryDate = "${
                    expiryCalendar.getDisplayName(
                        Calendar.MONTH, Calendar.LONG, Locale.getDefault()
                    )
                } ${getDayWithSuffix(expiryCalendar.get(Calendar.DAY_OF_MONTH))}, " +
                        "${expiryCalendar.get(Calendar.YEAR)}"

//                binding.ivIcon.load(categoryIcon, imageLoader)
                binding.tvTitle.text = warranty.title
                binding.tvCategory.text = categoryTitle
                binding.tvExpiryDate.text = expiryDate

                when {
                    daysUntilExpiry < 0 -> {
                        binding.tvStatus.text =
                            mContext.getString(R.string.warranties_list_status_expired)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(mContext, R.color.red)
                        )
                        binding.flStatus.backgroundTintList =
                            ContextCompat.getColorStateList(mContext, R.color.red)
                    }
                    daysUntilExpiry <= 30 -> {
                        binding.tvStatus.text =
                            mContext.getString(R.string.warranties_list_status_soon)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(mContext, R.color.orange)
                        )
                        binding.flStatus.backgroundTintList =
                            ContextCompat.getColorStateList(mContext, R.color.orange)
                    }
                    else -> {
                        binding.tvStatus.text =
                            mContext.getString(R.string.warranties_list_status_valid)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(mContext, R.color.green)
                        )
                        binding.flStatus.backgroundTintList =
                            ContextCompat.getColorStateList(mContext, R.color.green)
                    }
                }

                binding.cvWarranty.setOnClickListener {
                    clickInterface.onItemClick(warranty, daysUntilExpiry)
                }

                binding.ivIcon.load(categoryIcon, imageLoader)
            } catch (e: Exception) {
                Log.e("WarrantyViewHolder", "Exception: ${e.message}")
            }
        }
    }

    private fun getDayWithSuffix(day: Int): String {
        if (day in 11..13) {
            return "${day}th"
        }

        return when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    private fun getDaysUntilExpiry(expiryCalendar: Calendar): Long {
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.timeInMillis - todayCalendar.timeInMillis)
    }

    inner class AdViewHolder(val binding: ListAdContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val adHolder = TapsellPlus
            .createAdHolder(mActivity, binding.cvAdContainer, R.layout.list_ad_banner)

        fun bindView() {
            requestAdCounter = 0
            adHolder?.let { requestNativeAd(it) }
        }
    }

    private fun requestNativeAd(adHolder: AdHolder) {
        TapsellPlus.requestNativeAd(mActivity, Constants.WARRANTIES_NATIVE_ZONE_ID,
            object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.response(tapsellPlusAdModel)
                    Log.i("requestNativeAd", "response: $tapsellPlusAdModel")
                    TapsellPlus.showNativeAd(mActivity, tapsellPlusAdModel!!.responseId,
                        adHolder, object : AdShowListener() {
                            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                                super.onOpened(tapsellPlusAdModel)
                            }

                            override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                                super.onClosed(tapsellPlusAdModel)
                            }
                        })
                }

                override fun error(error: String?) {
                    super.error(error)
                    Log.e("requestNativeAd", "error: $error")
                    if (requestAdCounter < 3) {
                        requestAdCounter++
                        requestNativeAd(adHolder)
                    }
                }
            })
    }
}