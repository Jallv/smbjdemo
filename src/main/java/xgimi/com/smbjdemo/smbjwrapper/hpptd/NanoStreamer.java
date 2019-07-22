package xgimi.com.smbjdemo.smbjwrapper.hpptd;


import android.text.TextUtils;
import android.util.Log;


import com.hierynomus.smbj.auth.AuthenticationContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import xgimi.com.smbjdemo.smbjwrapper.core.ShareFile;
import xgimi.com.smbjdemo.smbjwrapper.utils.log.KLog;

/**
 * make sure <p/>
 * {@link NanoHTTPD.Response#sendAsFixedLength(OutputStream, int) }<p/>
 * is protected
 */
public class NanoStreamer extends NanoHTTPD implements IStreamer {
    public final static String TAG = "NanoStreamer";

    private int serverPort;
    private static NanoStreamer streamer;
    private static AuthenticationContext ntlAuth = null;

    private NanoStreamer() {
        this(HttpHelper.DEFAULT_SERVER_PORT);
    }

    private NanoStreamer(int port) {
        super(HttpHelper.DEFAULT_IP, port);
        this.serverPort = port;
    }

    public final static NanoStreamer INSTANCE() {
        if (streamer == null) {
            synchronized (NanoStreamer.class) {
                if (streamer == null) {
                    streamer = new NanoStreamer();
                }
            }
        }
        return streamer;
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopStream() {
        closeAllConnections();
    }

    @Override
    public int getPort() {
        return serverPort;
    }

    @Override
    public String getIp() {
        return HttpHelper.DEFAULT_IP;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String uri = session.getUri();
        return respond(headers, uri);
    }
    @Override
    public synchronized void unRegisterConnection(Socket socket) {
        super.unRegisterConnection(socket);
    }

    private Response respond(Map<String, String> headers, String uri) {
        Log.d(TAG, "respond uri=" + uri);
        String mimeTypeForFile = MimeTypeUtils.getMimeType(uri);
        Log.d(TAG, "------> mimeType = " + mimeTypeForFile);
        String smbUri = SmbUrlTools.cropStreamSmbURL(uri);
        String ipAddress = HttpBean.getmIpAddress();
        Log.d(TAG, "ipAddress = " + ipAddress);
        Log.d(TAG, "smbUri = " + smbUri);
        String name = HttpBean.getmName();
        String password = HttpBean.getmPassword();
        Log.d(TAG, "name == " + name + "; password == " + password);
        String remoteSmbFileName;
        ntlAuth = null;
        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(password)) {
            int index = name.indexOf("/");
            if (index > 0) {
                String username = name.substring(index + 1);
                remoteSmbFileName = name.substring(0, index);
                ntlAuth = new AuthenticationContext(username, password.toCharArray(), "");
                Log.d(TAG, "NtlmPasswordAuthentication = " + remoteSmbFileName + " " + username);
            } else {
                remoteSmbFileName = null;
                ntlAuth = new AuthenticationContext(name, password.toCharArray(), "");
                Log.d(TAG, "domain == null NtlmPasswordAuthentication = " + remoteSmbFileName + " " + name);
            }
        }

        Response response = null;
        try {
            if (SmbUrlTools.isSmbUrl(smbUri) && !TextUtils.isEmpty(mimeTypeForFile)) {
                ShareFile smbFile;
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)) {
                    smbFile = ShareFile.build(smbUri);
                } else {
                    smbFile = ShareFile.build(smbUri, ntlAuth);
                }
                InputStream copyStream = new BufferedInputStream(smbFile.getInputStream());
                response = serveSmbFile(smbUri, headers, copyStream, smbFile, mimeTypeForFile);
            } else {
                Log.e(TAG, "NOT A VALID SMBFILE VIDEO URL:" + uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "respond Exception:" + e.getMessage());
        }
        return response != null ? response : createResponse(
                Response.Status.NOT_FOUND, MIME_PLAINTEXT,
                "Error 404, file not found.");
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private Response createNonBufferedResponse(Response.Status status, String mimeType, InputStream message, Long len) {
        Response res = new StreamResponse(status, mimeType, message, len);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private Response serveSmbFile(String smbFileUrl, Map<String, String> header, InputStream is, ShareFile smbFile,
                                  String mime) {
        Response res;
        try {
            // Calculate etag
            String eTag = Integer.toHexString((new StringBuilder(smbFile.getName()).append(smbFile.getLastWriteTime())
                                                                                   .append(smbFile.getFileSize()))
                    .hashCode());
            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                        ignored.printStackTrace();
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = smbFile.getFileSize();
            KLog.i("SharedInputStream", "fileSize=" + fileLen+",startFrom="+startFrom);
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", eTag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    is.skip(startFrom);
                    res = createNonBufferedResponse(Response.Status.PARTIAL_CONTENT, mime, is, fileLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", eTag);
                }
            } else {
                if (eTag.equals(header.get("if-none-match"))) {
                    res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
                } else {
                    res = createNonBufferedResponse(Response.Status.OK, mime, is, fileLen);
                    res.addHeader("ETag", eTag);
                }
            }
        } catch (IOException ioe) {
            res = createResponse(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }
        return res;
    }

    private static class StreamResponse extends Response {
        private long available;

        public StreamResponse(Status status, String mimeType, InputStream data, long available) {
            super(status, mimeType, data);
            this.available = available;
        }

        @Override
        protected void sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header,
                                                                  int size) {
            // This is to support partial sends, see serveFile()
            long pending = (getData() != null ? available : 0);
            // Such as bytes 203437551-205074073/205074074
            String string = header.get("Content-Range");
            if (string != null) {
                if (string.startsWith("bytes ")) {
                    string = string.substring("bytes ".length());
                }
                Long start = Long.parseLong(string.split("-")[0]);
                pw.print("Content-Length: " + (pending - start) + "\r\n");
            } else {
                pw.print("Content-Length: " + pending + "\r\n");
            }
        }

        @Override
        protected void sendAsFixedLength(OutputStream outputStream, int pending) throws IOException {
                       // super.sendAsFixedLength(outputStream, pending);
            sendAsFixedLength(outputStream);
        }

        private void sendAsFixedLength(OutputStream outputStream) throws IOException {
            long pending = (getData() != null ? available : 0);
            if (getRequestMethod() != Method.HEAD && getData() != null) {
                byte[] buff = new byte[BUFFER_SIZE_1M];
                while (pending > 0) {
                    // Note the ugly cast to int to support > 2gb files. If pending < BUFFER_SIZE_1M we can safely cast
                    // anyway.
                    int read = getData().read(buff, 0, ((pending > BUFFER_SIZE_1M) ? BUFFER_SIZE_1M : (int) pending));
                    if (read <= 0) {
                        break;
                    }
                    outputStream.write(buff, 0, read);
                    pending -= read;
                }
            }
        }

    }

}
