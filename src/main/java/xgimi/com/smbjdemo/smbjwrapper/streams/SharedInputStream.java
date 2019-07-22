package xgimi.com.smbjdemo.smbjwrapper.streams;


import com.hierynomus.smbj.ProgressListener;
import com.hierynomus.smbj.share.File;

import java.io.IOException;
import java.io.InputStream;

import xgimi.com.smbjdemo.smbjwrapper.utils.MethodAverageTime;
import xgimi.com.smbjdemo.smbjwrapper.utils.log.KLog;

/**
 * This class represents a decorated input stream that respects the reference counting close mechanism of the file.
 *
 * @author Simon Wächter
 */
public class SharedInputStream extends InputStream {
    private static final String TAG = "SharedInputStream";
    /**
     * File that provides the input stream.
     */
    private final File file;

    /**
     * Input stream of the file that will be decorated.
     */
    private final InputStream inputStream;

    /**
     * Create a new decorated input stream that respects the reference couting close mechanism of the file.
     *
     * @param file File that will provide the input stream
     */
    public SharedInputStream(File file) {
        this.file = file;
        this.inputStream = file.getInputStream(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                KLog.i("DirectTcpPacketReader", "onProgressChanged numBytes" + numBytes + ",totalBytes=" + totalBytes);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    private MethodAverageTime mMethodAverageTime = new MethodAverageTime(TAG);
    private long allSize;

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        allSize += len;
        // KLog.i(TAG, "read off=" + off + ",len=" + len + ",allSize=" + allSize);
        mMethodAverageTime.start();
        int result = inputStream.read(b, off, len);
        mMethodAverageTime.end(false);
        double ave = mMethodAverageTime.average / 1000f;
        ave = ave == 0 ? 1 : ave;
        KLog.i(TAG, "speed=" + (int) ((len / 1024f) / ave) + "kb/s");
        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        KLog.i(TAG, "skip to " + n);
        return inputStream.skip(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
        file.close();
        KLog.i(TAG, "close");
    }
}
