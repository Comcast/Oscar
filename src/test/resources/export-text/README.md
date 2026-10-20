# Fixed exporter output fixtures

Synthetic fragments captured from the pre-refactor exporter, with numeric OIDs.
These are formatting fixtures, not complete provisionable configurations.
No customer files, certificates or secrets were used.

- `*-source.txt`: synthetic parser input.
- `*-annotated.txt`: exact expected body with leaf TLV-path comments.
- `*-plain.txt`: exact expected body with comments suppressed.

Tabs, LF line endings, spaces and the final LF are intentional. The versioned
help/license banner is excluded; JUnit checks it separately. Review intentional
output changes before editing fixtures; never auto-regenerate on a test failure.
