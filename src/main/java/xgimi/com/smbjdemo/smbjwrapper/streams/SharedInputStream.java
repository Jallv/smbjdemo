package xgimi.com.smbjdemo.smbjwrapper.streams;

import android.util.Log;

import com.hierynomus.smbj.ProgressListener;
import com.hierynomus.smbj.share.File;

import java.io.IOException;
import java.io.InputStream;

import xgimi.com.smbjdemo.smbjwrapper.utils.MethodAverageTime;

/**
 * This class represents a decorated input stream that respects the reference counting close mechanism of the file.
 *
 * @author Simon Wächter
 */
public class SharedInputStream extends InputStream {

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
                Log.i("SharedInputStream","onProgressChanged numBytes"+numBytes+",totalBytes="+totalBytes);
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

    private MethodAverageTime mMethodAverageTime = new MethodAverageTime("SharedInputStream");
    private long allSize;

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        allSize += len;
        Log.i("SharedInputStream", "read off=" + off + ",len=" + len + ",allSize=" + allSize);
        mMethodAverageTime.start();
        int result = super.read(b, off, len);
        mMethodAverageTime.end();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
        file.close();
    }
}
