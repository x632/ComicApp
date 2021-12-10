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
import com.poema.comicapp.databinding.FragmentDetailBinding
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.GlobalList.globalList


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
    private var internetPostInitialized = false
    private var savingIsDone = true
    private var letBackButtonPass = false

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

        if (globalList[viewModel.index!!].isNew) {
            cancelNotification()
        }

        globalList[viewModel.index!!].isNew = false

        subscribeToSaveIsDone()
        if (viewModel.isInCache(viewModel.number)) {
            viewModel.comicListItem = globalList[viewModel.index!!]
            titleHolder.text = globalList[viewModel.index!!].title
            val srcBmp = globalList[viewModel.index!!].bitmap!!
            val dstBmp = Bitmap.createScaledBitmap(
                srcBmp,
                srcBmp.width * 3,
                srcBmp.height * 3,
                true
            )
            (imageHolder as SubsamplingScaleImageView).setImage(ImageSource.bitmap(dstBmp))
            altHolder.text = globalList[viewModel.index!!].alt
            progBarHolder.visibility = View.GONE
            cachedPostIsInitialized = true
        } else {
            if (activity?.isInternetAvailable()!!) {
                viewModel.getComicPost(viewModel.number)
            }else{
                showToast("Please check your internet connection! This comic has not yet been cached. ")
            }
        }

        val heart = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_48, null)
        val emptyHeart =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_border_48, null)
        if (viewModel.comicListItem!!.isFavourite) heartHolder.setImageDrawable(heart)
        else {
            heartHolder.setImageDrawable(emptyHeart)
        }
        observeComicPostResponse()
        subscribeToFinishedBitmap()

        heartHolder.setOnClickListener {
            if (requireContext().isInternetAvailable()) {
                //if(savingIsDone) {
                    if (cachedPostIsInitialized || internetPostInitialized) {
                        if (!globalList[viewModel.index!!].isFavourite) {
                            savingIsDone = false
                            progBarHolder.visibility = View.VISIBLE
                            heartHolder.setImageDrawable(heart)
                            globalList[viewModel.index!!].isFavourite = true
                            val item = viewModel.createItem(true)
                            viewModel.updateComicListItem(item)

                        } else {
                            savingIsDone = false
                            println("!!! BBEEEEENNN HHHEEERREEE!")
                            progBarHolder.visibility= View.VISIBLE
                            heartHolder.setImageDrawable(emptyHeart)
                            globalList[viewModel.index!!].isFavourite = false
                            val item = viewModel.createItem(false)
                            viewModel.updateComicListItem(item)
                        }
                    }
                //}
            } else {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (cachedPostIsInitialized || internetPostInitialized ||  activity?.isInternetAvailable() == false) letBackButtonPass = true
               if(savingIsDone && letBackButtonPass) {
                       Navigation.findNavController(altHolder).popBackStack()
               }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private fun subscribeToSaveIsDone(){
        viewModel.savingListItemFinished.observe(viewLifecycleOwner,{
            savingIsDone = it
            println("!!! observer värdet är: $it")
            if(it)progBarHolder.visibility = View.GONE
        })
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
                    viewModel.postDtoFromInternet = post
                    altHolder.text = post.alt
                }
                progBarHolder.visibility = View.GONE
            }
        })
    }

    private fun cancelNotification() {
        globalList[viewModel.index!!].isNew = false
        val item = globalList.find { it.isNew }
        //cancel notification, but only if there are no unseen items left
        if (item == null) {
            val notificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Constants.NOTIFICATION_ID)
        }
    }


    private fun subscribeToFinishedBitmap() {
        viewModel.bitmap.observe(viewLifecycleOwner) {

            viewModel.comicListItem = ComicListItem(
                viewModel.postDtoFromInternet!!.title,
                viewModel.postDtoFromInternet!!.num,
                globalList[viewModel.index!!].date,
                viewModel.postDtoFromInternet!!.alt,
                it,
                globalList[viewModel.index!!].isFavourite,
                globalList[viewModel.index!!].isNew,
            )
            viewModel.saveComicListItem(viewModel.comicListItem!!)
            globalList[viewModel.index!!] = viewModel.comicListItem!!
            internetPostInitialized = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(), message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        val temp = activity as AppCompatActivity
        temp.supportActionBar?.hide()
    }
}