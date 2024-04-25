package com.aliza.alizacoin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.aliza.alizacoin.R

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null)
            throw IllegalArgumentException("binding cannot be null")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        return if (enter) {
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right)
        }
    }

    //Method to get the name of the caller's class.
    protected open fun getCallerClassName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        var callerClassName = ""
        for (stackTraceElement in stackTrace) {
            if (stackTraceElement.className != this.javaClass.name) {
                callerClassName = stackTraceElement.className
                break
            }
        }
        return callerClassName
    }
    //Implement the following method in the caller class to return the name.
    /*
    override fun getCallerClassName(): String {
        return "CallerActivity"

    }
    */

}