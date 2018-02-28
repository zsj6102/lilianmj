package org.soshow.beautydu.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

@SuppressWarnings("deprecation")
public class AndroidMultiPartEntity extends MultipartEntity {

    public AndroidMultiPartEntity(final HttpMultipartMode mode) {
        super(mode);
    }

    public AndroidMultiPartEntity(HttpMultipartMode mode,
            final String boundary, final Charset charset,
            final ProgressListener listener) {
        super(mode, boundary, charset);
    }

    public AndroidMultiPartEntity() {
        super();

    }

    public void progressListener(final OutputStream outstream)
            throws IOException {
        super.writeTo(new CountingOutputStream(outstream));
    }

    public static interface ProgressListener {
        void transferred(long num);
    }

    public static class CountingOutputStream extends FilterOutputStream {

        @SuppressWarnings("unused")
        private long transferred;

        public CountingOutputStream(final OutputStream out) {
            super(out);
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
        }
    }
}