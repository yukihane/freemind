package freemind.main;

import java.io.IOException;
import java.io.Writer;

/**
 * javax.swing.text.html.HTMLWriter で出力すると日本語が数値文字参照になってしまう問題に対処するためのクラス.
 * @see http://www.ne.jp/asahi/hishidama/home/tech/java/swing/HTMLWriter.html
 */
class CodeWriter extends Writer {

    protected Writer w;

    public CodeWriter(Writer w) {
        this.w = w;
    }

    /**
     * バッファに保存中かどうか
     */
    private boolean code;

    /**
     * コード文字列を保持する為のバッファ
     */
    private StringBuffer sb = new StringBuffer();

    private char[] c = new char[1];

    /**
     * <p>コード文字列が分割して呼ばれても、必ず先頭は&amp;#で、末尾が;で来るのが前提</p>
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len >= 2 && cbuf[off] == '&' && cbuf[off + 1] == '#') {
            sb.setLength(0); //バッファをクリア
            code = true;
        }
        if (code) {
            sb.append(cbuf, off, len);
            len = sb.length();
            if (sb.charAt(len - 1) != ';')
                return;

            String str = sb.substring(2, len - 1);
            cbuf = c;
            cbuf[off = 0] = (char) Integer.parseInt(str);
            len = 1;
            code = false;
        }

        w.write(cbuf, off, len);
    }

    public void flush() throws IOException {
        w.flush();
    }

    public void close() throws IOException {
        w.close();
    }
}
