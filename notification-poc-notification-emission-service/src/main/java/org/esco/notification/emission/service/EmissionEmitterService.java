package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.emission.exception.EmissionEmitException;

/**
 * Validate and emit event
 */
public interface EmissionEmitterService {
    void emit(Emission emission) throws EmissionEmitException;
}
