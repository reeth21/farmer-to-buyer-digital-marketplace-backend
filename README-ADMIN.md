# Local demo admin (optional)
Public registration intentionally blocks ADMIN. To create a local admin, start the backend once so the `users` table exists, then use MySQL Workbench to insert a BCrypt hash generated locally. Easiest safe path: temporarily register a normal account, then use a tiny Java BCrypt utility or Spring shell to encode a password and update that user's role:
```sql
UPDATE users SET role='ADMIN' WHERE email='your-local-account@example.com';
```
Log out and log back in so the JWT includes the new role. Do not expose admin credentials or database access publicly.
