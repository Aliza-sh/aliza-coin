package com.aliza.alizacoin.base

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun Context.showSnacbar(view: View, str: String):Snackbar {
    return Snackbar.make(view, str, Snackbar.LENGTH_INDEFINITE)
}