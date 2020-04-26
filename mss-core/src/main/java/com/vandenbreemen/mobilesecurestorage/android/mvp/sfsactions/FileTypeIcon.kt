package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions

import android.graphics.drawable.Drawable

/**
 *
 * @author kevin
 */
interface FileTypeIcon {

    fun getDrawable(): Drawable

}

enum class CoreFileTypeIcons : FileTypeIcon {

    UNKNOWN
    ;

    override fun getDrawable(): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}