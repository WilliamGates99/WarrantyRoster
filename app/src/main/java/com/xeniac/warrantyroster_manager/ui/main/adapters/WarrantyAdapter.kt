package com.xeniac.warrantyroster_manager.ui.main.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ListAdContainerBinding
import com.xeniac.warrantyroster_manager.databinding.ListWarrantyBinding
import com.xeniac.warrantyroster_manager.models.ListItemType
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.ui.main.MainViewModel
import com.xeniac.warrantyroster_manager.utils.CategoryHelper.getCategoryTitleMapKey
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_NATIVE_ZONE_ID
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WarrantyAdapter(
    private val activity: Activity,
    private val context: Context,
    private val viewModel: MainViewModel,
    private val clickInterface: WarrantyListClickInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var requestAdCounter = 0

    private val TAG = "WarrantyAdapter"

    companion object {
        private const val VIEW_TYPE_WARRANTY = 0
        private const val VIEW_TYPE_AD = 1
    }

    private val differCallback = object : DiffUtil.ItemCallback<Warranty>() {
        override fun areItemsTheSame(oldItem: Warranty, newItem: Warranty): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Warranty, newItem: Warranty): Boolean {
            return oldItem == newItem
        }
    }

    val warrantyListDiffer = AsyncListDiffer(this, differCallback)

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
        return if (warrantyListDiffer.currentList[position].itemType == ListItemType.WARRANTY) {
            VIEW_TYPE_WARRANTY
        } else {
            VIEW_TYPE_AD
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_WARRANTY) {
            (holder as WarrantyViewHolder).bindView(warrantyListDiffer.currentList[position])
        } else {
            (holder as AdViewHolder).bindView()
        }
    }

    override fun getItemCount(): Int = warrantyListDiffer.currentList.size

    inner class WarrantyViewHolder(val binding: ListWarrantyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(warranty: Warranty) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val category = warranty.categoryId?.let {
                    viewModel.getCategoryById(it)
//                    database.getCategoryDao().getCategoryById(it)
                }

                withContext(Dispatchers.Main) {
                    val expiryCalendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
                    dateFormat.parse(warranty.expiryDate!!)?.let { expiryCalendar.time = it }
                    val daysUntilExpiry = getDaysUntilExpiry(expiryCalendar)

                    val expiryDate = "${
                        expiryCalendar.getDisplayName(
                            Calendar.MONTH, Calendar.LONG, Locale.getDefault()
                        )
                    } ${getDayWithSuffix(expiryCalendar.get(Calendar.DAY_OF_MONTH))}, " +
                            "${expiryCalendar.get(Calendar.YEAR)}"

                    when {
                        daysUntilExpiry < 0 -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.red)
                            binding.statusTitle =
                                context.getString(R.string.warranties_list_status_expired)
                        }
                        daysUntilExpiry <= 30 -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.orange)
                            binding.statusTitle =
                                context.getString(R.string.warranties_list_status_soon)
                        }
                        else -> {
                            binding.statusColor = ContextCompat.getColor(context, R.color.green)
                            binding.statusTitle =
                                context.getString(R.string.warranties_list_status_valid)
                        }
                    }

                    binding.title = warranty.title
                    binding.expiryDate = expiryDate
                    binding.categoryTitle = category?.let {
                        it.title[getCategoryTitleMapKey(context)]
                    }
                    binding.executePendingBindings()

                    category?.let {
                        val imageLoader = ImageLoader.Builder(context)
                            .componentRegistry { add(SvgDecoder(context)) }.build()
                        binding.ivIcon.load(it.icon, imageLoader) {
                            memoryCachePolicy(CachePolicy.ENABLED)
                            diskCachePolicy(CachePolicy.ENABLED)
                            networkCachePolicy(CachePolicy.ENABLED)
                        }
                    }

                    binding.cvWarranty.setOnClickListener {
                        clickInterface.onItemClick(warranty, daysUntilExpiry)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
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
            .createAdHolder(activity, binding.cvAdContainer, R.layout.list_ad_banner)

        fun bindView() {
            requestAdCounter = 0
            adHolder?.let { requestNativeAd(it) }
        }
    }

    private fun requestNativeAd(adHolder: AdHolder) {
        TapsellPlus.requestNativeAd(activity, WARRANTIES_NATIVE_ZONE_ID,
            object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.response(tapsellPlusAdModel)
                    Log.i(TAG, "response: $tapsellPlusAdModel")
                    TapsellPlus.showNativeAd(activity, tapsellPlusAdModel!!.responseId,
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
                    Log.e(TAG, "error: $error")
                    if (requestAdCounter < 3) {
                        requestAdCounter++
                        requestNativeAd(adHolder)
                    }
                }
            })
    }
}