package com.clinic.util;

import com.clinic.entity.Role;
import com.clinic.entity.User;
import org.springframework.security.access.AccessDeniedException;

/**
 * Utility class for common authorization checks across services.
 * Centralizes authorization logic to reduce duplication and improve maintainability.
 */
public class AuthorizationUtil {

    private AuthorizationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates that the current user has permission to access or modify a resource.
     * Admins always have access; other users can only access their own resources.
     *
     * @param currentUser the authenticated user making the request
     * @param resourceOwnerEmail the email of the resource owner
     * @throws AccessDeniedException if the user lacks permission
     */
    public static void validateUserAccess(User currentUser, String resourceOwnerEmail) {
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getEmail().equals(resourceOwnerEmail)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }

    /**
     * Checks if a user is an admin.
     *
     * @param user the user to check
     * @return true if user is admin, false otherwise
     */
    public static boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    /**
     * Checks if a user is a doctor.
     *
     * @param user the user to check
     * @return true if user is doctor, false otherwise
     */
    public static boolean isDoctor(User user) {
        return user.getRole() == Role.DOCTOR;
    }

    /**
     * Checks if a user is a patient.
     *
     * @param user the user to check
     * @return true if user is patient, false otherwise
     */
    public static boolean isPatient(User user) {
        return user.getRole() == Role.PATIENT;
    }
}
