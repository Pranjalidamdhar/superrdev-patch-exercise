# NOTES

## Summary of changes (highest value first)

1. **SQL precedence bug** (`TaskRepository.java`, `db/` SQL files): `AND` bound tighter than `OR`, so the status and archived filters applied to only part of the search. I added parentheses, so `?status=OPEN` now returns only OPEN tasks. I applied the same fix to the H2 reference query and the Oracle package.
2. **Search wildcards**: `%` and `_` matched everything. I escape them in the controller and use `ESCAPE` in the queries.
3. **Controller** (`TaskController.java`): removed an artificial `Thread.sleep` (up to 1s per request). `page=0`, a bad `pageSize` and an invalid `status` now return a 400 with a message instead of a 500.
4. **Stable ordering**: added `id` as a tie-breaker, and sorted oldest first.
5. **Race condition** (`useTasks.js`): stale responses could overwrite newer ones. Requests are now cancelled with `AbortController`. Loading and error state are also fixed, so the spinner no longer sticks after a failure.
6. **Search UX** (`App.jsx`): 300 ms debounce, and the page resets to 1 when search or status changes.


## How I used AI

I used Claude AI as a supporting tool to identify possible bugs, and consider potential fixes. I reviewed and implemented the changes myself, tested the application to verify the results, and wrote the handwritten explanations in my own words based on my understanding.



