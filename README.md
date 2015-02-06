
Version 1.1
- Bug Fix: Force close on some devices.

Version 1.2
- Updating local cache with the latest schedule.

Updating database
=================

1. Use `DatabaseUtils.exportDatabase` method to copy the database. This is commented in `MainActivity.java`.
2. Add `WRITE_EXTERNAL_STORAGE` to manifest.
3. Use AstroFileManager or Android File Transfer to copy the file.
4. Move the current database file to /databases. This is for backup.
5. Copy the new database over to `assets/databases`.
6. Upgrade the database version in `LSDatabase.java`.