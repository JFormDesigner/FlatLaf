FlatLaf Linux Native Library
============================

This sub-project contains the source code for the FlatLaf Linux native library.

The native library can be built only on Linux and requires a C++ compiler.

The native library is available for following CPU architectures: `x86_64` (or
`amd64`) and `arm64` (or `aarch64`).

To be able to build FlatLaf on any platform, and without C++ compiler, the
pre-built native libraries are checked into Git at
[flatlaf-core/src/main/resources/com/formdev/flatlaf/natives/](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-core/src/main/resources/com/formdev/flatlaf/natives).

The native libraries were built on a GitHub server with the help of GitHub
Actions. See:
[Native Libraries](https://github.com/JFormDesigner/FlatLaf/actions/workflows/natives.yml)
workflow. Then the produced Artifacts ZIP was downloaded and the native library
checked into Git.


## Development

To build the library on Linux, some packages needs to be installed:

- `build-essential` - GCC and development tools
- `libxt-dev` - X11 toolkit development headers
- `libgtk-3-dev` - GTK 3 toolkit development headers
- `g++-aarch64-linux-gnu` - GNU C++ compiler for the arm64 architecture (only on
  x86_64 Linux for cross-compiling for arm64 architecture)


### Ubuntu

~~~
sudo apt update
sudo apt install build-essential libxt-dev libgtk-3-dev
~~~

Only on x86_64 Linux for cross-compiling for arm64 architecture:

~~~
sudo apt install g++-aarch64-linux-gnu
~~~

Download `libgtk-3.so` for arm64 architecture:

~~~
cd flatlaf-natives/flatlaf-natives-linux/lib/aarch64
wget --no-verbose https://ports.ubuntu.com/pool/main/g/gtk%2b3.0/libgtk-3-0_3.24.18-1ubuntu1_arm64.deb
ar -x libgtk-3-0_3.24.18-1ubuntu1_arm64.deb data.tar.xz
tar -xvf data.tar.xz --wildcards --to-stdout "./usr/lib/aarch64-linux-gnu/libgtk-3.so.0.*" > libgtk-3.so
rm libgtk-3-0_3.24.18-1ubuntu1_arm64.deb data.tar.xz
~~~


### Fedora

~~~
sudo dnf group install c-development
sudo dnf install libXt-devel gtk3-devel
~~~


### CentOS

~~~
sudo yum install libXt-devel gtk3-devel
~~~
