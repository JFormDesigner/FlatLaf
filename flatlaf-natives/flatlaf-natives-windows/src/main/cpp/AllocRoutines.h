#ifndef FLATLAF_WIN32_ALLOCROUTINES_H
#define FLATLAF_WIN32_ALLOCROUTINES_H

#include <stddef.h>
#include <windows.h>

struct NoThrowingWin32HeapAllocT {
};

constexpr NoThrowingWin32HeapAllocT NoThrowingWin32HeapAlloc{};

#if defined(_MSC_VER)
#define FLATLAF_WIN32_ALLOC_INLINE __forceinline
#elif (defined(__GNUC__) || defined(__clang__))
#define FLATLAF_WIN32_ALLOC_INLINE inline __attribute__((__always_inline__))
#else
#define FLATLAF_WIN32_ALLOC_INLINE inline
#endif

FLATLAF_WIN32_ALLOC_INLINE void* AllocateUsingProcessHeap(size_t cb) noexcept {
    return ::HeapAlloc(::GetProcessHeap(), HEAP_ZERO_MEMORY, cb);
}

FLATLAF_WIN32_ALLOC_INLINE void DeleteFromProcessHeap(void* pv) noexcept {
    if(pv)
        ::HeapFree(::GetProcessHeap(), 0, pv);
}

FLATLAF_WIN32_ALLOC_INLINE void* operator new(size_t cb, const NoThrowingWin32HeapAllocT& tag) noexcept {
    return AllocateUsingProcessHeap(cb);
}

FLATLAF_WIN32_ALLOC_INLINE void* operator new[](size_t cb, const NoThrowingWin32HeapAllocT& tag) noexcept {
    return AllocateUsingProcessHeap(cb);
}

FLATLAF_WIN32_ALLOC_INLINE void operator delete(void* pv, const NoThrowingWin32HeapAllocT& tag) noexcept {
    DeleteFromProcessHeap(pv);
}

FLATLAF_WIN32_ALLOC_INLINE void operator delete[](void* pv, const NoThrowingWin32HeapAllocT& tag) noexcept {
    DeleteFromProcessHeap(pv);
}
#endif
