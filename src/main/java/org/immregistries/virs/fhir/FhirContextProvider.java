package org.immregistries.virs.fhir;

import ca.uhn.fhir.context.FhirContext;

/**
 * Singleton provider for the shared HAPI FHIR R4 context.
 * FhirContext creation is expensive; use this class to obtain the single shared
 * instance.
 */
public final class FhirContextProvider {

    private static final FhirContext INSTANCE = FhirContext.forR4();

    private FhirContextProvider() {
        // prevent instantiation
    }

    /**
     * Returns the shared HAPI FHIR R4 {@link FhirContext}.
     */
    public static FhirContext getContext() {
        return INSTANCE;
    }
}
