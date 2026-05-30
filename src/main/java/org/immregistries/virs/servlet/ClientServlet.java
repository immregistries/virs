package org.immregistries.virs.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Browser-based demonstration client UI.
 * <p>
 * GET /client
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
                <title>VIRS Client Demonstrator</title>
                <style>
                    :root {
                        --bg: #f5f8fc;
                        --panel: #ffffff;
                        --border: #d5deea;
                        --ink: #1d2a3a;
                        --muted: #51627a;
                        --accent: #1e5f93;
                        --accent-2: #16476d;
                        --warn: #a65809;
                        --ok: #1a6a3e;
                        --danger: #8b1e1e;
                    }

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;
                        font-family: "Segoe UI", Tahoma, sans-serif;
                        background: linear-gradient(180deg, #f9fbff 0%, var(--bg) 100%);
                        color: var(--ink);
                    }

                    .container {
                        width: min(1200px, 96%);
                        margin: 20px auto 40px;
                    }

                    .hero,
                    .panel {
                        background: var(--panel);
                        border: 1px solid var(--border);
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(17, 42, 67, 0.06);
                    }

                    .hero {
                        padding: 20px;
                        margin-bottom: 16px;
                    }

                    .hero h1 {
                        margin: 0 0 10px;
                        color: var(--accent-2);
                        font-size: 1.7rem;
                    }

                    .hero p {
                        margin: 8px 0;
                        color: var(--muted);
                    }

                    .layout {
                        display: grid;
                        grid-template-columns: 1.05fr 1fr;
                        gap: 16px;
                    }

                    .panel {
                        padding: 16px;
                    }

                    h2 {
                        margin: 0 0 12px;
                        font-size: 1.15rem;
                        color: var(--accent-2);
                    }

                    .field-grid {
                        display: grid;
                        grid-template-columns: repeat(2, minmax(0, 1fr));
                        gap: 10px 12px;
                    }

                    .field {
                        display: flex;
                        flex-direction: column;
                    }

                    .field.service,
                    .field.product {
                        grid-column: 1 / -1;
                    }

                    label {
                        font-size: 0.88rem;
                        margin-bottom: 4px;
                        color: var(--muted);
                        font-weight: 600;
                    }

                    input {
                        width: 100%;
                        border: 1px solid #b8c8dc;
                        border-radius: 6px;
                        padding: 9px 10px;
                        font-size: 0.96rem;
                    }

                    input:focus {
                        outline: 2px solid rgba(30, 95, 147, 0.25);
                        border-color: var(--accent);
                    }

                    .btn-row {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 8px;
                        margin-top: 14px;
                    }

                    button {
                        border: 1px solid var(--accent);
                        background: var(--accent);
                        color: #fff;
                        border-radius: 6px;
                        padding: 8px 12px;
                        font-size: 0.9rem;
                        cursor: pointer;
                    }

                    button:hover {
                        background: var(--accent-2);
                        border-color: var(--accent-2);
                    }

                    button.secondary {
                        background: #edf3fb;
                        border-color: #b4c8df;
                        color: #294a6b;
                    }

                    button.secondary:hover {
                        background: #dde9f7;
                    }

                    button:disabled {
                        opacity: 0.6;
                        cursor: not-allowed;
                    }

                    .message {
                        margin-top: 12px;
                        padding: 10px 12px;
                        border-radius: 6px;
                        font-size: 0.9rem;
                        border: 1px solid #d7e2ef;
                        background: #f7fbff;
                    }

                    .message.warn {
                        border-color: #efcb9f;
                        background: #fff8ef;
                        color: var(--warn);
                    }

                    .message.ok {
                        border-color: #abd6bb;
                        background: #f1fbf4;
                        color: var(--ok);
                    }

                    .message.err {
                        border-color: #e6b0b0;
                        background: #fff2f2;
                        color: var(--danger);
                    }

                    .kv-grid {
                        display: grid;
                        grid-template-columns: 180px 1fr;
                        gap: 6px 10px;
                    }

                    .kv-grid .key {
                        color: var(--muted);
                        font-weight: 600;
                    }

                    .list-box {
                        border: 1px solid var(--border);
                        border-radius: 6px;
                        padding: 10px;
                        background: #fbfdff;
                        min-height: 50px;
                    }

                    .list-item {
                        margin-bottom: 10px;
                        padding-bottom: 10px;
                        border-bottom: 1px solid #e4ebf4;
                    }

                    .list-item:last-child {
                        margin-bottom: 0;
                        padding-bottom: 0;
                        border-bottom: none;
                    }

                    .raw {
                        margin-top: 14px;
                    }

                    pre {
                        margin: 8px 0 0;
                        border: 1px solid #c7d7ea;
                        border-radius: 6px;
                        background: #0f1b2d;
                        color: #dbe9ff;
                        padding: 12px;
                        max-height: 260px;
                        overflow: auto;
                        font-family: Consolas, "Courier New", monospace;
                        font-size: 0.82rem;
                        line-height: 1.35;
                        white-space: pre-wrap;
                    }

                    .empty {
                        color: #65748a;
                        font-style: italic;
                    }

                    nav {
                        margin-top: 16px;
                    }

                    nav a {
                        color: var(--accent);
                        text-decoration: none;
                    }

                    nav a:hover {
                        text-decoration: underline;
                    }

                    @media (max-width: 960px) {
                        .layout {
                            grid-template-columns: 1fr;
                        }
                    }

                    @media (max-width: 640px) {
                        .field-grid {
                            grid-template-columns: 1fr;
                        }

                        .kv-grid {
                            grid-template-columns: 1fr;
                        }
                    }
                </style>
            </head>
            <body>
                <main class="container">
                    <section class="hero">
                        <h1>VIRS Demonstrator Client</h1>
                        <p>Use this page to create a FHIR operation request, submit it to the VIRS endpoint, and inspect both parsed and raw responses.</p>
                        <p>This is a demonstration utility only. No data is persisted.</p>
                    </section>

                    <div class="layout">
                        <section class="panel" aria-label="Input Section">
                            <h2>Request Input</h2>
                            <div class="field-grid">
                                <div class="field service">
                                    <label for="serviceUrl">Service URL</label>
                                    <input id="serviceUrl" type="text" />
                                </div>
                                <div class="field">
                                    <label for="lotNumber">Lot Number</label>
                                    <input id="lotNumber" type="text" />
                                </div>
                                <div class="field">
                                    <label for="cvx">CVX</label>
                                    <input id="cvx" type="text" />
                                </div>
                                <div class="field">
                                    <label for="mvx">MVX</label>
                                    <input id="mvx" type="text" />
                                </div>
                                <div class="field">
                                    <label for="ndc">NDC</label>
                                    <input id="ndc" type="text" />
                                </div>
                                <div class="field">
                                    <label for="administrationDate">Administration Date</label>
                                    <input id="administrationDate" type="date" />
                                </div>
                                <div class="field product">
                                    <label for="productText">Product Text</label>
                                    <input id="productText" type="text" />
                                </div>
                            </div>

                            <div class="btn-row">
                                <button id="submitBtn" type="button">Submit</button>
                                <button id="exactBtn" type="button" class="secondary">Load Exact Match Example</button>
                                <button id="suffixBtn" type="button" class="secondary">Load Suffix Parsing Example</button>
                                <button id="ozeroBtn" type="button" class="secondary">Load O/Zero Correction Example</button>
                                <button id="conflictBtn" type="button" class="secondary">Load Conflict Example</button>
                                <button id="unknownBtn" type="button" class="secondary">Load Unknown Lot Example</button>
                                <button id="multipleBtn" type="button" class="secondary">Load Multiple Candidates Example</button>
                                <button id="clearBtn" type="button" class="secondary">Clear</button>
                            </div>

                            <div id="message" class="message">Ready.</div>

                            <div class="raw">
                                <h2>Raw FHIR Request</h2>
                                <pre id="rawRequest" class="empty">No request generated yet.</pre>
                            </div>

                            <div class="raw">
                                <h2>Raw FHIR Response</h2>
                                <pre id="rawResponse" class="empty">No response received yet.</pre>
                            </div>
                        </section>

                        <section class="panel" aria-label="Result Section">
                            <h2>Result Summary</h2>
                            <div id="summaryGrid" class="kv-grid">
                                <div class="key">Resolution Status</div><div id="resolutionStatus">-</div>
                                <div class="key">Resolved Lot</div><div id="resolvedLot">-</div>
                                <div class="key">Resolved CVX</div><div id="resolvedCvx">-</div>
                                <div class="key">Resolved MVX</div><div id="resolvedMvx">-</div>
                                <div class="key">Confidence</div><div id="confidence">-</div>
                                <div class="key">Summary</div><div id="summary">-</div>
                            </div>

                            <div class="raw">
                                <h2>Issues</h2>
                                <div id="issues" class="list-box"></div>
                            </div>

                            <div class="raw">
                                <h2>Candidates</h2>
                                <div id="candidates" class="list-box"></div>
                            </div>
                        </section>
                    </div>

                    <nav><a href=".">&#8592; Home</a></nav>
                </main>

                <script>
                    (function () {
                        const CVX_SYSTEM = "http://hl7.org/fhir/sid/cvx";
                        const MVX_SYSTEM = "http://hl7.org/fhir/sid/mvx";
                        const NDC_SYSTEM = "http://hl7.org/fhir/sid/ndc";

                        const defaults = {
                            serviceUrl: "https://informatics.immregistries.org/virs/fhir/Immunization/$resolve-identification",
                            lotNumber: "TY00T5 Priv",
                            cvx: "208",
                            mvx: "PFR",
                            ndc: "",
                            administrationDate: "2026-05-29",
                            productText: "COVID-19 vaccine"
                        };

                        const ids = {
                            serviceUrl: document.getElementById("serviceUrl"),
                            lotNumber: document.getElementById("lotNumber"),
                            cvx: document.getElementById("cvx"),
                            mvx: document.getElementById("mvx"),
                            ndc: document.getElementById("ndc"),
                            administrationDate: document.getElementById("administrationDate"),
                            productText: document.getElementById("productText"),
                            submitBtn: document.getElementById("submitBtn"),
                            message: document.getElementById("message"),
                            rawRequest: document.getElementById("rawRequest"),
                            rawResponse: document.getElementById("rawResponse"),
                            resolutionStatus: document.getElementById("resolutionStatus"),
                            resolvedLot: document.getElementById("resolvedLot"),
                            resolvedCvx: document.getElementById("resolvedCvx"),
                            resolvedMvx: document.getElementById("resolvedMvx"),
                            confidence: document.getElementById("confidence"),
                            summary: document.getElementById("summary"),
                            issues: document.getElementById("issues"),
                            candidates: document.getElementById("candidates")
                        };

                        function setMessage(text, styleClass) {
                            ids.message.textContent = text;
                            ids.message.className = "message" + (styleClass ? " " + styleClass : "");
                        }

                        function valueOf(input) {
                            return input.value ? input.value.trim() : "";
                        }

                        function setFields(values) {
                            ids.serviceUrl.value = values.serviceUrl !== undefined ? values.serviceUrl : defaults.serviceUrl;
                            ids.lotNumber.value = values.lotNumber !== undefined ? values.lotNumber : defaults.lotNumber;
                            ids.cvx.value = values.cvx !== undefined ? values.cvx : defaults.cvx;
                            ids.mvx.value = values.mvx !== undefined ? values.mvx : defaults.mvx;
                            ids.ndc.value = values.ndc !== undefined ? values.ndc : defaults.ndc;
                            ids.administrationDate.value = values.administrationDate !== undefined
                                ? values.administrationDate
                                : defaults.administrationDate;
                            ids.productText.value = values.productText !== undefined ? values.productText : defaults.productText;
                        }

                        function clearRenderedData() {
                            ids.resolutionStatus.textContent = "-";
                            ids.resolvedLot.textContent = "-";
                            ids.resolvedCvx.textContent = "-";
                            ids.resolvedMvx.textContent = "-";
                            ids.confidence.textContent = "-";
                            ids.summary.textContent = "-";

                            ids.issues.innerHTML = "";
                            ids.candidates.innerHTML = "";

                            const issuesEmpty = document.createElement("div");
                            issuesEmpty.className = "empty";
                            issuesEmpty.textContent = "No issues.";
                            ids.issues.appendChild(issuesEmpty);

                            const candidatesEmpty = document.createElement("div");
                            candidatesEmpty.className = "empty";
                            candidatesEmpty.textContent = "No candidates.";
                            ids.candidates.appendChild(candidatesEmpty);
                        }

                        function buildRequestPayload() {
                            const lotNumber = valueOf(ids.lotNumber);
                            const cvx = valueOf(ids.cvx);
                            const mvx = valueOf(ids.mvx);
                            const ndc = valueOf(ids.ndc);
                            const administrationDate = valueOf(ids.administrationDate);
                            const productText = valueOf(ids.productText);

                            const immunization = {
                                resourceType: "Immunization",
                                status: "completed"
                            };

                            if (lotNumber) {
                                immunization.lotNumber = lotNumber;
                            }

                            if (administrationDate) {
                                immunization.occurrenceDateTime = administrationDate;
                            }

                            const coding = [];
                            if (cvx) {
                                coding.push({
                                    system: CVX_SYSTEM,
                                    code: cvx
                                });
                            }
                            if (ndc) {
                                coding.push({
                                    system: NDC_SYSTEM,
                                    code: ndc
                                });
                            }

                            if (coding.length > 0 || productText) {
                                immunization.vaccineCode = {};
                                if (coding.length > 0) {
                                    immunization.vaccineCode.coding = coding;
                                }
                                if (productText) {
                                    immunization.vaccineCode.text = productText;
                                }
                            }

                            if (mvx) {
                                immunization.manufacturer = {
                                    identifier: {
                                        system: MVX_SYSTEM,
                                        value: mvx
                                    }
                                };
                            }

                            return {
                                resourceType: "Parameters",
                                parameter: [
                                    {
                                        name: "immunization",
                                        resource: immunization
                                    }
                                ]
                            };
                        }

                        function prettyJson(text) {
                            if (!text || !text.trim()) {
                                return "";
                            }
                            try {
                                return JSON.stringify(JSON.parse(text), null, 2);
                            } catch (error) {
                                return text;
                            }
                        }

                        function findParameter(parameters, name) {
                            return (parameters || []).find(function (p) {
                                return p && p.name === name;
                            });
                        }

                        function findParameters(parameters, name) {
                            return (parameters || []).filter(function (p) {
                                return p && p.name === name;
                            });
                        }

                        function findPart(parts, name) {
                            return (parts || []).find(function (p) {
                                return p && p.name === name;
                            });
                        }

                        function codeFromConcept(concept, system) {
                            const coding = (concept && concept.coding) || [];
                            const match = coding.find(function (c) {
                                return c && c.system === system && c.code;
                            });
                            return match ? match.code : "-";
                        }

                        function extractIssues(outcome) {
                            const issues = [];
                            const rawIssues = (outcome && outcome.issue) || [];
                            rawIssues.forEach(function (item) {
                                const detailCoding = ((item.details && item.details.coding) || []).map(function (c) {
                                    return c && c.code ? c.code : null;
                                }).filter(Boolean);

                                issues.push({
                                    severity: item.severity || "-",
                                    code: item.code || "-",
                                    detailsText: (item.details && item.details.text) || "-",
                                    detailsCode: detailCoding.length > 0 ? detailCoding.join(", ") : "-"
                                });
                            });
                            return issues;
                        }

                        function renderIssues(issues) {
                            ids.issues.innerHTML = "";
                            if (!issues || issues.length === 0) {
                                const empty = document.createElement("div");
                                empty.className = "empty";
                                empty.textContent = "No issues.";
                                ids.issues.appendChild(empty);
                                return;
                            }

                            issues.forEach(function (issue) {
                                const row = document.createElement("div");
                                row.className = "list-item";
                                row.textContent = "severity=" + issue.severity
                                    + " | code=" + issue.code
                                    + " | details.text=" + issue.detailsText
                                    + " | details.coding.code=" + issue.detailsCode;
                                ids.issues.appendChild(row);
                            });
                        }

                        function renderCandidates(candidates) {
                            ids.candidates.innerHTML = "";
                            if (!candidates || candidates.length === 0) {
                                const empty = document.createElement("div");
                                empty.className = "empty";
                                empty.textContent = "No candidates.";
                                ids.candidates.appendChild(empty);
                                return;
                            }

                            candidates.forEach(function (candidate) {
                                const row = document.createElement("div");
                                row.className = "list-item";
                                row.textContent = "lot=" + candidate.lot
                                    + " | CVX=" + candidate.cvx
                                    + " | MVX=" + candidate.mvx
                                    + " | confidence=" + candidate.confidence
                                    + " | matchType=" + candidate.matchType
                                    + " | summary=" + candidate.summary;
                                ids.candidates.appendChild(row);
                            });
                        }

                        function renderFromParameters(resource) {
                            const params = resource.parameter || [];
                            const resolutionStatus = findParameter(params, "resolutionStatus");
                            const confidence = findParameter(params, "confidence");
                            const summary = findParameter(params, "summary");
                            const resolved = findParameter(params, "resolvedImmunization");
                            const outcomeParam = findParameter(params, "outcome");
                            const candidateParams = findParameters(params, "candidate");

                            const resolvedImm = resolved && resolved.resource ? resolved.resource : null;
                            ids.resolutionStatus.textContent = resolutionStatus ? (resolutionStatus.valueCode || "-") : "-";
                            ids.resolvedLot.textContent = resolvedImm && resolvedImm.lotNumber ? resolvedImm.lotNumber : "-";
                            ids.resolvedCvx.textContent = resolvedImm ? codeFromConcept(resolvedImm.vaccineCode, CVX_SYSTEM) : "-";
                            ids.resolvedMvx.textContent = resolvedImm
                                && resolvedImm.manufacturer
                                && resolvedImm.manufacturer.identifier
                                && resolvedImm.manufacturer.identifier.value
                                ? resolvedImm.manufacturer.identifier.value
                                : "-";
                            ids.confidence.textContent = confidence && confidence.valueDecimal !== undefined
                                ? String(confidence.valueDecimal)
                                : "-";
                            ids.summary.textContent = summary ? (summary.valueString || "-") : "-";

                            const issues = extractIssues(outcomeParam && outcomeParam.resource);
                            renderIssues(issues);

                            const candidates = candidateParams.map(function (p) {
                                const immunizationPart = findPart(p.part, "immunization");
                                const confidencePart = findPart(p.part, "confidence");
                                const matchTypePart = findPart(p.part, "matchType");
                                const summaryPart = findPart(p.part, "summary");
                                const imm = immunizationPart ? immunizationPart.resource : null;
                                return {
                                    lot: imm && imm.lotNumber ? imm.lotNumber : "-",
                                    cvx: imm ? codeFromConcept(imm.vaccineCode, CVX_SYSTEM) : "-",
                                    mvx: imm
                                        && imm.manufacturer
                                        && imm.manufacturer.identifier
                                        && imm.manufacturer.identifier.value
                                        ? imm.manufacturer.identifier.value
                                        : "-",
                                    confidence: confidencePart && confidencePart.valueDecimal !== undefined
                                        ? String(confidencePart.valueDecimal)
                                        : "-",
                                    matchType: matchTypePart ? (matchTypePart.valueCode || "-") : "-",
                                    summary: summaryPart ? (summaryPart.valueString || "-") : "-"
                                };
                            });

                            renderCandidates(candidates);
                        }

                        function renderFromOperationOutcome(resource) {
                            ids.resolutionStatus.textContent = "-";
                            ids.resolvedLot.textContent = "-";
                            ids.resolvedCvx.textContent = "-";
                            ids.resolvedMvx.textContent = "-";
                            ids.confidence.textContent = "-";

                            const issues = extractIssues(resource);
                            renderIssues(issues);
                            renderCandidates([]);

                            if (issues.length > 0 && issues[0].detailsText && issues[0].detailsText !== "-") {
                                ids.summary.textContent = issues[0].detailsText;
                            } else {
                                ids.summary.textContent = "OperationOutcome returned without detailed text.";
                            }
                        }

                        function renderResponse(resource) {
                            if (!resource || typeof resource !== "object") {
                                setMessage("Response body is not valid JSON.", "warn");
                                return;
                            }

                            if (resource.resourceType === "Parameters") {
                                renderFromParameters(resource);
                                return;
                            }

                            if (resource.resourceType === "OperationOutcome") {
                                renderFromOperationOutcome(resource);
                                return;
                            }

                            setMessage("Unexpected FHIR resourceType: " + (resource.resourceType || "unknown"), "warn");
                        }

                        async function submitRequest() {
                            const serviceUrl = valueOf(ids.serviceUrl);
                            const lotNumber = valueOf(ids.lotNumber);

                            if (!serviceUrl) {
                                setMessage("Service URL is required.", "err");
                                return;
                            }
                            if (!lotNumber) {
                                setMessage("Lot Number is required.", "err");
                                return;
                            }

                            clearRenderedData();

                            const requestPayload = buildRequestPayload();
                            const requestJson = JSON.stringify(requestPayload, null, 2);
                            ids.rawRequest.textContent = requestJson;
                            ids.rawRequest.classList.remove("empty");

                            ids.submitBtn.disabled = true;
                            setMessage("Submitting request...", "warn");

                            try {
                                const response = await fetch(serviceUrl, {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/fhir+json",
                                        "Accept": "application/fhir+json"
                                    },
                                    body: requestJson
                                });

                                const bodyText = await response.text();
                                const pretty = prettyJson(bodyText);
                                const prefix = "HTTP " + response.status + " " + response.statusText + "\n\n";
                                ids.rawResponse.textContent = prefix + (pretty || "(empty body)");
                                ids.rawResponse.classList.remove("empty");

                                let parsed = null;
                                try {
                                    parsed = bodyText ? JSON.parse(bodyText) : null;
                                } catch (error) {
                                    parsed = null;
                                }

                                if (parsed) {
                                    renderResponse(parsed);
                                } else {
                                    ids.summary.textContent = response.ok
                                        ? "Response could not be parsed as JSON."
                                        : "HTTP error response body was not valid JSON.";
                                    renderIssues([]);
                                    renderCandidates([]);
                                }

                                if (response.ok) {
                                    setMessage("Request completed successfully.", "ok");
                                } else {
                                    setMessage("Request failed with HTTP " + response.status + ".", "err");
                                }
                            } catch (error) {
                                ids.rawResponse.textContent = "Request failed: " + (error && error.message ? error.message : String(error));
                                ids.rawResponse.classList.remove("empty");
                                setMessage("Network or fetch error occurred while calling the service.", "err");
                                ids.summary.textContent = "Request failed before a FHIR response was received.";
                                renderIssues([]);
                                renderCandidates([]);
                            } finally {
                                ids.submitBtn.disabled = false;
                            }
                        }

                        function loadExample(name) {
                            const presets = {
                                exact: { lotNumber: "TY00T5", cvx: "208" },
                                suffix: { lotNumber: "TY00T5 Priv", cvx: "208" },
                                ozero: { lotNumber: "TYYOOT5", cvx: "208" },
                                conflict: { lotNumber: "TY00T5", cvx: "999" },
                                unknown: { lotNumber: "UNKNOWN123", cvx: "208" },
                                multiple: { lotNumber: "ABC123", cvx: "208" }
                            };
                            const preset = presets[name] || {};
                            setFields(Object.assign({}, defaults, preset));
                            setMessage("Loaded " + name + " example.", "ok");
                        }

                        document.getElementById("submitBtn").addEventListener("click", submitRequest);
                        document.getElementById("exactBtn").addEventListener("click", function () { loadExample("exact"); });
                        document.getElementById("suffixBtn").addEventListener("click", function () { loadExample("suffix"); });
                        document.getElementById("ozeroBtn").addEventListener("click", function () { loadExample("ozero"); });
                        document.getElementById("conflictBtn").addEventListener("click", function () { loadExample("conflict"); });
                        document.getElementById("unknownBtn").addEventListener("click", function () { loadExample("unknown"); });
                        document.getElementById("multipleBtn").addEventListener("click", function () { loadExample("multiple"); });
                        document.getElementById("clearBtn").addEventListener("click", function () {
                            setFields({
                                serviceUrl: defaults.serviceUrl,
                                lotNumber: "",
                                cvx: "",
                                mvx: "",
                                ndc: "",
                                administrationDate: "",
                                productText: ""
                            });
                            ids.rawRequest.textContent = "No request generated yet.";
                            ids.rawRequest.className = "empty";
                            ids.rawResponse.textContent = "No response received yet.";
                            ids.rawResponse.className = "empty";
                            clearRenderedData();
                            setMessage("Cleared inputs and results.");
                        });

                        setFields(defaults);
                        clearRenderedData();
                    })();
                </script>
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
