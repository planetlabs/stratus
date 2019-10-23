#!/bin/bash

# This script sets up locust inside a virtualenv.
# - If run inside a virtualenv, it should just use that.
# - If it finds a virtualenv at VIRTUALENV_PATH, it should use that.
#   (This allows reuse of a past execution to set everything up)
# - Otherwise it should make a virtualenv, if the utility is installed.
# - Otherwise if run as root, it should install virtualenv/python
#   and then make the virtualenv and install locust inside.

# Globals specifying where to find various commands
PYTHON=python
VIRTUALENV=virtualenv
declare -a VIRTUALENV_FLAGS
VIRTUALENV_FLAGS=()
PIP=pip
LOCUST=locust

# Where we look for, and/or create, the virtualenv to run locust in
VIRTUALENV_PATH="$HOME/locust_env"

# General Notes:
# - Our general goal is to get a python3 virtualenv.
# - The more recent python3 we can get, the newer code we can support, and
#   the more features we can take advantage of in our own new code.
# - We need a python3 binary at the system level to have a python3 virtualenv.
# - It doesn't really matter what version of python the system is using to run
#   virtualenv itself, but it has to be a working version of virtualenv.
# - If there is no virtualenv system package, we have to get pip, then use
#   that to install virtualenv. We have *no* other use for system pip.
#   If there is a working virtualenv system package, we can skip pip, since
#   we're only going to use the pip inside the virtualenv anyway.
# - If possible, we really want pip install to grab manylinux binary wheels.
#   This makes it unnecessary to compile anything, faster and less fragile.
# - Many pip packages and some virtualenv packages drag in a gcc build
#   environment. 
#   This isn't necessarily a big problem.
#   If we need pip installs of our app in our virtualenv to build C extensions,
#   this is fine, but we'd probably better add the build deps explicitly.
# - If we didn't install python2 on the system (and why would we), many distro
#   virtualenv packages will require us to speify --python python3 when running
#   virtualenv

die () {
    echo "Error: $@"
    exit 1
}

think () {
    echo ""
    echo -e "\e[1;32m$@\e[0m"
    echo ""
}

# We have to sniff the distro to figure out what packages to install and
# what other workarounds to apply. These are specific to distro releases.
# Just knowing whether we have e.g. yum or apt-get is not enough.

get_distro_name () {
    if [ -f /etc/arch-release ]; then
        echo "Arch"
    elif [ -f /etc/system-release ]; then
        cut -d" " -f1 < /etc/system-release
    elif [ -f /etc/os-release ]; then
        grep ^NAME /etc/os-release | cut -d'"' -f2 | cut -d" " -f1
    fi
}

get_distro_version () {
    distro_name=$(get_distro_name)
    case "${distro_name}" in
        "Arch")
            echo "NA"
            ;;
        "Alpine")
            # E.g. 3.6.2
            grep ^VERSION_ID /etc/os-release | cut -d'=' -f2
            ;;
        "Amazon")
            # E.g. 2017.03
            cut -d" " -f5 < /etc/system-release
            ;;
        "CentOS")
            # E.g. 6, 7
            cut -d" " -f4 < /etc/system-release | cut -d"." -f1
            ;;
        "Fedora")
            # E.g. 26, 22
            cut -d" " -f3 < /etc/system-release
            ;;
        "Debian")
            # E.g. 9.1 - major changes only every few years
            cat /etc/debian_version | cut -d"." -f1
            ;;
        "Ubuntu")
            # E.g. 14.04, 14.10 - biannual
            grep ^VERSION_ID /etc/os-release | cut -d'"' -f2
            ;;
    esac
}

install_system_packages () {

    if [ $(id -u) != "0" ]; then
        echo "To install system packages, this script would need root privs"
        exit 1
    fi
    distro_name=$(get_distro_name)
    case "$distro_name" in
        "Alpine")
            install_system_packages_alpine
            ;;
        "Amazon")
            install_system_packages_amazon
            ;;
        "CentOS")
            install_system_packages_centos
            ;;
        "Fedora")
            install_system_packages_fedora
            ;;
        "Debian")
            install_system_packages_debian
            ;;
        "Ubuntu")
            install_system_packages_ubuntu
            ;;
        *)
            echo "Can't identify distribution $distro_name"
            exit 1
            ;;
    esac
}

