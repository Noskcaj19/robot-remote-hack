#!/bin/bash

#export HALSIM_EXTENSIONS=/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/expandedArchives/halsim_gui-2021.1.2-osxx86-64.zip_cebe157c0370b0beb19e3bf47444ccd5/osx/x86-64/shared/libhalsim_gui.dylib:/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/expandedArchives/halsim_ds_socket-2021.1.2-osxx86-64.zip_74f940fde2ab76e055beee74e438787d/osx/x86-64/shared/libhalsim_ds_socket.dylib
export LD_LIBRARY_PATH=/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/jniExtractDir
export DYLD_FALLBACK_LIBRARY_PATH=/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/jniExtractDir
export DYLD_LIBRARY_PATH=/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/jniExtractDir
"/usr/local/Cellar/openjdk@11/11.0.9/libexec/openjdk.jdk/Contents/Home/bin/java" -Djava.library.path="/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/tmp/jniExtractDir" -XstartOnFirstThread -jar "/Users/jack/Documents/Development/MARS/RemoteControl/SimpleRemoteControl/build/libs/SimpleRemoteControl.jar"
