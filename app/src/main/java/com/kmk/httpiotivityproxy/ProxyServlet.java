package com.kmk.httpiotivityproxy;

import android.util.Log;

import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet {
    private static final String TAG = ProxyServlet.class.getSimpleName();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getParameter("uri");
        Log.d(TAG, uri);
        IoTivityRequest ioTivityRequest = new IoTivityRequest();
        synchronized (resp) {
            ioTivityRequest.test(uri, resp);
            try {
                resp.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        resp.setContentType("text/plain; charset=UTF-8");
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(ioTivityRequest.result);
    }
}