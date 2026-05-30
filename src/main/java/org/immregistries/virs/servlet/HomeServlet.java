package org.immregistries.virs.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Home page servlet.
 * <p>
 * GET /
 * </p>
 */
@WebServlet("/")
public class HomeServlet extends HttpServlet {

    private static final String HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>VIRS Demonstrator</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; color: #333; }
                    h1   { color: #2c5f8a; }
                    .notice { background: #fff3cd; border: 1px solid #ffc107; padding: 12px 16px; border-radius: 4px; margin-top: 20px; }
                    nav a { margin-right: 16px; color: #2c5f8a; }
                </style>
            </head>
            <body>
                <h1>VIRS Demonstrator</h1>
                <p><strong>FHIR-based Vaccine Identification and Resolution Service</strong></p>
                <p>Version: 0.1</p>
                <div class="notice">
                    <strong>Notice:</strong> This is a demonstration service only.
                    <ul>
                        <li>No data is persisted.</li>
                        <li>No authentication is required or enforced.</li>
                    </ul>
                </div>
                <h2>Navigation</h2>
                <nav>
                    <a href="api/status">API Status</a>
                    <a href="client">Client Demo</a>
                </nav>
            </body>
            </html>
            """;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.write(HTML);
    }
}
