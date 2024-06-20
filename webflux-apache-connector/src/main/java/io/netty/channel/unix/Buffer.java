//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.netty.channel.unix;

import io.micrometer.core.instrument.Metrics;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Buffer {
    private Buffer() {
    }

    public static void free(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }

    public static ByteBuffer allocateDirectWithNativeOrder(int capacity) {
        Metrics.counter("byteBufferAllocateDirect").increment();
        return ByteBuffer.allocateDirect(capacity).order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }

    public static long memoryAddress(ByteBuffer buffer) {
        assert buffer.isDirect();

        return PlatformDependent.hasUnsafe() ? PlatformDependent.directBufferAddress(buffer) : memoryAddress0(buffer);
    }

    public static int addressSize() {
        return PlatformDependent.hasUnsafe() ? PlatformDependent.addressSize() : addressSize0();
    }

    private static native int addressSize0();

    private static native long memoryAddress0(ByteBuffer var0);
}
