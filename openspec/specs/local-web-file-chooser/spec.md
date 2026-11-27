# local-web-file-chooser Specification

## Purpose

Lets the user of the local web app choose a file to open, a file to save to, or a folder on their own computer, read by the local agent in place, on Windows, macOS and Linux.

## Requirements

### Requirement: Places

`GET /api/files/places` SHALL return 200 with the user's starting places, each with `kind` and absolute `path` and no path twice: the home folder, the Desktop, Documents and Downloads folders that exist, and the file-system roots (drive letters on Windows; `/` and each volume under `/Volumes` on macOS; `/` and mounted media on Linux). It SHALL answer within a few seconds even when a folder lies on an unreachable share.

#### Scenario: Places of the current user
- **GIVEN** a valid local session and a home folder containing a `Documents` folder
- **WHEN** the client requests `GET /api/files/places`
- **THEN** the agent returns 200 with an entry of kind `home` whose `path` is the home folder
- **AND** an entry of kind `documents` whose `path` is that `Documents` folder
- **AND** at least one entry of kind `root`

#### Scenario: Missing standard folder is left out
- **GIVEN** a valid local session and no `Downloads` folder for the user
- **WHEN** the client requests `GET /api/files/places`
- **THEN** the agent returns 200 and no entry has kind `downloads`
- **AND** a Documents folder redirected to an unreachable network share (e.g. `\\nas\home\Documents`) is returned without being checked, and the response arrives within 3 seconds
- **AND** a folder name with non-ASCII characters (e.g. `C:\Users\Zoë\Données`) is returned unchanged

#### Scenario: Places need a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it requests `GET /api/files/places`
- **THEN** the agent returns 401 with problem details and no path in the body

### Requirement: Folder listing

`GET /api/files?path=<absolute folder>` SHALL return 200 with the folder's normalised `path`, its `parent` (null at a root) and its `entries`, each with `name`, absolute `path`, `folder`, `size` (files only), `modified` and `hidden`. Folders SHALL come first, then files, each sorted by name ignoring case.

#### Scenario: Folder with files and subfolders
- **GIVEN** a valid local session and a folder containing `b.siard`, `A.siard` and a subfolder `data`
- **WHEN** the client requests `GET /api/files?path=<that folder>`
- **THEN** the agent returns 200 with entries in the order `data`, `A.siard`, `b.siard`
- **AND** `data` has `folder` true and `A.siard` has `folder` false and its size in bytes

#### Scenario: Root has no parent
- **GIVEN** a valid local session
- **WHEN** the client lists a root returned by `GET /api/files/places`
- **THEN** the agent returns 200 with `parent` null

#### Scenario: Invalid or unusable folder path
- **GIVEN** a valid local session
- **WHEN** the client lists a relative path, a path that does not exist, an existing file, or a folder the user may not read
- **THEN** the agent returns 400, 404, 400 or 403 respectively with problem details
- **AND** a listing that has not finished after 10 seconds (e.g. an unreachable network share) returns 504 with problem details
- **AND** while all 8 listing workers are still waiting on earlier unfinished listings, a further listing returns 503 at once with problem details
- **AND** neither response carries a `Retry-After` header

#### Scenario: Listing needs a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it requests `GET /api/files?path=<home folder>`
- **THEN** the agent returns 401 and no entry is listed

### Requirement: Listing filters

The listing SHALL omit hidden entries unless `hidden=true`, SHALL omit files unless their name ends with one of the given `extension` parameters (ignoring case; no parameter keeps all files), and SHALL omit all files when `foldersOnly=true`. Folders SHALL be kept by the extension filter.

#### Scenario: Extension filter keeps folders
- **GIVEN** a valid local session and a folder with `a.SIARD`, `notes.txt` and a subfolder `old`
- **WHEN** the client requests `GET /api/files?path=<folder>&extension=.siard`
- **THEN** the entries are exactly `old` and `a.SIARD`

#### Scenario: Hidden entries and folders only
- **GIVEN** a valid local session and a folder with a hidden subfolder, a visible subfolder and a file
- **WHEN** the client lists it with `foldersOnly=true`, then with `foldersOnly=true&hidden=true`
- **THEN** the first listing contains only the visible subfolder
- **AND** the second listing contains both subfolders and no file

#### Scenario: Filtered listing needs a session
- **GIVEN** a request with an invalid session cookie
- **WHEN** it requests `GET /api/files?path=<folder>&extension=.siard`
- **THEN** the agent returns 401

### Requirement: Large folders

A listing SHALL contain at most 10000 entries after filtering and SHALL set `truncated` to true when entries were left out, false otherwise.

#### Scenario: Normal folder is complete
- **GIVEN** a valid local session and a folder with 3 entries
- **WHEN** the client lists it
- **THEN** the response has 3 entries and `truncated` false

#### Scenario: Oversized folder is cut
- **GIVEN** a valid local session and a folder with 10001 files
- **WHEN** the client lists it
- **THEN** the response has 10000 entries and `truncated` true

