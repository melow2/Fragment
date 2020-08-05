package com.khs.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.khs.fragment.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity(),TestFragment.FragmentEventListener {

    lateinit var mBinding: ActivityMainBinding

    companion object {
        const val FRAGMENT_TAG = "TEST_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(savedInstanceState==null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.flt_container, TestFragment.newInstance("1", "2"), FRAGMENT_TAG).commit()
        }

        mBinding.apply {
            btnTest.setOnClickListener {
                val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                fragment?.let{
                    (fragment as TestFragment).testMessage("테스트입니다.")
                }
            }
            btnReplace.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                transaction.replace(R.id.flt_container,ReplaceFragment.newInstance("1","2"))
                transaction.addToBackStack("BACK") // 스택으로 쌓아둠, 명시하지않으면 기존의 Fragment 제거.
                transaction.commit()
            }
        }
    }

    override fun onTestEvent(message: String) {
        Toast.makeText(MainActivity@this,message,Toast.LENGTH_SHORT).show()
    }

}