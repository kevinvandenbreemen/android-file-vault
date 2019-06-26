package com.vandenbreemen.secretcamera.di.activity

import com.vandenbreemen.secretcamera.Gallery
import dagger.Module
import dagger.Provides

@Module
class GalleryActivityModule(private val gallery: Gallery) {

    @Provides
    fun provide(): Gallery = gallery

}