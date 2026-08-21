# ADR-0002: Web Companion Authentication and Authorization

## Status

Accepted

## Context

The Web Companion Central Hub is a multi-user service handling sensitive
archival data. It needs authentication and authorization. SFA staff will
authenticate through the federal EIAM identity solution, while other
deployments may use different corporate identity providers. The application
code should not hard-code any one provider.

The Local Agent runs as a single-user service on the user's own machine and
must be deployable with zero configuration.

## Decision

- **Hub mode**: the hub authenticates users through **Keycloak** using OIDC.
  Keycloak is the primary user directory and may federate to external identity
  providers such as EIAM, Microsoft Entra ID, or any OIDC/SAML provider. The hub
  itself does not talk directly to EIAM or other IdPs.
- **Local agent mode**: no authentication. The agent binds to `127.0.0.1` only
  and serves its own frontend bundle.

Authorization is role-based. Keycloak users or groups are mapped to internal
SIARD roles:

- `ARCHIVE` — submit archive jobs.
- `RESTORE` — submit restore jobs.
- `ADMIN` — manage all jobs and users within the hub.

Keycloak administrators assign roles. Until a role is assigned, a user can log
in but sees only a read-only "access pending" page.

User identity uses the stable **Keycloak user ID** (UUID) internally. Email and
username are display attributes only.

The React SPA uses the **authorization code flow with PKCE**. The hub backend
holds the refresh token in an `httpOnly` cookie and validates access tokens
offline with Keycloak's JWKS endpoint. Token lifetimes are configurable with
suggested defaults of 15 minutes for access tokens and 8 hours for refresh
tokens.

Logout uses front-channel logout for user-initiated logout and backchannel
logout when Keycloak revokes the session.

Multi-factor authentication is not implemented in the hub; it is delegated to
Keycloak and upstream identity providers.

## Consequences

- **Positive**: provider-agnostic application code; IT can swap or add IdPs
  through Keycloak configuration; single-user agent stays simple.
- **Negative**: operating a Keycloak realm is required for hub deployments;
  role assignment is a manual admin step.
- **Neutral**: MFA policy is owned by the deployer, not the application.

## Alternatives

- **Direct OIDC/SAML to EIAM in the hub** — rejected because it hard-codes the
  SFA provider and makes the hub unusable for other deployments.
- **API keys for hub authentication** — rejected because it shifts user
  management to IT and does not integrate with EIAM.
- **Local agent with authentication** — rejected because it would force users to
  obtain and manage credentials for a single-user local tool.
