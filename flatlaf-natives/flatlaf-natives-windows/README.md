FlatLaf Windows 10 Native Library
=================================

This sub-project contains the source code for the FlatLaf Windows 10 native
library (DLL).

The native library can be built only on Windows and requires a C++ compiler.
Tested only with Microsoft Visual C++ 2019 (comes with Visual Studio 2019).

To be able to build FlatLaf on any platform, and without C++ compiler, the
pre-built DLL is checked into Git at
[flatlaf-core/src/main/resources/com/formdev/flatlaf/natives/](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-core/src/main/resources/com/formdev/flatlaf/natives).

The DLL was built on a GitHub server with the help of GitHub Actions. See:
[Native Libraries](https://github.com/JFormDesigner/FlatLaf/actions/workflows/natives.yml)
workflow. Then the produced Artifacts ZIP was downloaded and the DLL checked
into Git.
