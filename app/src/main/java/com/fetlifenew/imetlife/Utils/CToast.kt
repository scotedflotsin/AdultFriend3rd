package com.fetlifenew.imetlife.Utils

import android.content.Context
import android.widget.Toast

class CToast {
    fun showToast(context:Context,message:String){
        Toast.makeText(context,message.toString(),Toast.LENGTH_SHORT).show()
    }
}