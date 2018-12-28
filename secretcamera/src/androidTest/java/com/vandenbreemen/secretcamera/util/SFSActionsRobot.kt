package com.vandenbreemen.secretcamera.util

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.vandenbreemen.secretcamera.R

class SFSActionsRobot {
    fun clickChangePassword(): PasswordChangeRobot {
        clickOn(R.id.changePassword)
        assertDisplayed(R.id.reEnterNewPassword)
        return PasswordChangeRobot()
    }

}
