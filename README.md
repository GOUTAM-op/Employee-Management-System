# Employee Management & Payroll System

A desktop Java application (Swing GUI, JDBC, MySQL) for managing employee
records and generating monthly payroll. Built with OOP layering — model /
DAO / service / UI are fully separated — full input validation, and custom
checked/unchecked exceptions instead of raw `SQLException` leaking into the UI.

## Features

- **Employee CRUD** — add, edit, delete, search and list employees, with a
  live-styled table (status badges, alternating rows).
- **Payroll engine** — computes HRA, DA, medical allowance, PF, professional
  tax and a simplified slab-based income tax, then previews and saves a
  payslip per employee/month/year (duplicate runs are blocked at the DB level).
- **Dashboard** — headline stats (total/active employees, departments,
  total monthly basic payroll) plus a hand-drawn department bar chart.
- **Validation & error handling** — every field is validated before it ever
  reaches SQL (name, email, phone, salary, date of joining); all `SQLException`s
  are caught at the DAO layer and re-thrown as a friendly `DatabaseException`
  so the UI never shows a raw stack trace to the user.
- **Custom, from-scratch UI** — no external UI library: rounded cards, a
  dark sidebar, styled buttons/fields with hover states and inline validation
  errors, all hand-built on top of plain Swing.

## Project layout

```
src/main/java/com/empmgmt/
  Main.java                     entry point
  model/                        Employee, PayrollRecord (POJOs)
  exception/                    ValidationException, DatabaseException
  dao/                          DatabaseConnection, EmployeeDAO, PayrollDAO
  service/                      ValidationService, PayrollService (business logic)
  ui/                           MainFrame + the three screens (Dashboard, Employees, Payroll)
  ui/components/                Reusable styled Swing widgets (RoundedButton, StyledField, ...)
  ui/theme/                     AppTheme — single source of truth for colors/fonts
resources/
  schema.sql                    Standalone copy of the table definitions
  db.properties                 MySQL URL / username / password (edit this!)
```

## Setup

1. **Install MySQL** (5.7+ or 8.x) and make sure it's running locally.
2. **Edit `resources/db.properties`** with your MySQL username/password.
   You don't need to create the tables yourself — the app runs
   `CREATE TABLE IF NOT EXISTS` for you on first launch — but you can also
   run `resources/schema.sql` manually if you prefer.
3. **Download the MySQL Connector/J jar** (the JDBC driver — it's the one
   external dependency this project needs, since the JDK does not ship a
   MySQL driver): https://dev.mysql.com/downloads/connector/j/
   Grab the "Platform Independent" `.zip`/`.tar.gz`, and pull out the
   `mysql-connector-j-<version>.jar` file. Put it in a `lib/` folder in the
   project root.

## Compile & run

From the project root, with the connector jar in `lib/`:

```bash
# Compile (resources/ goes on the classpath too, so db.properties is found)
javac -d out -cp "lib/*" $(find src -name "*.java")

# Run
java -cp "out:resources:lib/*" com.empmgmt.Main
```

On Windows, replace the `:` classpath separators with `;`:

```bat
javac -d out -cp "lib\*" src\main\java\com\empmgmt\**\*.java
java -cp "out;resources;lib\*" com.empmgmt.Main
```

If you'd rather use an IDE (IntelliJ IDEA / Eclipse), just import the
`src` folder as a source root, add the connector jar as a library, and mark
`resources` as a resources root, then run `Main.java`.

## Notes on the payroll formulas

The percentages in `service/PayrollService.java` (HRA 40%, DA 10%, PF 12%,
professional tax ₹200/month, and the income-tax slab) are simplified,
illustrative defaults — tune the constants at the top of that file to match
whatever policy you actually need; they're all in one place on purpose.
