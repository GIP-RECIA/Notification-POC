package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.emission.exception.EmissionEmitException;

/**
 * Validates and emit {@link org.esco.notification.data.Event}.
 */
public interface EmissionEmitterService {
    void emit(Emission emission) throws EmissionEmitException;
}
