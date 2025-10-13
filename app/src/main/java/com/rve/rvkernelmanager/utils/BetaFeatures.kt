/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import com.rve.rvkernelmanager.BuildConfig

object BetaFeatures {
    val isBetaFeaturesEnabled: Boolean
        get() = BuildConfig.ENABLE_BETA_FEATURES
}
