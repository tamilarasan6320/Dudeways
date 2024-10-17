package com.gmwapp.dudeways.Fragment

import androidx.fragment.app.Fragment
import kotlinx.coroutines.Job

open class BaseFragment : Fragment() {
    var apiJob: Job? = null

    override fun onStop() {
        super.onStop()
        // Cancel any ongoing API request when the fragment is no longer visible
        apiJob?.cancel()
    }
}
