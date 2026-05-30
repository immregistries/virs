package org.immregistries.virs.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Placeholder client UI servlet.
 * <p>
 * GET /client
 * </p>
 * <p>
 * This page is a placeholder for future development. No vaccine resolution
 * logic is implemented.
 * </p>
 */
@WebServlet("/client")
public class ClientServlet extends HttpServlet {

    private static final String HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>VIRS Client - Demonstrator</title>
                <style>
                    body  { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; padding: 0 20px; color: #333; }
                    h1    { color: #2c5f8a; }
                    label { display: block; margin-top: 16px; font-weight: bold; }
                    input[type=text] { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
                    button { margin-top: 16px; padding: 10px 24px; background: #2c5f8a; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background: #1e4266; }
                    .placeholder-notice { margin-top: 24px; color: #888; font-style: italic; }
                    nav a { color: #2c5f8a; }
                </style>
            </head>
            <body>
                <h1>VIRS Client Demo</h1>
                <p>FHIR-based Vaccine Identification and Resolution Service — Demonstrator</p>
                <form>
                    <label for="lotNumber">Lot Number</label>
                    <input type="text" id="lotNumber" name="lotNumber" placeholder="Enter vaccine lot number" />
                    <button type="button">Submit</button>
                </form>
                <p class="placeholder-notice">
                    This page is a placeholder for future development. No functionality is implemented yet.
                </p>
                <nav><a href=".">&#8592; Home</a></nav>
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
