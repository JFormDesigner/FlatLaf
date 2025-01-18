FlatLaf Windows 10/11 Native Library
====================================

This sub-project contains the source code for the FlatLaf Windows 10/11 native
library (DLL).

The native library can be built only on Windows and requires a C++ compiler.
Tested with Microsoft Visual C++ (MSVC) 2019 and 2022 (comes with Visual Studio
2019 and 2022).

The native library is available for following CPU architectures: `x86_64` (or
`amd64`), `x86` and `arm64` (or `aarch64`).

To be able to build FlatLaf on any platform, and without C++ compiler, the
pre-built DLLs are checked into Git at
[flatlaf-core/src/main/resources/com/formdev/flatlaf/natives/](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-core/src/main/resources/com/formdev/flatlaf/natives).

The DLLs were built on a GitHub server with the help of GitHub Actions. See:
[Native Libraries](https://github.com/JFormDesigner/FlatLaf/actions/workflows/natives.yml)
workflow. Then the produced Artifacts ZIP was downloaded, signed DLLs with
FormDev Software code signing certificate and committed the DLLs to Git.


## Development

To build the library on Windows using Gradle, (parts of)
[Visual Studio Community
2022](https://visualstudio.microsoft.com/downloads/)
needs to be installed. After downloading and running `VisualStudioSetup.exe` the
**Visual Studio Installer** is installed and started. Once running, it shows the
**Workloads** tab that allows you to install additional packages. Either choose
**Desktop development with C++**, or to save some disk space switch to the
**Single Components** tab and choose following components (newest versions):

- MSVC v143 - VS 2022 C++ x64/x86 Buildtools
- MSVC v143 - VS 2022 C++ ARM64/ARM64EC Buildtools
- Windows 11 SDK

Note that the Visual Studio Installer shows many similar named components for
MSVC. Make sure to choose exactly those components listed above.

Using
[Build Tools for Visual Studio 2022](https://visualstudio.microsoft.com/downloads/#remote-tools-for-visual-studio-2022)
(installs only compiler and SDKs) instead of
[Visual Studio Community
2022](https://visualstudio.microsoft.com/downloads/)
does not work with Gradle.

[Visual Studio Code](https://code.visualstudio.com/) with **C/C++** extension
can be used for C++ code editing.
