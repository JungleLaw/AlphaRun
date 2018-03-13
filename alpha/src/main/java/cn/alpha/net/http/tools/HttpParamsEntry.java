package cn.alpha.net.http.tools;

/**
 * Created by Law on 2017/4/5.
 */

public class HttpParamsEntry implements Comparable<HttpParamsEntry> {
    public String k;
    public String v;

    public HttpParamsEntry(String key, String value) {
        k = key;
        v = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HttpParamsEntry) {
            return k.equals(((HttpParamsEntry) o).k);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return k.hashCode();
    }

    @Override
    public int compareTo(HttpParamsEntry another) {
        if (k == null) {
            return -1;
        } else {
            return k.compareTo(another.k);
        }
    }
}
