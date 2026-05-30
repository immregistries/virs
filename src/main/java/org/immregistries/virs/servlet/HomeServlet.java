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
                                .card { background: #f7fbff; border: 1px solid #d3e2f0; padding: 14px 16px; border-radius: 6px; margin-top: 16px; }
                                code, pre { font-family: Consolas, "Courier New", monospace; }
                                pre { background: #0f1b2d; color: #dbe9ff; border-radius: 6px; padding: 12px; overflow-x: auto; }
                                ul { margin-top: 8px; }
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

                                            <div class="card">
                                                    <h2>FHIR Operation Endpoint</h2>
                                                    <p>
                                                            Endpoint URL:
                                                            <code>https://informatics.immregistries.org/virs/fhir/Immunization/$resolve-identification</code>
                                                    </p>
                                                    <p>
                                                            Method: <code>POST</code><br/>
                                                            Content-Type: <code>application/fhir+json</code><br/>
                                                            Accept: <code>application/fhir+json</code>
                                                    </p>
                                            </div>

                                            <div class="card">
                                                    <h2>How It Works</h2>
                                                    <ol>
                                                            <li>Send a FHIR <code>Parameters</code> resource with one parameter named <code>immunization</code>.</li>
                                                            <li>The <code>immunization</code> parameter contains an <code>Immunization</code> resource.</li>
                                                            <li><code>Immunization.lotNumber</code> is required.</li>
                                                            <li>Optional fields that improve matching include CVX, MVX, administration date, and product text.</li>
                                                            <li>The service returns a FHIR <code>Parameters</code> response with resolution status, confidence, summary, issues, and candidates (if any).</li>
                                                    </ol>
                                            </div>

                                            <div class="card">
                                                    <h2>Request Shape (Example)</h2>
                                                    <pre>{
                "resourceType": "Parameters",
                "parameter": [
                    {
                        "name": "immunization",
                        "resource": {
                            "resourceType": "Immunization",
                            "status": "completed",
                            "lotNumber": "TY00T5",
                            "occurrenceDateTime": "2026-05-29",
                            "vaccineCode": {
                                "coding": [
                                    {
                                        "system": "http://hl7.org/fhir/sid/cvx",
                                        "code": "208"
                                    }
                                ],
                                "text": "COVID-19 vaccine"
                            },
                            "manufacturer": {
                                "identifier": {
                                    "system": "http://hl7.org/fhir/sid/mvx",
                                    "value": "PFR"
                                }
                            }
                        }
                    }
                ]
            }</pre>
                                            </div>

                                            <div class="card">
                                                    <h2>Response Highlights</h2>
                                                    <ul>
                                                            <li><code>resolutionStatus</code>: resolved, probable, multiple-candidates, unresolved, conflict, or invalid-request.</li>
                                                            <li><code>resolvedImmunization</code>: populated when the service can resolve a product.</li>
                                                            <li><code>confidence</code>: decimal confidence score for the overall result.</li>
                                                            <li><code>outcome</code>: <code>OperationOutcome</code> issues for informational, warning, or error details.</li>
                                                            <li><code>candidate</code>: candidate immunization options when multiple products match.</li>
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