install_system_packages_alpine () {
    # Notes on Alpine:
    # - generally used for docker containers, which makes virtualenv isolation
    #   less relevant for this case
    # - manylinux binary wheels don't come in, which is a bummer; 
    #   may work around with --system-site-packages for libs with C exts
    # - this only works if you are NOT using --upgrade to enforce
    #   requirements.txt versions strictly... be warned
    # - py-virtualenv uses python2, which is fine
    # - if you install locust directly at system level

    declare -a system_packages

    distro_version=$(get_distro_version)
    case "$distro_version" in
        *)
            system_packages=(python3 py-virtualenv py3-gevent py3-zmq py3-lxml)
            PYTHON=python3.6
            PIP=pip3
            VIRTUALENV=virtualenv
            ;;
    esac

    apk add "${system_packages[@]}"

    VIRTUALENV_FLAGS=(--system-site-packages)

    # maybe add py3-gevent and use --system-site-packages
    # since it's in a container anyway
    # echo "manylinux1_compatible = True" >> _manylinux.py
}

install_system_packages_amazon () {
    # - Not sniffing versions because I can't reliably get at them to test
    # - No preinstall necessary for python3.5, the amzn repos have 3.5 not 3.6
    #   not sure whether IUS would even work

    # Package names we want to install, based on distro sniffing
    declare -a system_packages

    # - You might have the idea of bootstrapping by using a system package
    #   containing virtualenv, as on CentOS. 
    #   But Amazon Linux's package of virtualenv creates virtualenvs that use
    #   an insanely old pip-6.0.8. This prevents it from grabbing manylinux
    #   binary wheels, so packages like greenlet have to be compiled at pip
    #   install time, which means you either need a gcc build environment,
    #   or you need to upgrade pip inside every virtualenv.
    #   That's not really worth it since we can grab the pip package and 
    #   install a proper virtualenv from PyPI anyway.
    #   Not even CentOS needs this babying.
    # - Like the CentOS package, it insists on naming itself virtualenv-3.5
    #
    # So it's not really worth it. Still, this is how you'd do it:
    #
    # system_packages=(python35 python35-virtualenv)
    # VIRTUALENV=virtualenv-3.5

    distro_version=$(get_distro_version)
    case "$distro_version" in
        "")
            ;;
        *)
            system_packages=(python35 python35-pip)
            VIRTUALENV=virtualenv
            PYTHON=python3.5
            ;;
    esac

    yum install -y "${system_packages[@]}"
    think "Installing virtualenv from PyPI"
    pip-3.5 install virtualenv
}

install_system_packages_centos () {
    # Notes on CentOS:
    # - CentOS 6 defaults to Python 2.6, CentOS 7 to 2.7
    # - 2.6 is both formally and practically deprecated,
    #   so we can't use that for any modern app,
    #   and we need to install some other python
    #   for use inside the virtualenv.
    # - CentOS 6 also spews lots of ugly warning messages for this reason
    # - No version of Python 3 is in the main repos,
    #   you have to use third-party stuff like EPEL or IUS
    #   EPEL only gives Python 3.4
    # - If you try to install a package by name before
    #   e.g. installing EPEL, it just fails silently
    # - We would like to use a virtualenv package.
    #   There is one named python-virtualenv.
    #   However, it provides a broken version of virtualenv
    #   (1.10.1) with a bug that prevents using EPEL python3.4
    #   in a virtualenv... pointless. The upstream bug was
    #   fixed years ago. Did anyone even test this?
    # - So we grab a pip package instead,
    #   and use that to install a working virtualenv utility
    # - This is pip 7.1.0, which is years behind and may
    #   need to be updated to deal with SSL shenanigans.

    declare -a system_packages
    # Array of packages needed before system packages, e.g. EPEL
    declare -a preinstall

    distro_version=$(get_distro_version)
    case "$distro_version" in
        # "6")
        #     # CentOS 6 with IUS
        #     preinstall=("https://centos6.iuscommunity.org/ius-release.rpm")
        #     system_packages=(python36u python36u-pip)
        #     PYTHON=python3.6
        #     ;;
        # "7")
        #     # CentOS 7 with IUS
        #     preinstall=("https://centos7.iuscommunity.org/ius-release.rpm")
        #     system_packages=(python36u python36u-pip)
        #     PYTHON=python3.6
        #     ;;
        *)
            # Based on testing with CentOS 6 or 7 with EPEL
            preinstall=(epel-release)
            system_packages=(python34 python-pip)
            PYTHON=python3.4
            ;;
    esac

    if [ ${#preinstall[@]} -ne 0 ]; then
        yum install -y "${preinstall[@]}"
    fi

    yum install -y "${system_packages[@]}"

    # The following assumes we aren't using a virtualenv package,
    # therefore we are using pip to install virtualenv.

    # Upgrade pip
    think "Upgrading pip from PyPI"
    pip install --upgrade pip

    # Install proper virtualenv from PyPI
    think "Installing virtualenv from PyPI"
    pip install virtualenv
}

install_system_packages_fedora () {
    # Notes on Fedora:
    # - Fedora insists on naming python3-virtualenv as "virtualenv-3"
    # - Fedora wants you to use dnf, not yum

    # Accommodate install commands based on version
    declare -a INSTALL
    INSTALL=(dnf install -y)

    distro_version=$(get_distro_version)
    case "$distro_version" in
        "21")
            system_packages=(python3 python3-virtualenv)
            PYTHON=python3.4
            VIRTUALENV=virtualenv-3.4
            INSTALL=(yum install -y)

            # Fails building greenlet, cause not immediately clear
            die "Unsupported distro version"
            ;;
        "22")
            system_packages=(python3 python3-virtualenv)
            PYTHON=python3.4
            VIRTUALENV=virtualenv-3.4
            ;;
        "23")
            system_packages=(python35 python3-virtualenv)
            PYTHON=python3.5
            VIRTUALENV=virtualenv-3.4
            ;;
        # 24, 25, 26
        *)
            system_packages=(python36 python3-virtualenv)
            PYTHON=python3.6
            VIRTUALENV=virtualenv-3
            ;;
    esac

    "${INSTALL[@]}" "${system_packages[@]}"
}


