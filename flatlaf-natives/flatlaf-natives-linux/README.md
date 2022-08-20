FlatLaf Linux Native Library
============================

This sub-project contains the source code for the FlatLaf Linux native library.

The native library can be built only on Linux and requires a C++ compiler.

To be able to build FlatLaf on any platform, and without C++ compiler, the
pre-built native library is checked into Git at
[flatlaf-core/src/main/resources/com/formdev/flatlaf/natives/](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-core/src/main/resources/com/formdev/flatlaf/natives).

The native library was built on a GitHub server with the help of GitHub Actions.
See:
[Native Libraries](https://github.com/JFormDesigner/FlatLaf/actions/workflows/natives.yml)
workflow. Then the produced Artifacts ZIP was downloaded and the native library
checked into Git.


## Development

To build the library on Linux, some packages needs to be installed.


### Ubuntu

`build-essential` contains GCC and development tools. `libxt-dev` contains the
X11 toolkit development headers.

~~~
sudo apt update
sudo apt install build-essential libxt-dev
~~~


### CentOS

~~~
sudo yum install libXt-devel
~~~
