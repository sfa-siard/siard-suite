# local-web-archive-wizard Specification

## Purpose

Gives the local web app's archive workflow the desktop app's wizard frame — a step indicator on top and a consistent bottom action bar — so the flow reads as guided steps and later archive steps can plug into the same chrome.

## Requirements

### Requirement: Wizard frame on archive pages

The `/archive` and `/archive/connection` routes SHALL render inside a shared wizard frame consisting of a step indicator at the top and a bottom action bar. Routes outside the archive workflow (`/`, `/about`, `/launch-failed`) SHALL NOT show the frame. The frame SHALL be localized in de, fr, it and en and remain usable at 360 CSS pixels width without horizontal page scrolling.

#### Scenario: Frame shown on archive steps
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive` or `/archive/connection?dbms=postgresql`
- **THEN** a step indicator is shown at the top of the page
- **AND** a bottom action bar with navigation controls is shown below the step content

#### Scenario: Frame absent outside the archive workflow
- **GIVEN** a valid local session
- **WHEN** the user visits `/` or `/about`
- **THEN** no archive step indicator and no wizard action bar is shown

#### Scenario: Narrow viewport
- **GIVEN** a valid local session and a 360 by 800 CSS-pixel viewport
- **WHEN** the user visits `/archive` or `/archive/connection?dbms=postgresql`
- **THEN** the step indicator and the action bar remain visible and reachable
- **AND** the page scroll width is no greater than the viewport width

#### Scenario: Missing session keeps existing behaviour
- **GIVEN** the agent is running and no valid session exists
- **WHEN** the user visits `/archive`
- **THEN** the page shows the session-missing message as before
- **AND** no connection data is displayed

### Requirement: Step indicator

The step indicator SHALL list the archive workflow's implemented steps in order, each with a short title in the interface language, and SHALL mark the current step as active and earlier steps as completed. The step sequence SHALL be defined once so later archive steps can be appended without restructuring the indicator. The implemented steps are "Select connection" (`/archive`) and "Connection details" (`/archive/connection`).

#### Scenario: First step active
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive`
- **THEN** the indicator lists "Select connection" and "Connection details" in that order
- **AND** "Select connection" is marked as the current step
- **AND** no step is marked as completed

#### Scenario: Second step active
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive/connection?dbms=postgresql`
- **THEN** "Connection details" is marked as the current step
- **AND** "Select connection" is marked as completed

#### Scenario: Titles follow the interface language
- **GIVEN** a browser whose preferred language is one of de, fr, it, en
- **WHEN** an archive wizard page loads
- **THEN** the step titles are shown in that language
- **AND** no raw translation key is displayed

#### Scenario: Indicator does not depend on session data
- **GIVEN** the agent is running and no valid session exists
- **WHEN** the user visits `/archive`
- **THEN** the step indicator still shows both step titles in the interface language

### Requirement: Bottom action bar and navigation

Every archive wizard step SHALL show a bottom action bar with Cancel and, where the step defines them, Back and the step's primary action. Back SHALL navigate to the previous step (`/archive/connection` to `/archive`, `/archive` to `/`). The primary action SHALL keep the step's existing validation and forward behaviour. Cancel SHALL open a confirmation dialog; confirming SHALL navigate to `/`, and dismissing SHALL keep the current step.

#### Scenario: Details step action bar
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive/connection?dbms=postgresql`
- **THEN** the action bar shows Back, the form's submit action and Cancel
- **AND** no Next-style button is shown

#### Scenario: Primary action keeps step validation
- **GIVEN** the details step with a required field empty
- **WHEN** the user activates the action bar's submit action
- **THEN** inline validation messages appear on the invalid fields
- **AND** no save request is sent

#### Scenario: Back navigates to the previous step
- **GIVEN** the user is on `/archive/connection?dbms=postgresql`
- **WHEN** the user activates Back in the action bar
- **THEN** the route becomes `/archive`
- **WHEN** the user then activates Back on `/archive`
- **THEN** the route becomes `/`

#### Scenario: Cancel asks for confirmation and leaves on confirm
- **GIVEN** the user is on `/archive` or `/archive/connection`
- **WHEN** the user activates Cancel and confirms the dialog
- **THEN** the route becomes `/`

#### Scenario: Dismissed cancel keeps the step
- **GIVEN** the user is on `/archive/connection?dbms=postgresql`
- **WHEN** the user activates Cancel and dismisses the confirmation dialog
- **THEN** the route stays `/archive/connection` with its query parameters
- **AND** the entered form values are unchanged

#### Scenario: Wizard navigation needs no session
- **GIVEN** the agent is running and no valid session exists
- **WHEN** the user visits `/archive`, activates Cancel and confirms
- **THEN** the route becomes `/`
