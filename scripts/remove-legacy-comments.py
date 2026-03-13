#!/usr/bin/env python3
"""Remove legacy section-separator comments and method-end comments from Java files.

NOTE: File/class header blocks (containing copyright notices) are intentionally preserved.

Patterns removed:
1. 3-line section separators: /*====...\n text \n ====*/  and  /*====...\n text \n /*===*/
2. 3-line section separators: /*----...\n text \n ----*/
3. Standalone separator lines: /*====...====*/  and  /*----...----*/
4. Standalone separator lines with spaces: /* ====...==== */
5. Inline dash separators: /*--- text ---*/
6. Method-end comments on same line as }: } /* methodName */

Usage:
    python3 scripts/remove-legacy-comments.py [directory]
    python3 scripts/remove-legacy-comments.py .                    # whole project
    python3 scripts/remove-legacy-comments.py siard-cmd/src        # single module
"""
import re
import sys
import os


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8', errors='replace') as f:
        lines = f.readlines()

    original_lines = lines[:]
    result = []
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # 3-line section separators with equals: /*====...\n text \n ====*/ or /*====...\n text \n /*===*/
        if re.match(r'/\*={3,}', stripped) and not re.search(r'\*/', stripped):
            j = i + 1
            found_end = False
            while j < len(lines) and j < i + 5:
                end_stripped = lines[j].strip()
                if re.match(r'={3,}\*/', end_stripped) or re.match(r'/\*[=]{3,}\*/$', end_stripped):
                    i = j + 1
                    found_end = True
                    break
                j += 1
            if found_end:
                continue

        # 3-line section separators with dashes
        if re.match(r'/\*-{3,}', stripped) and not re.search(r'\*/', stripped):
            j = i + 1
            found_end = False
            while j < len(lines) and j < i + 5:
                end_stripped = lines[j].strip()
                if re.match(r'-{3,}\*/', end_stripped):
                    i = j + 1
                    found_end = True
                    break
                j += 1
            if found_end:
                continue

        # Pattern 5+6: Standalone separator lines (single line, closed on same line)
        if re.match(r'/\*[=\-]{3,}\*/$', stripped) or re.match(r'/\*\s+[=\-]{3,}\s+\*/$', stripped):
            i += 1
            continue

        # Pattern 7: Inline dash separators like /*--- uncompressed ---*/
        if re.match(r'/\*-{3,}\s+.*-{3,}\*/$', stripped):
            i += 1
            continue

        # Pattern 8: Method-end comments: } /* methodName */
        # The line must start with } (possibly indented) and have /* ... */ after it
        m = re.match(r'^(\s*\}+)\s*/\*\s*[^*].*\*/$', line.rstrip())
        if m:
            result.append(m.group(1) + '\n')
            i += 1
            continue

        result.append(line)
        i += 1

    # Clean up excessive blank lines (3+ consecutive -> 2)
    cleaned = []
    blank_count = 0
    for line in result:
        if line.strip() == '':
            blank_count += 1
            if blank_count <= 2:
                cleaned.append(line)
        else:
            blank_count = 0
            cleaned.append(line)

    # Strip leading blank lines
    while cleaned and cleaned[0].strip() == '':
        cleaned.pop(0)

    if cleaned != original_lines:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(cleaned)
        return True
    return False


def main():
    root = sys.argv[1] if len(sys.argv) > 1 else '.'
    count = 0
    total = 0
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in ('build', '.git', '.gradle', 'generated')]
        for fn in filenames:
            if fn.endswith('.java'):
                total += 1
                filepath = os.path.join(dirpath, fn)
                if process_file(filepath):
                    count += 1
    print(f"Processed {total} Java files, modified {count}")


if __name__ == '__main__':
    main()
