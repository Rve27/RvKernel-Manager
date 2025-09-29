#!/bin/sh

setProfile() {
    # CPU
    # Only add this if your CPU only has single cluster (0-3)
    echo performance > /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
    # Add this if your CPU has a big cluster (4-7)
    # echo performance > /sys/devices/system/cpu/cpufreq/policy4/scaling_governor
    # Use this if your big cluster is on cpu6
    # echo performance > /sys/devices/system/cpu/cpufreq/policy6/scaling_governor
    # Add this if your CPU has a prime cluster (cpu7)
    # echo performance > /sys/devices/system/cpu/cpufreq/policy7/scaling_governor
    
    # GPU
    echo performance > /sys/class/kgsl/kgsl-3d0/devfreq/governor
    echo 0 > /sys/class/kgsl/kgsl-3d0/default_pwrlevel
    echo 0 > /sys/class/kgsl/kgsl-3d0/throttling
    # Add this if your kernel has Adreno Boost feature
    # echo 3 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost
    
    # Thermal profiles
    # Add this if the kernel/ROM you are using has a Thermal profiles feature
    chmod 664 /sys/class/thermal/thermal_message/sconfig
    echo 10 > /sys/class/thermal/thermal_message/sconfig
    chmod 444 /sys/class/thermal/thermal_message/sconfig
    
    # You can also add other things below here, such as VM, ZRAM, and other kernel parameters.
}

{ setProfile; echo "[$(date '+%Y-%m-%d %H:%M:%S')] Performance mode applied"; } 2>&1 | tee -a /sdcard/RvKernel-Manager/kernel-profile/kernel-profile.log