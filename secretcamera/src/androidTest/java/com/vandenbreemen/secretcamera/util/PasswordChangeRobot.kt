package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.vandenbreemen.secretcamera.R

class PasswordChangeRobot {
    fun currentPassword(currentPass: String) {
        writeTo(R.id.currentPassword, currentPass)
    }

    fun newPassword(newPass: String) {
        writeTo(R.id.newPassword, newPass)
    }

    fun reenterNewPassword(reEnteredPass: String) {
        writeTo(R.id.reEnterNewPassword, reEnteredPass)
    }

    fun clickOK() {
        clickOn(R.id.ok)
    }


}
