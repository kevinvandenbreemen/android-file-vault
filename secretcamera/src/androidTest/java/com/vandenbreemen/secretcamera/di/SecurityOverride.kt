package com.vandenbreemen.secretcamera.di

fun turnOffSecureActivities() {
    ActivitySecurity.setPreparations { }
}