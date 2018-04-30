package com.vandenbreemen.mobilesecurestorage.patterns.mvp

import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

var closed = false

@Implements(Model::class)
class ShadowModel {

    @Implementation
    fun close() {
        closed = true
    }

}