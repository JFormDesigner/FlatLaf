#ifndef FLATLAF_WIN32_ALLOCROUTINES_H
#define FLATLAF_WIN32_ALLOCROUTINES_H

#include <stddef.h>
#include <windows.h>

struct FlatLafNoThrowT {
};

constexpr FlatLafNoThrowT FlatLafNoThrow{};

#if defined(_MSC_VER)
#define FLATLAF_WIN32_ALLOC_INLINE __forceinline
#elif (defined(__GNUC__) || defined(__clang__))
#define FLATLAF_WIN32_ALLOC_INLINE inline __attribute__((__always_inline__))
#else
#define FLATLAF_WIN32_ALLOC_INLINE inline
#endif

FLATLAF_WIN32_ALLOC_INLINE void* FlatLafWin32ProcessHeapAlloc(size_t cb) noexcept {
    #ifdef _WIN32
    return ::HeapAlloc(::GetProcessHeap(), HEAP_ZERO_MEMORY, cb);
    #else
    return ::calloc(cb, 1);
    #endif
}

FLATLAF_WIN32_ALLOC_INLINE void FlatLafWin32ProcessHeapFree(void* pv) noexcept {
    #ifdef _WIN32
    if(pv)
        ::HeapFree(::GetProcessHeap(), 0, pv);
    #else
    if(pv)
        ::free(pv);
    #endif
}

FLATLAF_WIN32_ALLOC_INLINE void* operator new(size_t cb, const FlatLafNoThrowT& tag) noexcept {
    return FlatLafWin32ProcessHeapAlloc(cb);
}

FLATLAF_WIN32_ALLOC_INLINE void* operator new[](size_t cb, const FlatLafNoThrowT& tag) noexcept {
    return FlatLafWin32ProcessHeapAlloc(cb);
}

FLATLAF_WIN32_ALLOC_INLINE void operator delete(void* pv, const FlatLafNoThrowT& tag) noexcept {
    FlatLafWin32ProcessHeapFree(pv);
}

FLATLAF_WIN32_ALLOC_INLINE void operator delete[](void* pv, const FlatLafNoThrowT& tag) noexcept {
    FlatLafWin32ProcessHeapFree(pv);
}
#endif