install_system_packages_debian () {

    # Notes on Debian:
    # - Don't need pip system packages since the virtualenv package is fine.
    # - If you use python3-pip etc., these packages drag in build deps,
    #   you can likely use --no-install-recommends if you want,
    #   but this behavior is probably fine if you're relying on system pip
    #   or you want virtualenv pip installs to build C extensions.
    #   the advent of manylinux binary wheels means this is rarely necessary.
    # - Jessie and below ship an ancient pip in virtualenvs, so need either 
    #   a newer virtualenv or to upgrade pip in every virtualenv,
    #   or (lame idea) have gcc instead of using manylinux binary wheels. :/

    declare -a system_packages
    debian_version=$(get_distro_version)
    debian_pip=pip
    case "$debian_version" in
        # Wheezy
        "7")
            # - Wheezy repos only have 3.2, which isn't too usable
            # - python-virtualenv pip has version 1.1 (!!!)

            # system_packages=(python3.2 python-virtualenv)
            system_packages=(python3.2 python3-pip)
            PYTHON=python3.2
            debian_pip=pip-3.2
            need_virtualenv=yep

            # Locust doesn't support Python < 3.3, will fail at pip install.
            die "need python at least 3.3 to continue"
            ;;
        # Jessie
        "8")
            # - virtualenv package's pip has version 1.5.6 (!!)

            # system_packages=(python3.4 virtualenv)
            system_packages=(python3.4 python3-pip)
            PYTHON=python3.4
            debian_pip=pip3
            need_virtualenv=yep
            ;;
        # Stretch
        "9")
            system_packages=(python3.5 virtualenv)
            PYTHON=python3.5
            ;;
        # Newer than 9, maybe? Or just no version number, as is common...
        # Buster and Sid choked in setup.py bdist_wheel for pyzmq unless it had
        # gcc stuff. that build slows down the install quite a bit.
        # It's worth revisiting as future versions are special cased above.
        *)
            system_packages=(python3.6 virtualenv build-essential python3.6-dev)
            PYTHON=python3.6
            ;;
    esac

    apt-get install -y "${system_packages[@]}"

    # The following assumes we aren't using a virtualenv package,
    # therefore we are using pip to install virtualenv.
    if [ -n "$need_virtualenv" ]; then
        # Install proper virtualenv from PyPI
        $debian_pip install virtualenv
    fi
}

install_system_packages_ubuntu () {
    # Notes on Ubuntu:
    # - See notes on Debian.
    # - Indicated python versions here should be the highest available.
    #   Other versions are typically available via PPAs.
    # - can also get any version of python from fkrull/deadsnakes PPA,
    #   which has been reliable for a while
    # - When using ubuntu versions that build things, there is an ignorable
    #   error during the pip install:
    #   "Error compiling '/root/locust_env/build/Jinja2/jinja2/asyncfilters.py"

    declare -a system_packages
    distro_version=$(get_distro_version)
    case "$distro_version" in
        "14.04")
            # - no 'virtualenv' package.
            # - python-virtualenv uses python2.7, but that doesn't matter.
            # - python-virtualenv recommends build deps,
            #   and they are needed to compile stuff for pyzmq, gevent,
            #   along with the dev headers for the python we're using
            system_packages=(python3.4 python-virtualenv build-essential python3.4-dev libxml2-dev libxslt1-dev libz-dev)
            PYTHON=python3.4
            ;;

        # Skipping 14.10 since what's on dockerhub with
        # that tag was 14.04.5 in /etc/lsb-release
        # and complained about utopic repositories.

        # 15.04, 15.10, 16.04 don't grab the binary wheels so they need
        # build environments
        "15.04")
            system_packages=(python3.4 virtualenv build-essential python3.4-dev libxml2-dev libxslt-dev libz-dev)
            PYTHON=python3.4
            ;;
        "15.10")
            # lxml <- libxml2-dev libxslt-dev libz-dev
            system_packages=(python3.5 virtualenv build-essential python3.5-dev libxml2-dev libxslt-dev libz-dev)
            VIRTUALENV_FLAGS=(--system-site-packages)
            PYTHON=python3.5
            ;;
        "16.04")
            system_packages=(python3.5 virtualenv build-essential python3.5-dev)
            PYTHON=python3.5
            ;;

        # 16.10, 17.04, 17.10...
        *)
            system_packages=(python3.6 virtualenv build-essential python3.6-dev libxml2-dev libxslt-dev libz-dev)
            PYTHON=python3.6
            ;;
    esac

    # If we're depending on deadsnakes ppa, it's like this first:
    # add-apt-repository ppa:fkrull/deadsnakes
    # apt-get update

    apt-get install -y "${system_packages[@]}"
}

