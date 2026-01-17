package com.lunanotes.security;

import com.lunanotes.util.UserRole;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/users/{userId}");

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userIdFromRequestUri = uriVariables.get("userId");

        Authentication authentication = authenticationSupplier.get();
        String userIdFromJwt = ((Jwt) authentication.getPrincipal()).getClaim("userId").toString();

        boolean hasUserRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + UserRole.USER.name()));

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + UserRole.ADMIN.name()));

        //boolean userIdsMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(userIdFromJwt);

        //System.out.println(hasUserRole + " " +hasAdminRole + " " + userIdsMatch + "\n"+userIdFromRequestUri+" "+userIdFromJwt);

        return new AuthorizationDecision(hasAdminRole || (hasUserRole/* && userIdsMatch*/));
    }

}