#### Scenario: Large listing needs a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it lists a folder with 10001 files
- **THEN** the agent returns 401

### Requirement: Entry lookup

`GET /api/files/entry?path=<absolute path>` SHALL return 200 with the entry's normalised `path`, `name`, `parent`, `folder`, `size`, `modified`, `hidden` and `writable` (whether the user may write the file, or create files in the folder), or 404 when nothing exists at the path.

#### Scenario: Existing file
- **GIVEN** a valid local session and an existing file `/data/db.siard` (or `C:\data\db.siard`)
- **WHEN** the client requests the entry for that path
- **THEN** the agent returns 200 with `folder` false, `name` `db.siard` and `parent` the containing folder

#### Scenario: Missing or malformed path
- **GIVEN** a valid local session
- **WHEN** the client requests the entry for a path that does not exist, then for a relative path or a path containing a NUL character
- **THEN** the agent returns 404, then 400, each with problem details

#### Scenario: Lookup needs a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it requests the entry for an existing file
- **THEN** the agent returns 401 and the body does not say whether the file exists

### Requirement: Show in file browser

`POST /api/files/reveal` with `{"path": <absolute path>}` SHALL open the system file manager on the user's desktop and answer 204: an existing file is shown selected in its folder, an existing folder is opened (on macOS shown selected in its parent folder, because opening a bundle folder such as an `.app` would run it). Arguments SHALL be passed without a command shell.

#### Scenario: Reveal a file
- **GIVEN** a valid local session and an existing file
- **WHEN** the client posts its path to `/api/files/reveal`
- **THEN** the agent answers 204 and starts the platform's file manager for that file (Explorer from the Windows folder with the file selected on Windows, Finder with the file selected on macOS, the desktop's file manager on Linux)

#### Scenario: Reveal a folder
- **GIVEN** a valid local session and an existing folder
- **WHEN** the client posts its path to `/api/files/reveal`
- **THEN** the agent answers 204 and opens that folder in the platform's file manager (Explorer on Windows, Finder with the folder selected in its parent on macOS, on Linux the desktop's file manager asked over D-Bus like for a file, with `xdg-open` only when that fails)

#### Scenario: Reveal fails safely
- **GIVEN** a valid local session
- **WHEN** the client posts a path that does not exist, a relative path, or a path like `/tmp/x; rm -rf ~`
- **THEN** the agent answers 404, 400 or 404 respectively with problem details and starts no process
- **AND** when no file manager can be started for an existing path, or the opener (`open`, `xdg-open`) exits with a non-zero code within 2 seconds, the agent answers 503 with problem details

#### Scenario: Reveal needs a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it posts an existing path to `/api/files/reveal`
- **THEN** the agent answers 401 and starts no process

### Requirement: Chooser modes

The local frontend SHALL provide a file chooser in open, save and folder mode that returns one absolute path or nothing when cancelled. Open SHALL accept only an existing file matching one of the extensions it is given; save SHALL be given exactly one extension (the format the workflow writes), take a folder and a file name, and append that extension unless the name already ends with it in any letter case; folder SHALL return the folder shown.

#### Scenario: Open an archive file
- **GIVEN** the chooser in open mode with extension `.siard`, showing a folder with `a.siard` and `b.txt`
- **WHEN** the user selects `a.siard` and confirms
- **THEN** the chooser returns the absolute path of `a.siard`
- **AND** `b.txt` was not listed

#### Scenario: Save with confirmation and folder choice
- **GIVEN** the chooser in save mode with extension `.siard`, showing a writable folder that contains `db.siard`
- **WHEN** the user enters `db` and confirms
- **THEN** the chooser asks whether to replace `db.siard` and returns its path only after the user agrees
- **AND** in folder mode, confirming returns the absolute path of the folder shown

#### Scenario: Save with the name of an existing folder
- **GIVEN** the chooser in save mode with extension `.siard`, showing a folder that contains a subfolder `Reports`
- **WHEN** the user enters `Reports` and confirms
- **THEN** the chooser shows the folder `Reports` with an empty file name, as native save dialogs do
- **AND** it returns no path and asks no replace question

#### Scenario: Save where the name with the extension is a folder
- **GIVEN** the chooser in save mode with extension `.siard`, showing a folder that contains a subfolder `Archives.siard` and nothing named `Archives`
- **WHEN** the user enters `Archives` and confirms
- **THEN** the chooser shows an error that a folder named `Archives.siard` already exists here and stays in the current folder
- **AND** it returns no path and asks no replace question

