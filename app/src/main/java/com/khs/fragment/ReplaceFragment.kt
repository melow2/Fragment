package com.khs.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.khs.fragment.databinding.FragmentReplaceBinding
import com.khs.fragment.databinding.FragmentTestBinding
import timber.log.Timber
import java.lang.ClassCastException


class ReplaceFragment : BaseFragment<FragmentReplaceBinding>() {

    private var param1: String? = null
    private var param2: String? = null

    private var listener:FragmentEventListener?=null

    interface FragmentEventListener{
        fun onTestEvent(message:String)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReplaceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        // The first to be fired!
        // Called when the fragment instance is associated with an Activity
        // This does not mean the Activity is fully intialized.
        Timber.d("onAttach")
        super.onAttach(context)
        if(context is FragmentEventListener){
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // is called when fragment should create its view object hierarchy, either dynamically or via XML layout inflation.
        bindView(inflater, container!!, R.layout.fragment_replace)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Called right after onCreateView is called.
        // Note: onViewCreated is only called if the view returned from onCreateView() is non-null
        // Any view setup should occur, for example: view lookups and attaching view listeners.
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // this method is called after the parent Activity's onCreate() method has completed.
        super.onActivityCreated(savedInstanceState)
    }


    override fun onDestroyView() {
        // The fragment is about to be destroyed - cleanup
        Timber.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        // is called when the fragment is no longer connected to the Activity
        // get rid of referencees from onAttach() - null them out - to prevent memory leaks
        Timber.d("onDetach")
        super.onDetach()
        // clean up!
        this.listener = null

    }

    fun testMessage(msg:String){
        mBinding?.tvTest?.text = msg
        listener?.onTestEvent("message")
    }

}