FlatLaf Windows 10/11 Native Library
====================================

This sub-project contains the source code for the FlatLaf Windows 10/11 native
library (DLL).

The native library can be built only on Windows and requires a C++ compiler.
Tested with Microsoft Visual C++ (MSVC) 2019 and 2022 (comes with Visual Studio
2019 and 2022).

The native library is available for folloging CPU architectures: `x86_64` (or
`amd64`), `x86` and `arm64`.

To be able to build FlatLaf on any platform, and without C++ compiler, the
pre-built DLLs are checked into Git at
[flatlaf-core/src/main/resources/com/formdev/flatlaf/natives/](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-core/src/main/resources/com/formdev/flatlaf/natives).

The DLLs were built on a GitHub server with the help of GitHub Actions. See:
[Native Libraries](https://github.com/JFormDesigner/FlatLaf/actions/workflows/natives.yml)
workflow. Then the produced Artifacts ZIP was downloaded, signed DLLs with
FormDev Software code signing certificate and committed the DLLs to Git.
