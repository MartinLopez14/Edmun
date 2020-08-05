package com.springvuegradle.team6.security;

import com.springvuegradle.team6.models.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;
import java.util.Collection;

public class UserSecurityService {

    private UserSecurityService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check if user is authorised to access requested user information.
     *
     * @param requestId  user id under query
     * @param session    Http session
     * @param repository profile repository
     * @return ResponseEntity will return 401 if user is not logged in or user is not authorized to edit the requested user's information,
     * or 404 if requested user's profile is not found, else it will return null if user is authorized
     */
    public static ResponseEntity<String> checkAuthorised(Integer requestId, HttpSession session, ProfileRepository repository) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        if (!(id.toString().equals(requestId.toString()))) {
            Collection<SimpleGrantedAuthority> userRoles = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            boolean isAdmin = userRoles.stream().anyMatch(simpleGrantedAuthority -> (simpleGrantedAuthority.getAuthority().equals("ROLE_ADMIN") || simpleGrantedAuthority.getAuthority().equals("ROLE_USER_ADMIN")));
            if (!isAdmin) {
                return new ResponseEntity<>("Only accessible by the owner", HttpStatus.UNAUTHORIZED);
            }
        }

        if (!repository.existsById(requestId)) {
            return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
        }
        return null;
    }

    /**
     * Check if user is authorised to view requested user information.
     *
     * @param requestId  user id under query
     * @param session    Http session
     * @param repository profile repository
     * @return ResponseEntity will return 401 if user is not logged in
     * or 404 if requested user's profile is not found, else it will return null if user is authorized
     */
    public static ResponseEntity<String> canViewPermission(Integer requestId, HttpSession session, ProfileRepository repository) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }
        if (!repository.existsById(requestId)) {
            return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
        }
        return null;
    }
}
