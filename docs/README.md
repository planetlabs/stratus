# Stratus Documentation

This module builds all of the documentation for Stratus. 

## Prerequesites:

* Ant
* Python 2
* [Sphinx](http://sphinx-doc.org/).

## Usage

Before building the docs, you must initialize the submodules using:

    % git submodule update --init

The Stratus documentation is split into the main usermanual, plus several sub modules for upstream projects. To build all the documentation included with Stratus invoke ant from this directory.

    % ant
    
Or to build a specific sub module change directory to that sub module and invoke ant.

    % cd usermanual
    % ant basic

To assemble the final documentation bundle, call the assemble task.

    % ant assemble

To test out the docs locally, including links between component manuals, use the run task. Note that this requires python to be installed.

    % ant run

## Building PDFs

The build will attempt to build PDF versions of the installation documentation
if the `pdflatex` command is available on the ``PATH``. If the command is
not available the build will skip PDF generation.

The ``pdflatex`` requires installing Latex which can be tricky depending on the
platform. On Ubuntu systems install the following packages:

    % apt-get install texlive-latex-recommended texlive-latex-extra texlive-fonts-recommended
