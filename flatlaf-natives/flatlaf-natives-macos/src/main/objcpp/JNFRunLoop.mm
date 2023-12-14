// from https://github.com/apple/openjdk/blob/xcodejdk14-release/apple/JavaNativeFoundation/JavaNativeFoundation/JNFRunLoop.m

/*
 * Copyright (c) 2009-2020 Apple Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 *   3. Neither the name of the copyright holder nor the names of its
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#import "JNFRunLoop.h"

#import <Cocoa/Cocoa.h>


NSString *JNFRunLoopDidStartNotification = @"JNFRunLoopDidStartNotification";

static NSString *AWTRunLoopMode = @"AWTRunLoopMode";
static NSArray *sPerformModes = nil;

@implementation FlatJNFRunLoop

+ (void)initialize {
    if (sPerformModes) return;
    sPerformModes = [[NSArray alloc] initWithObjects:NSDefaultRunLoopMode, NSModalPanelRunLoopMode, NSEventTrackingRunLoopMode, AWTRunLoopMode, nil];
}

+ (NSString *)javaRunLoopMode {
    return AWTRunLoopMode;
}

+ (void)performOnMainThread:(SEL)aSelector on:(id)target withObject:(id)arg waitUntilDone:(BOOL)waitUntilDone {
    [target performSelectorOnMainThread:aSelector withObject:arg waitUntilDone:waitUntilDone modes:sPerformModes];
}

+ (void)_performDirectBlock:(void (^)(void))block {
    block();
}

+ (void)_performCopiedBlock:(void (^)(void))newBlock {
    newBlock();
    Block_release(newBlock);
}

+ (void)performOnMainThreadWaiting:(BOOL)waitUntilDone withBlock:(void (^)(void))block {
    if (waitUntilDone) {
        [self performOnMainThread:@selector(_performDirectBlock:) on:self withObject:block waitUntilDone:YES];
    } else {
        void (^newBlock)(void) = Block_copy(block);
        [self performOnMainThread:@selector(_performCopiedBlock:) on:self withObject:newBlock waitUntilDone:NO];
    }
}

@end