#### Scenario: Save with a folder separator in the name
- **GIVEN** the chooser in save mode with extension `.siard`
- **WHEN** the user enters a name containing `/` or `\`, such as `../db` or `sub/db`, and confirms
- **THEN** the chooser shows an error that the name may not contain a folder separator, sends no request and returns no path

#### Scenario: Cancel and unusable choices
- **GIVEN** the chooser in any mode
- **WHEN** the user cancels, or confirms in save mode for a folder that is not writable
- **THEN** cancel returns nothing and issues no further request
- **AND** the non-writable folder shows an error in the chooser and returns no path

#### Scenario: Chooser without a session
- **GIVEN** the chooser is open and the session is no longer valid
- **WHEN** a listing request is answered 401
- **THEN** the chooser shows an error, lists no entries and returns no path

### Requirement: Chooser navigation

The chooser SHALL show the places, the current folder's listing and a path field. It SHALL open in the home folder, open a folder on double-click or Enter, go to the parent folder, and toggle hidden entries. A folder it cannot read SHALL show an error inside the chooser.

#### Scenario: Browse folders
- **GIVEN** the chooser open in the home folder
- **WHEN** the user opens the `Documents` place, then double-clicks a subfolder, then goes to the parent
- **THEN** the listing shows `Documents`, then the subfolder, then `Documents` again

#### Scenario: Unreadable folder
- **GIVEN** the chooser showing a folder with a subfolder the user may not read
- **WHEN** the user opens that subfolder
- **THEN** the chooser shows an error naming the folder and keeps the chooser usable

#### Scenario: Unreachable location
- **GIVEN** the chooser showing a folder
- **WHEN** the user opens a folder whose listing is answered 504, whatever its problem detail text
- **THEN** the chooser shows, in the current interface language, that the location is not reachable right now, for example a network drive while offline
- **AND** the chooser stays usable and the places and the path field still work

#### Scenario: Listing workers exhausted
- **GIVEN** the chooser showing a folder
- **WHEN** the user opens a folder whose listing is answered 503, whatever its problem detail text
- **THEN** the chooser shows, in the current interface language, that some locations are not responding and to try again in a minute
- **AND** the chooser stays usable

#### Scenario: Truncated listing is announced
- **GIVEN** the chooser and a listing answered with 10000 entries and `truncated` true
- **WHEN** the listing is shown
- **THEN** the chooser shows a notice that only the first 10000 entries are listed and that the path field reaches any other entry
- **AND** for a listing with `truncated` false no such notice is shown

#### Scenario: Navigation without a session
- **GIVEN** the chooser is open and the session is no longer valid
- **WHEN** the user opens a place
- **THEN** the chooser shows an error and no entries

### Requirement: Path field

The chooser's path field SHALL accept a typed or pasted absolute path, also enclosed in double quotes or given as a `file://` URI with percent-encoding. A folder SHALL be opened; a file SHALL be selected in its folder; a path that does not exist SHALL show an error.

#### Scenario: Paste from a file manager
- **GIVEN** the chooser in open mode with extension `.siard`
- **WHEN** the user pastes `"C:\Users\me\My Data\db.siard"` on Windows, `/Users/me/My Data/db.siard` on macOS, or `file:///home/me/My%20Data/db.siard` on Linux, and presses Enter
- **THEN** the chooser shows the folder `My Data` with `db.siard` selected and the confirm button enabled, without the user browsing to it
- **AND** pasting `"C:\Users\me\My Data"` (a quoted folder) shows that folder

#### Scenario: Selected file not visible in the list
- **GIVEN** the chooser in open mode with extension `.siard`, hidden items not shown
- **WHEN** the user pastes the path of a file that is far down its folder's listing, hidden, or beyond the 10,000-entry cap
- **THEN** a listed file's row is scrolled into view
- **AND** the chooser shows "Selected: <file name>" next to its buttons, whether or not the row is listed, and the confirm button returns that file's path

#### Scenario: Path that does not exist or does not match
- **GIVEN** the chooser in open mode with extension `.siard`
- **WHEN** the user enters a path that does not exist, then the path of an existing `notes.txt`
- **THEN** the chooser shows an error for each and returns no path

#### Scenario: Path field without a session
- **GIVEN** the chooser in open mode whose session is no longer valid
- **WHEN** the user enters the absolute path of an existing `.siard` file and presses Enter
- **THEN** the entry lookup is answered 401 and the chooser shows the warning that the window is no longer connected to SIARD Suite
- **AND** no file is selected and the chooser returns no path

#### Scenario: Malformed input is not passed on
- **GIVEN** the chooser and a network-request recorder
- **WHEN** the user enters a relative path such as `../secret` or an empty string and presses Enter
- **THEN** the chooser shows an error and sends no request with that input

### Requirement: Show in file browser from the chooser

The chooser SHALL offer "Show in file browser", which asks the agent to reveal the selected entry, or the current folder when nothing is selected.

#### Scenario: Reveal the selection
- **GIVEN** the chooser with `a.siard` selected
- **WHEN** the user activates "Show in file browser"
- **THEN** the frontend posts the absolute path of `a.siard` to `/api/files/reveal`
- **AND** with nothing selected, it posts the current folder's path

#### Scenario: Reveal fails
- **GIVEN** the chooser with an entry selected
- **WHEN** the agent answers 503 to the reveal request
- **THEN** the chooser shows an error saying the file browser could not be opened and stays open

#### Scenario: Reveal without a session
- **GIVEN** the chooser is open and the session is no longer valid
- **WHEN** the user activates "Show in file browser"
- **THEN** the request is answered 401 and the chooser shows an error