in_virtualenv () {
    if [[ "$VIRTUAL_ENV" != "" ]]; then
        echo "Already in virtualenv $VIRTUAL_ENV"
        return 0
    fi
    return 1
}

virtualenv_exists () {
    if [[ -d "$VIRTUALENV_PATH" ]]; then
        echo "Have virtualenv at $VIRTUALENV_PATH"
        return 0
    fi
    return 1
}

has_python () {
    if [ ! -n "$(command -v $PYTHON)" ]; then
        return 1
    fi 
    if $PYTHON --version > /dev/null ; then
        return 0
    fi
    return 1
}

has_virtualenv () {
    if [ ! -n "$(command -v $VIRTUALENV)" ]; then
        return 1
    fi 
    if $VIRTUALENV --version ; then
        return 0
    fi
    return 1
}

has_pip () {
    if [ ! -n "$(command -v $PIP)" ]; then
        return 1
    fi 
    if $PIP --version ; then
        return 0
    fi
    return 1
}

install_locust () {
    think "Installing locust in virtualenv $VIRTUALENV_PATH"
    # Install dependencies listed declaratively in requirements.txt
    # $PIP install --upgrade -r requirements.txt
    $PIP install -r requirements.txt
}

has_locust () {
    $LOCUST --version 
}

# If we're working in a virtualenv right now
if in_virtualenv ; then

    think "Already in virtualenv $VIRTUAL_ENV"

    # We won't use the virtualenv utility
    VIRTUALENV=false

    # Rely on virtualenv having set $PATH to find us the right executables
    # overriding any system-specific cruft about command names
    # Using the virtualenv's pip is enough to install packages into it
    PYTHON=python
    PIP=pip
    LOCUST=locust

# If we can find a virtualenv at expected path
elif virtualenv_exists ; then

    think "Found existing virtualenv at $VIRTUALENV_PATH"

    # We won't use the virtualenv utility
    VIRTUALENV=false

    # Look for utilities relative to that
    # overriding any system-specific cruft about command names
    PYTHON="$VIRTUALENV_PATH/bin/python"
    PIP="$VIRTUALENV_PATH/bin/pip"
    LOCUST="$VIRTUALENV_PATH/bin/locust"

# If we don't already have a virtualenv to use, we need to make one
else

    # If we already have Virtualenv named as such, we'll try just using that
    # If we don't have Virtualenv, try to get it first
    if ! has_virtualenv ; then

        think "How can I get virtualenv?"

        # If we already have pip named as such, try just using that
        if has_pip ; then
            think "Getting virtualenv using existing system pip"
            pip install virtualenv
        fi

        # If we still don't have it, try system packages
        if ! has_virtualenv ; then
            think "Installing system packages to get virtualenv"
            install_system_packages
        fi

        # Sanity check and fail
        has_virtualenv || die "needed but could not find or install virtualenv"
        think "Can run virtualenv"

    fi

    think "Creating virtualenv with $PYTHON at $VIRTUALENV_PATH"
    $VIRTUALENV "${VIRTUALENV_FLAGS[@]}" --python "$PYTHON" "$VIRTUALENV_PATH"
    virtualenv_exists || die "needed but failed to create virtualenv at $VIRTUALENV_PATH"
    think "Virtualenv created"

    PYTHON="$VIRTUALENV_PATH/bin/python"
    PIP="$VIRTUALENV_PATH/bin/pip"
    LOCUST="$VIRTUALENV_PATH/bin/locust"

fi

# By now we *should* have $PYTHON set up properly pointing inside a virtualenv;
# if we don't, we can't continue.
# Generate friendly error messages if anything big is still missing
has_python || die "expected Python as $PYTHON but it is missing"
think "Can run python"

has_pip || die "could not run pip at $PIP"
think "Can run pip"

install_locust
has_locust || die "failed to install locust ${LOCUST} at ${VIRTUALENV_PATH}"
think "Can run locust inside virtualenv at $VIRTUALENV_PATH"
