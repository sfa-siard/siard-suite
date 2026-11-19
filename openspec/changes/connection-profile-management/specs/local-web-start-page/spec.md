# Spec Delta

## ADDED Requirements

### Requirement: Archive workflow entry point

The Archive workflow control on the start page SHALL be an accessible, named button that navigates to the `/archive` connection picker route when activated by pointer or keyboard.

#### Scenario: Pointer activation navigates
- **GIVEN** the start page and a valid local session
- **WHEN** the user clicks the Archive button
- **THEN** the route becomes `/archive`

#### Scenario: Keyboard activation navigates
- **GIVEN** the Archive button has keyboard focus
- **WHEN** the user activates it with Enter or Space
- **THEN** the route becomes `/archive`

#### Scenario: Direct visit shows the picker
- **GIVEN** a valid local session
- **WHEN** the user requests `/archive` directly or reloads it
- **THEN** the connection picker page is displayed
- **AND** the existing SIARD Suite header and Info navigation remain visible

## MODIFIED Requirements

### Requirement: Presentation-only workflow controls

The Open, Upload and Export workflow controls SHALL be accessible, named buttons with no workflow action. Activating them SHALL NOT navigate, open dialogs or file pickers, download files, or issue workflow API requests. Existing Info navigation and agent lifecycle behavior SHALL remain unchanged.

#### Scenario: Pointer activation is inert
- **GIVEN** the start page and a valid local session
- **WHEN** the user clicks each of Open, Upload and Export
- **THEN** the route remains `/`, no dialog or file picker opens and no download starts
- **AND** no workflow API request is sent

#### Scenario: Keyboard activation is inert
- **GIVEN** a workflow button has keyboard focus
- **WHEN** the user activates Open, Upload or Export with Enter or Space
- **THEN** the route remains `/` and no workflow starts
- **AND** no dialog, file picker, download or workflow API request occurs

#### Scenario: Activation discloses no local information
- **GIVEN** a valid local session and a network-request recorder
- **WHEN** the user activates all four workflow buttons
- **THEN** no filesystem paths or database credentials are requested or displayed
- **AND** no launch token is added to the address bar or request payloads

#### Scenario: Existing navigation and lifecycle survive
- **GIVEN** the start page was opened through the local agent's launch link
- **WHEN** the user follows Info
- **THEN** the existing About page is displayed at `/about`
- **AND** the agent connection remains active while that application page is open
