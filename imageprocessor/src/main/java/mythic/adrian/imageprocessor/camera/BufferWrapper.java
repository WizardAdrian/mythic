package mythic.adrian.imageprocessor.camera;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by Adrian on 2017/6/5.
 * E-mail:aliu@in66.com
 */
@ThreadSafe
public final class BufferWrapper {

    @GuardedBy("this")
    private byte[] mFrameBufferFront;
    @GuardedBy("this")
    private byte[] mFrameBufferBack;
    @GuardedBy("this")
    private boolean mWriteToBack = true;

    public synchronized byte[] readBuffer() {
        return mWriteToBack ? mFrameBufferFront : mFrameBufferBack;
    }

    public synchronized byte[] writeBuffer() {
        return mWriteToBack ? mFrameBufferBack : mFrameBufferFront;
    }

    public synchronized void swapBuffers() {
        mWriteToBack = !mWriteToBack;
    }

    public synchronized void refreshCache(int w, int h) {
        if (mFrameBufferFront == null) {
            mFrameBufferFront = new byte[w * (h + h / 2)];
        }
        if (mFrameBufferBack == null) {
            mFrameBufferBack = new byte[w * (h + h / 2)];
        }
    }

    public synchronized void release(){
        mFrameBufferFront = null;
        mFrameBufferBack = null;
    }
}
