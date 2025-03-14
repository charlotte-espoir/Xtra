package com.github.andreyasadchy.xtra.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.databinding.DialogUserResultBinding
import com.github.andreyasadchy.xtra.databinding.FragmentSearchBinding
import com.github.andreyasadchy.xtra.ui.Utils
import com.github.andreyasadchy.xtra.ui.channel.ChannelPagerFragmentDirections
import com.github.andreyasadchy.xtra.ui.common.BaseNetworkFragment
import com.github.andreyasadchy.xtra.ui.main.IntegrityDialog
import com.github.andreyasadchy.xtra.ui.main.MainActivity
import com.github.andreyasadchy.xtra.util.C
import com.github.andreyasadchy.xtra.util.TwitchApiHelper
import com.github.andreyasadchy.xtra.util.prefs
import com.github.andreyasadchy.xtra.util.reduceDragSensitivity
import com.github.andreyasadchy.xtra.util.showKeyboard
import com.github.andreyasadchy.xtra.util.visible
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SearchPagerFragment : BaseNetworkFragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchPagerViewModel by viewModels()
    private var firstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstLaunch = savedInstanceState == null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    val currentFragment: Fragment?
        get() = childFragmentManager.findFragmentByTag("f${binding.pagerLayout.viewPager.currentItem}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.integrity.observe(viewLifecycleOwner) {
            if (requireContext().prefs().getBoolean(C.ENABLE_INTEGRITY, false) && requireContext().prefs().getBoolean(C.USE_WEBVIEW_INTEGRITY, true)) {
                IntegrityDialog.show(childFragmentManager)
            }
        }
        val activity = requireActivity() as MainActivity
        with(binding.pagerLayout) {
            val adapter = SearchPagerAdapter(this@SearchPagerFragment)
            viewPager.adapter = adapter
            if (firstLaunch) {
                viewPager.setCurrentItem(2, false)
                firstLaunch = false
            }
            viewPager.offscreenPageLimit = adapter.itemCount
            viewPager.reduceDragSensitivity()
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.videos)
                    1 -> getString(R.string.streams)
                    2 -> getString(R.string.channels)
                    else -> getString(R.string.games)
                }
            }.attach()
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (currentFragment as? Searchable)?.search(binding.search.query.toString())
                }
            })
        }
        with(binding) {
            menu.visible()
            menu.setOnClickListener {
                val binding = DialogUserResultBinding.inflate(layoutInflater)
                AlertDialog.Builder(requireContext()).apply {
                    setView(binding.root)
                    setNegativeButton(getString(android.R.string.cancel), null)
                    setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        val result = binding.editText.text?.toString()
                        val checkedId = if (binding.radioButton.isChecked) 0 else 1
                        if (!result.isNullOrBlank()) {
                            userResult = Pair(checkedId, result)
                            viewModel.loadUserResult(TwitchApiHelper.getGQLHeaders(requireContext()), checkedId, result)
                            viewModel.userResult.observe(viewLifecycleOwner) {
                                if (it != null) {
                                    if (!it.first.isNullOrBlank()) {
                                        AlertDialog.Builder(requireContext()).apply {
                                            setTitle(it.first)
                                            setMessage(it.second)
                                            setNegativeButton(getString(android.R.string.cancel), null)
                                            setPositiveButton(getString(R.string.view_profile)) { _, _ -> viewUserResult() }
                                        }.show()
                                    } else {
                                        viewUserResult()
                                    }
                                }
                            }
                        }
                    }
                    setNeutralButton(getString(R.string.view_profile)) { _, _ ->
                        val result = binding.editText.text?.toString()
                        val checkedId = if (binding.radioButton.isChecked) 0 else 1
                        if (!result.isNullOrBlank()) {
                            userResult = Pair(checkedId, result)
                            viewUserResult()
                        }
                    }
                }.show()
            }
            toolbar.apply {
                navigationIcon = Utils.getNavigationIcon(activity)
                setNavigationOnClickListener { activity.popFragment() }
            }
            search.showKeyboard()
        }
    }

    override fun initialize() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            private var job: Job? = null

            override fun onQueryTextSubmit(query: String): Boolean {
                (currentFragment as? Searchable)?.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                job?.cancel()
                if (newText.isNotEmpty()) {
                    job = lifecycleScope.launchWhenResumed {
                        delay(750)
                        (currentFragment as? Searchable)?.search(newText)
                    }
                } else {
                    (currentFragment as? Searchable)?.search(newText) //might be null on rotation, so as?
                }
                return false
            }
        })
    }

    private var userResult: Pair<Int?, String?>? = null

    private fun viewUserResult() {
        userResult?.let {
            when (it.first) {
                0 -> findNavController().navigate(ChannelPagerFragmentDirections.actionGlobalChannelPagerFragment(channelId = it.second))
                1 -> findNavController().navigate(ChannelPagerFragmentDirections.actionGlobalChannelPagerFragment(channelLogin = it.second))
                else -> {}
            }
        }
    }

    override fun onNetworkRestored() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
