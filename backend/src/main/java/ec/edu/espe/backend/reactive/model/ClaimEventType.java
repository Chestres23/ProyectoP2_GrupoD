package ec.edu.espe.backend.reactive.model;

/**
 * Tipos de eventos emitidos al stream SSE de reclamos.
 */
public enum ClaimEventType {
    CREATED,
    APPROVED,
    REJECTED,
    DELETED
}
