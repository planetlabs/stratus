#!/bin/sh
# Do package system initialization for your distro, e.g. update package index 

# Alpine
if [ -n "$(command -v apk)" ]; then
    apk update && apk upgrade && apk add bash

# Fedora
elif [ -n "$(command -v dnf)" ]; then
    # deltarpm=false is to avoid lots of spam about
    # "cannot reconstruct rpm from disk files"
    # in e.g. Fedora 24
    dnf update -y -v --setopt=deltarpm=false

# CentOS, Amazon
elif [ -n "$(command -v yum)" ]; then
    yum update -y -v

# Debian, Ubuntu
elif [ -n "$(command -v apt-get)" ]; then
    apt-get update
fi
