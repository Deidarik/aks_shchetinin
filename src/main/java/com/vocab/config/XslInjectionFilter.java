package com.vocab.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class XslInjectionFilter implements Filter {

    private static final String XSL_TAG = "<?xml-stylesheet type=\"text/xsl\" href=\"/vocab.xsl\"?>\n";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Перехватываем только XML ответы нашего API
        if (httpResponse.getContentType() != null && httpResponse.getContentType().contains("application/xml")) {

            // Создаем обертку для перехвата вывода
            ResponseWrapper wrapper = new ResponseWrapper(httpResponse);
            chain.doFilter(request, wrapper);

            // Получаем оригинальный XML, добавляем тег и записываем в реальный ответ
            String originalXml = wrapper.toString();
            if (originalXml.startsWith("<?xml")) {
                // Вставляем после первой строки <?xml version="1.0"...?>
                int endOfFirstLine = originalXml.indexOf("?>");
                if (endOfFirstLine != -1) {
                    String modifiedXml = originalXml.substring(0, endOfFirstLine + 2) + "\n" + XSL_TAG + originalXml.substring(endOfFirstLine + 2);
                    response.getWriter().write(modifiedXml);
                } else {
                    response.getWriter().write(XSL_TAG + originalXml);
                }
            } else {
                response.getWriter().write(XSL_TAG + originalXml);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    // Внутренний класс для перехвата вывода
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream capture;
        private ServletOutputStream output;
        private PrintWriter writer;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
            capture = new ByteArrayOutputStream(response.getBufferSize());
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            if (output == null) {
                output = new ServletOutputStream() {
                    @Override
                    public void write(int b) { capture.write(b); }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setWriteListener(WriteListener listener) {}
                };
            }
            return output;
        }

        @Override
        public PrintWriter getWriter() {
            if (output != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            if (writer == null) {
                writer = new PrintWriter(capture);
            }
            return writer;
        }

        @Override
        public void flushBuffer() {
            if (writer != null) writer.flush();
            if (output != null) {
                try { output.flush(); } catch (IOException e) { /* ignore */ }
            }
        }

        @Override
        public String toString() {
            flushBuffer();
            return capture.toString();
        }
    }
}