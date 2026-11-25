# Spec Delta

## MODIFIED Requirements

### Requirement: Presentation-only workflow controls

The Upload and Export workflow controls SHALL be accessible, named buttons with no workflow action. Activating them SHALL NOT navigate, open dialogs or file pickers, download files, or issue workflow API requests. Existing Info navigation and agent lifecycle behavior SHALL remain unchanged.

#### Scenario: Pointer activation is inert
- **GIVEN** the start page and a valid local session
- **WHEN** the user clicks each of Upload and Export
- **THEN** the route remains `/`, no dialog or file picker opens and no download starts
- **AND** no workflow API request is sent

#### Scenario: Keyboard activation is inert
- **GIVEN** a workflow button has keyboard focus
- **WHEN** the user activates Upload or Export with Enter or Space
- **THEN** the route remains `/` and no workflow starts
- **AND** no dialog, file picker, download or workflow API request occurs

#### Scenario: Activation discloses no local information
- **GIVEN** a valid local session and a network-request recorder
- **WHEN** the user activates the Upload and Export buttons
- **THEN** no filesystem paths or database credentials are requested or displayed
- **AND** no launch token is added to the address bar or request payloads

#### Scenario: Existing navigation and lifecycle survive
- **GIVEN** the start page was opened through the local agent's launch link
- **WHEN** the user follows Info
- **THEN** the existing About page is displayed at `/about`
- **AND** the agent connection remains active while that application page is open

## ADDED Requirements

### Requirement: Open workflow entry point

The Open workflow control on the start page SHALL be an accessible, named button that, activated by pointer or keyboard, opens the file chooser in open mode offering only `.siard` files. Cancelling SHALL leave the route at `/`.

#### Scenario: Activation opens the chooser
- **GIVEN** the start page and a valid local session
- **WHEN** the user clicks Open, or activates it with Enter or Space
- **THEN** the file chooser opens in open mode in the home folder, listing only folders and `.siard` files

#### Scenario: Cancel returns to the start page
- **GIVEN** the chooser opened from Open
- **WHEN** the user cancels it
- **THEN** the route remains `/`, no archive is opened and focus returns to the Open button

#### Scenario: Open without a session
- **GIVEN** the start page whose session is no longer valid
- **WHEN** the user activates Open
- **THEN** the chooser shows an error for the 401 answer and lists no entries
- **AND** no launch token appears in the address bar or in any request URL or payload
