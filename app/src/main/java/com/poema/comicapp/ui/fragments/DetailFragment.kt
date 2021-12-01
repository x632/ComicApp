package com.poema.comicapp.ui.fragments

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.poema.comicapp.R
import com.poema.comicapp.data_sources.model.ComicPostCache
import com.poema.comicapp.data_sources.model.GlobalList
import com.poema.comicapp.databinding.FragmentDetailBinding
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels()
    private lateinit var titleHolder: TextView
    private lateinit var altHolder: TextView
    private lateinit var imageHolder: View
    private lateinit var progBarHolder: ProgressBar
    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private var cachedPostIsInitialized = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        altHolder = binding.tvAlt
        titleHolder = binding.textView
        imageHolder = binding.imageView
        progBarHolder = binding.progressBar2


        val heartHolder = binding.heartHolder

        val explBtn = binding.btnWeb

        viewModel.number = args.id
        viewModel.index = viewModel.indexInList(viewModel.number)

        if (GlobalList.globalList[viewModel.index!!].isNew) {
            cancelNotification()
        }
        GlobalList.globalList[viewModel.index!!].isNew = false
        if (activity?.isInternetAvailable()!!) {
            viewModel.getComicPost(viewModel.number)
        } else {
            if (viewModel.isInCache(viewModel.number)) {
                viewModel.getComicPostCache(viewModel.number)
                subscribeToComicPostCache()
            }else{
                showToast("There is no internet connection. This comic is not in your cached favorites. Please check your connection!")
            }
        }

        val heart = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_48, null)
        val emptyHeart =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_border_48, null)
        if (viewModel.isInCache(viewModel.number)) heartHolder.setImageDrawable(heart)
        observeComicPostResponse()
        observeIsRead()
        subscribeToFinishedBitmap()

        heartHolder.setOnClickListener {
            if (requireContext().isInternetAvailable()){
                if (cachedPostIsInitialized) {
                    if (!viewModel.isInCache(viewModel.number)) {
                        GlobalList.globalList[viewModel.index!!].isFavourite = true
                        viewModel.saveComicPostCache(viewModel.cachedPost!!)
                        viewModel.saveComicListItem(viewModel.comicListItem!!)
                        heartHolder.setImageDrawable(heart)
                    } else {
                        GlobalList.globalList[viewModel.index!!].isFavourite = false
                        heartHolder.setImageDrawable(emptyHeart)
                        viewModel.deleteComicPostCacheById(viewModel.number)
                        viewModel.deleteComicListItemById(viewModel.number)
                    }
                }
            }else{
                showToast("You can only alter your favorites when there is an internet connection. Please check your connection!")
            }
        }

        explBtn.setOnClickListener {
            if (activity?.isInternetAvailable()!!) {
                val id = viewModel.number
                val title = viewModel.comicListItem!!.title
                val action =
                    DetailFragmentDirections.actionDetailFragmentToExplanationFragment(id, title)
                Navigation.findNavController(it).navigate(action)

            } else {
                showToast("You cannot see explanations without internet-connection. Please check your connection!")
            }
        }

        return binding.root
    }

    private fun observeComicPostResponse() {
        viewModel.response.observe(viewLifecycleOwner, {

            if (it.isSuccessful) {
                titleHolder.text = it.body()?.title

                Glide
                    .with(this)
                    .asBitmap()
                    .load(it.body()?.img)
                    .into(object : SimpleTarget<Bitmap?>(800, 800) {

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            (imageHolder as SubsamplingScaleImageView).setImage(
                                ImageSource.cachedBitmap(
                                    resource
                                )
                            )
                        }
                    })

                it.body()?.let { post ->
                    viewModel.createBitmap(post.img)
                    viewModel.postFromInternet = post
                    altHolder.text = post.alt
                }
                progBarHolder.visibility = View.GONE
            }
        })
    }


    private fun observeIsRead() {
        viewModel.isReadList.observe(viewLifecycleOwner) {
            for (item in it) {
                val comicListIt = GlobalList.globalList.find { searchItem ->
                    item.id == searchItem.id
                }
                comicListIt?.isRead = true

            }
        }
    }

    private fun cancelNotification() {
        GlobalList.globalList[viewModel.index!!].isNew = false
        val item = GlobalList.globalList.find { it.isNew }
        //but only if there are no unseen items left
        if (item == null) {
            val notificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Constants.NOTIFICATION_ID)
        }
    }

    private fun subscribeToComicPostCache() {
        viewModel.comicPostCache.observe(viewLifecycleOwner) {

            titleHolder.text = it.title
            val srcBmp = it.imgBitMap!!
            val dstBmp = Bitmap.createScaledBitmap(
                srcBmp,
                srcBmp.width * 3,
                srcBmp.height * 3,
                true
            )
            (imageHolder as SubsamplingScaleImageView).setImage(ImageSource.bitmap(dstBmp))
            altHolder.text = it.alt
            progBarHolder.visibility = View.GONE
        }
    }

    private fun subscribeToFinishedBitmap() {
        viewModel.bitmap.observe(viewLifecycleOwner) {
            viewModel.cachedPost = ComicPostCache(
                viewModel.postFromInternet!!.month,
                viewModel.postFromInternet!!.num,
                viewModel.postFromInternet!!.link,
                viewModel.postFromInternet!!.year,
                viewModel.postFromInternet!!.news,
                viewModel.postFromInternet!!.safe_title,
                viewModel.postFromInternet!!.transcript,
                viewModel.postFromInternet!!.alt,
                viewModel.postFromInternet!!.img,
                viewModel.postFromInternet!!.title,
                viewModel.postFromInternet!!.day,
                it,
            )
            cachedPostIsInitialized = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(), message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        val temp = activity as AppCompatActivity
        temp.supportActionBar?.hide()
    }
}