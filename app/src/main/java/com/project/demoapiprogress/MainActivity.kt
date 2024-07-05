package com.project.demoapiprogress

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.demoapiprogress.adapters.BottomSheetButtonAdapter
import com.project.demoapiprogress.adapters.ImagePagerAdapter
import com.project.demoapiprogress.adapters.PostUploadingAdapter
import com.project.demoapiprogress.databinding.ActivityMainBinding
import com.project.demoapiprogress.databinding.BottomSheetListBinding
import com.project.demoapiprogress.upload.PostProgressItem
import com.project.demoapiprogress.upload.ProgressItem
import com.project.demoapiprogress.upload.RequestBodyProgressManager
import com.project.demoapiprogress.upload.UploadCallback
import com.project.demoapiprogress.upload.UploadQueue
import com.project.demoapiprogress.utils.C
import com.project.demoapiprogress.utils.FileUtils
import com.project.demoapiprogress.utils.MyUtils
import com.thrubal.app.models.auth.ResponseModel
import retrofit2.Response

class MainActivity : AppCompatActivity(),
    ImagePagerAdapter.Callback,
    UploadCallback, RequestBodyProgressManager.Callback, PostUploadingAdapter.Callback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var context: Context
    private lateinit var pagerAdapter: ImagePagerAdapter
    private lateinit var postUploadingAdapter: PostUploadingAdapter
    private val userId: String = "9bad727a-de18-44fd-b345-8b9d26d72500"


    private var pagerPosition = 0

    // ------------------------- Gallery Variables ------------------------------
    private var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var gallerySettingIntentLauncher: ActivityResultLauncher<Intent>? = null
    private var galleryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val googlePixelGalleryPermissions =
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    private val googlePixelGalleryBelowPermissions =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val galleryRequestCode = 100
    private var mediaType = "image"
    private lateinit var media: ArrayList<String?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        initLaunchers()
        setupViewPager()

        binding.btnSubmit.setOnClickListener { uploadPost() }
        binding.rlAddMedia.setOnClickListener { showSelectMediaBottomSheet() }
        binding.llAddMoreImages.setOnClickListener { checkGalleryPermission() }
        binding.ivPostImage.setOnClickListener { }
        binding.ivClose.setOnClickListener {
            media.clear()
            updateUi()
        }


    }

    private fun uploadPost() {

        if (media.isEmpty() || media[0].isNullOrEmpty()) {
            showToast("Please select the media first")
            return
        }

        // Create a Progress Item and add it in the Upload Queue
        val post: ProgressItem = PostProgressItem(userId, mediaType, "", media, this)
        // API inside the ProgressItem will be called immediately after adding in the Upload Queue
        UploadQueue.addItem(post)
        showUploadRecyclerView()
        media.clear()
        updateUi()
    }

    private fun showToast(s: String) {
        Toast.makeText(baseContext, s, Toast.LENGTH_SHORT).show()
    }

    override fun onUploadSuccess(action: String) {
        showToast(action)
    }

    override fun onUploadFailed(action: String) {
        showToast(action)
    }

    override fun onUploadUnsuccessful(response: Response<ResponseModel?>) {
        showToast(response.message())
    }

    override fun onUploadFailure(t: Throwable) {
        showToast(t.message.toString())
    }


    private fun updateUi(isAdded: Boolean = false) {
        if (media.isEmpty()) {
            binding.pagerCard.visibility = View.GONE
            binding.compoundTab.visibility = View.GONE
            binding.ivPostImage.visibility = View.GONE
            binding.llAddMoreImages.visibility = View.GONE
            binding.ivPlay.visibility = View.GONE
            binding.ivClose.visibility = View.GONE
        } else {
            if (mediaType == "image") {
                binding.pagerCard.visibility = View.VISIBLE
                binding.llAddMoreImages.visibility = if (media.size < 5) View.VISIBLE else View.GONE
                binding.compoundTab.visibility = if (media.size > 1) View.VISIBLE else View.GONE
                binding.ivPlay.visibility = View.GONE
                binding.ivClose.visibility = View.GONE
                binding.ivPostImage.visibility = View.GONE
                pagerAdapter = ImagePagerAdapter(context, media, true, true, this)
                binding.pagerImages.adapter = pagerAdapter
                if (isAdded) binding.pagerImages.setCurrentItem(media.size, true)
            } else if (mediaType == "video") {
                binding.ivPostImage.visibility = View.VISIBLE
                binding.ivPlay.visibility = View.VISIBLE
                binding.ivClose.visibility = View.VISIBLE
                binding.llAddMoreImages.visibility = View.GONE
                binding.compoundTab.visibility = View.GONE
                binding.pagerCard.visibility = View.GONE
                MyUtils.loadImageOffline(context, media[0], binding.ivPostImage)
            }
        }
    }

    private fun showSelectMediaBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetBinding: BottomSheetListBinding =
            BottomSheetListBinding.inflate(LayoutInflater.from(context))

        val adapter = BottomSheetButtonAdapter(context,
            arrayListOf("Select Images", "Select Video"),
            object : BottomSheetButtonAdapter.Callback {
                override fun onItemClicked(position: Int) {
                    when (position) {
                        0 -> {
                            bottomSheetDialog.dismiss()
                            mediaType = "image"
                            checkGalleryPermission()
                        }

                        1 -> {
                            bottomSheetDialog.dismiss()
                            mediaType = "video"
                            checkGalleryPermission()
                        }
                    }
                }
            })

        sheetBinding.rvBottomSheetButtons.layoutManager = LinearLayoutManager(context)
        sheetBinding.rvBottomSheetButtons.adapter = adapter
        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.dismissWithAnimation = true
        sheetBinding.tvCancel.setOnClickListener { bottomSheetDialog.dismiss() }
    }


    private fun initLaunchers() {
        requestGalleryPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                var permissionGranted = false
                val permissions: Array<String>
                val manufacturer = Build.MANUFACTURER
                permissions = if (manufacturer.equals("google", ignoreCase = true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        googlePixelGalleryPermissions
                    } else {
                        googlePixelGalleryBelowPermissions
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        googlePixelGalleryPermissions
                    } else {
                        galleryPermissions
                    }
                }
                for (i in 0 until result.size) {
                    if (java.lang.Boolean.TRUE == result.get(permissions[i])) {
                        permissionGranted = true
                    } else {
                        permissionGranted = false
                        break
                    }
                }
                if (permissionGranted) {
                    openGallery()
                } else {
                    binding.layoutPermissionDenied.llPermissionDenied.visibility = View.VISIBLE
                    binding.layoutPermissionDenied.btnGoToSetting.setOnClickListener { _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        gallerySettingIntentLauncher!!.launch(intent)
                    }
                }
            }
        gallerySettingIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { checkGalleryPermission() }
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun checkGalleryPermission() {
        val manufacturer = Build.MANUFACTURER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (manufacturer.equals("google", ignoreCase = true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        binding.layoutPermissionDenied.llPermissionDenied.visibility = View.GONE
                        openGallery()
                    } else {
                        requestGalleryPermissionLauncher!!.launch(googlePixelGalleryPermissions)
                    }
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        binding.layoutPermissionDenied.llPermissionDenied.visibility = View.GONE
                        openGallery()
                    } else {
                        requestGalleryPermissionLauncher!!.launch(googlePixelGalleryBelowPermissions)
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        binding.layoutPermissionDenied.llPermissionDenied.visibility = View.GONE
                        openGallery()
                    } else {
                        requestGalleryPermissionLauncher!!.launch(googlePixelGalleryPermissions)
                    }
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        binding.layoutPermissionDenied.llPermissionDenied.visibility = View.GONE
                        openGallery()
                    } else {
                        requestGalleryPermissionLauncher!!.launch(galleryPermissions)
                    }
                }
            }
        } else {
            binding.layoutPermissionDenied.llPermissionDenied.visibility = View.GONE
            openGallery()
        }
    }

    private fun openGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "${mediaType}/*"
        startActivityIfNeeded(photoPickerIntent, galleryRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == galleryRequestCode) {
            if (data != null) {
                val imageUri = data.data
                if (imageUri != null) {
                    if (mediaType == "image") {
                        val image = FileUtils.getPath(context, imageUri)
                        media.add(image)
                        updateUi(true)
                    } else {
                        val image = FileUtils.getPath(context, imageUri)
                        media.clear()
                        media.add(image)
                        updateUi(true)
                    }
                }
            }
        }
    }


    private fun setupViewPager() {
        binding.pagerCard.visibility = View.GONE
        media = ArrayList()
        pagerAdapter = ImagePagerAdapter(context, media, true, true, this)
        binding.pagerImages.adapter = pagerAdapter
        binding.compoundTab.setupWithViewPager(binding.pagerImages)

        setMarginBetweenTabs()

        binding.pagerImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("ResourceAsColor", "SetTextI18n")
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                pagerPosition = position
            }

            override fun onPageSelected(position: Int) {
                pagerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setMarginBetweenTabs() {
        val tabs = binding.compoundTab.getChildAt(0) as ViewGroup
        for (i in 0 until tabs.childCount - 1) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginEnd = 15
            tab.layoutParams = layoutParams
            binding.compoundTab.requestLayout()
        }
    }

    override fun onClose(position: Int) {
        if (media.isEmpty() || position >= media.size) return

        media.removeAt(position)
        updateUi()
    }

    override fun onPagerImageClicked(position: Int, image: String, isOfflineImage: Boolean) {
        showToast("View Image")
    }


    // ---------------------------------  Showing Uploading Queue ----------------------------------
    private fun showUploadRecyclerView() {
        val uploadingList = UploadQueue.getList()
        if (uploadingList.isEmpty()) {
            binding.rvUploading.visibility = View.GONE
        } else {
            binding.rvUploading.visibility = View.VISIBLE
            binding.rvUploading.layoutManager = LinearLayoutManager(context)
            postUploadingAdapter = PostUploadingAdapter(context, uploadingList, this)
            binding.rvUploading.adapter = postUploadingAdapter
            binding.rvUploading.itemAnimator = null
            UploadQueue.addCallback(this)
        }
    }

    override fun onProgressUpdate(position: Long, percentage: Int) {
        Log.d(C.UPLOADING_TEST, "Home Fragment ->  $position : $percentage%")
        runOnUiThread {
            postUploadingAdapter!!.update(position.toInt())
        }
    }

    override fun onError(position: Long, message: String?) {
        Log.d(C.UPLOADING_TEST, "Home Fragment -> Error --> $position : $message")
        runOnUiThread {
            showToast(message.toString())
            postUploadingAdapter!!.update(position.toInt())
        }
    }

    override fun onFinish(position: Long) {
        Log.d(C.UPLOADING_TEST, "Home Fragment -> Finished --> $position")

        Handler(Looper.getMainLooper()).postDelayed({
            postUploadingAdapter.notifyRemoved(position.toInt())
        }, 1000)
    }

    override fun onRetryUpload(position: Int, model: ProgressItem) {
        if (!MyUtils.isConnected(context)) {
            showToast("No Internet Connection!")
        } else {
            UploadQueue.retry(position, model)
        }
    }

    override fun onUploadOptions(position: Int, model: ProgressItem) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetBinding: BottomSheetListBinding =
            BottomSheetListBinding.inflate(layoutInflater)

        val adapter = BottomSheetButtonAdapter(context,
            arrayListOf("Cancel Uploading"),
            object : BottomSheetButtonAdapter.Callback {
                override fun onItemClicked(position: Int) {
                    when (position) {
                        0 -> {
                            bottomSheetDialog.dismiss()
                            UploadQueue.cancelUpload(position, model)
                        }
                    }
                }
            })

        adapter.updateColor(0, R.color.red_delete)
        sheetBinding.rvBottomSheetButtons.layoutManager = LinearLayoutManager(context)
        sheetBinding.rvBottomSheetButtons.adapter = adapter
        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.dismissWithAnimation = true
        sheetBinding.tvCancel.setOnClickListener { bottomSheetDialog.dismiss() }
    }
}