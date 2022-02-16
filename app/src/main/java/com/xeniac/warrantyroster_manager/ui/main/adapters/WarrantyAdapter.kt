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
import coil.load
import coil.request.CachePolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ListAdContainerBinding
import com.xeniac.warrantyroster_manager.databinding.ListWarrantyBinding
import com.xeniac.warrantyroster_manager.di.CategoryTitleMapKey
import com.xeniac.warrantyroster_manager.models.ListItemType
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.VIEW_TYPE_AD
import com.xeniac.warrantyroster_manager.utils.Constants.VIEW_TYPE_WARRANTY
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_NATIVE_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.DateHelper.getDayWithSuffix
import com.xeniac.warrantyroster_manager.utils.DateHelper.getDaysUntilExpiry
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import java.text.SimpleDateFormat
import java.util.*

class WarrantyAdapter(
    private val activity: Activity,
    private val context: Context,
    private val viewModel: MainViewModel,
    private val clickInterface: WarrantyListClickInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categoryTitleMapKey: String
    private var imageLoader: ImageLoader

    private var requestAdCounter = 0

    companion object {
        private const val TAG = "WarrantyAdapter"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ImageLoaderProviderEntryPoint {
        fun getImageLoader(): ImageLoader
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CategoryTitleMapKeyProviderEntryPoint {
        @CategoryTitleMapKey
        fun getCategoryTitleMapKey(): String
    }

    init {
        val categoryTitleMapKeyProviderEntryPoint = EntryPointAccessors
            .fromApplication(context, CategoryTitleMapKeyProviderEntryPoint::class.java)
        val imageLoaderProviderEntryPoint = EntryPointAccessors
            .fromApplication(context, ImageLoaderProviderEntryPoint::class.java)

        categoryTitleMapKey = categoryTitleMapKeyProviderEntryPoint.getCategoryTitleMapKey()
        imageLoader = imageLoaderProviderEntryPoint.getImageLoader()
    }

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
            val warrantyBinding = ListWarrantyBinding.inflate(inflater, parent, false)
            WarrantyViewHolder(warrantyBinding)
        } else {
            val adContainerBinding = ListAdContainerBinding.inflate(inflater, parent, false)
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

    inner class WarrantyViewHolder(val binding: ListWarrantyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(warranty: Warranty) {
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
            binding.executePendingBindings()

            val category = warranty.categoryId?.let {
                viewModel.getCategoryById(it)
            }

            category?.let {
                binding.categoryTitle = it.title[categoryTitleMapKey]

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
                    Log.i(TAG, "RequestNativeAd Response: $tapsellPlusAdModel")
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
                    Log.e(TAG, "RequestNativeAd Error: $error")
                    if (requestAdCounter < 3) {
                        requestAdCounter++
                        requestNativeAd(adHolder)
                    }
                }
            })
    }
}