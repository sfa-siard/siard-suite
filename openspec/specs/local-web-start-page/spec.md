# local-web-start-page Specification

## Purpose

Provide the local SIARD Suite web app with a responsive, localized overview of its four workflows, retaining the JavaFX start page's visual relationships without executing those workflows.

## Requirements

### Requirement: Responsive workflow overview

The local start route SHALL show the Archive, Open, Upload and Export workflow overview using the JavaFX screenshot's artwork, blue buttons, grey arrows, emphasized description verbs and central SIARD illustration. It SHALL preserve the existing web header and navigation, and rearrange vertically on narrow viewports without horizontal page scrolling. Text SHALL follow the existing interface-language selection.

#### Scenario: Desktop composition
- **GIVEN** an English browser at a 1280 by 800 CSS-pixel viewport
- **WHEN** the user visits `/` through a valid local launch
- **THEN** the source database and Archive button are left of the central SIARD illustration
- **AND** Upload and its database destination are above Export and its table-file destination on the right
- **AND** Open and the Display description are below the central illustration
- **AND** the descriptions read "Archive database and save in SIARD format.", "Display contents of a SIARD file." and "Upload contents of a SIARD file to a database or export data to a table."
- **AND** Archive, Display, Upload and export are emphasized within their descriptions
- **AND** the SIARD illustration has a shadow, the buttons are blue and neutral artwork and arrows are grey
- **AND** the existing SIARD Suite home and Info navigation links remain visible

#### Scenario: Narrow localized composition
- **GIVEN** a 360 by 800 CSS-pixel viewport
- **WHEN** the start page loads in each of de, fr, it and en
- **THEN** the overview uses a vertical arrangement instead of the desktop left-to-right composition
- **AND** all four buttons and all descriptions remain readable and reachable by vertical scrolling
- **AND** the page scroll width is no greater than its viewport width
- **AND** labels and descriptions are in the selected language without raw translation keys

#### Scenario: Untrusted query text is not page content
- **GIVEN** a valid local session
- **WHEN** the user visits `/?title=%3Cscript%3Ealert(1)%3C%2Fscript%3E`
- **THEN** the overview still shows its translated labels and descriptions
- **AND** the supplied title is neither inserted into the overview nor executed

### Requirement: Workflow visual feedback

Hovering or keyboard-focusing a workflow button SHALL highlight its associated artwork red. Archive, Upload and Export SHALL animate a dot from source to destination along their arrow. Open SHALL highlight only the central SIARD illustration. With no hovered or focused action, artwork SHALL return to neutral and dots SHALL be hidden. Reduced-motion preference SHALL suppress dot motion without removing highlighting.

#### Scenario: Hover action mappings
- **GIVEN** the start page is shown with no reduced-motion preference
- **WHEN** the pointer enters each action button in turn, leaving the previous button first
- **THEN** Archive highlights the source database and central SIARD illustration and animates its incoming-arrow dot
- **AND** Upload highlights the central SIARD illustration and destination database and animates its outgoing-arrow dot
- **AND** Export highlights the central SIARD illustration and table-file destination and animates its outgoing-arrow dot
- **AND** Open highlights only the central SIARD illustration and shows no dot
- **AND** artwork unrelated to the active action remains neutral

#### Scenario: Reset after interaction
- **GIVEN** an action has hover or keyboard-focus feedback
- **WHEN** neither the pointer nor keyboard focus remains on any action button
- **THEN** all artwork returns to neutral
- **AND** all dots are hidden and their animations stop

#### Scenario: Keyboard and reduced-motion feedback
- **GIVEN** the browser requests reduced motion
- **WHEN** the user tabs through the four workflow buttons
- **THEN** each button has a visible keyboard-focus indicator and the same red artwork mapping as hover
- **AND** no moving dot is displayed

#### Scenario: Feedback does not access local data
- **GIVEN** a valid local session and a network-request recorder
- **WHEN** the user hovers or focuses each workflow button
- **THEN** feedback issues no workflow API requests and opens no file picker
- **AND** feedback displays no filesystem paths, database credentials or launch tokens

### Requirement: Presentation-only workflow controls

The four workflow controls SHALL be accessible, named buttons with no workflow action in this change. Activating them SHALL NOT navigate, open dialogs or file pickers, download files, or issue workflow API requests. Existing Info navigation and agent lifecycle behavior SHALL remain unchanged.

#### Scenario: Pointer activation is inert
- **GIVEN** the start page and a valid local session
- **WHEN** the user clicks each of Archive, Open, Upload and Export
- **THEN** the route remains `/`, no dialog or file picker opens and no download starts
- **AND** no workflow API request is sent

#### Scenario: Keyboard activation is inert
- **GIVEN** a workflow button has keyboard focus
- **WHEN** the user activates it with Enter or Space
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
