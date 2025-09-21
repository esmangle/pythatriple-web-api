spring boot backend + frontend example project, developed with VS Code on Debian Linux

Technologies used:
* Spring Boot 3.5.6
* Java 21
* MySQL database
* Maven build tool
* Bootstrap 5.3.3

click here for normalized schema version: https://github.com/esmangle/pythatriple-web-api/tree/normalized-schema

### How to run

Download source code:
```bash
git clone https://github.com/esmangle/pythatriple-web-api.git
```

Run app:
```bash
cd pythatriple-web-api
DB_USER="admin" DB_PASS="password" ./mvnw spring-boot:run
```

Run tests:
```bash
cd pythatriple-web-api
./mvnw test
```

(test uses in-mem db, so no login needed)

## Web App (Front-end)

<img width="690" height="480" alt="image" src="https://github.com/user-attachments/assets/bc675e93-1710-415e-8ecc-a6faa418b350" />

Accessible at http://localhost:8080/ (if the spring boot app is running)

Submitting an input number will immediately prepend results to the table

All results from the database are listed under the table in reverse insertion order on page load

## Web API (Back-end)

### API Endpoints

`GET /api/triples` - List all calculated pythagorean triples

```js
[
	{
		"hypotSq": 25,
		"a": 3,
		"b": 4,
		"c": 5,
		"avg": 4.0
	}
]
```

`GET /api/triples?hypotenuse_squared=25` - Calculates a pythagorean triple

```js
{ "a": 3, "b": 4, "c": 5, "avg": 4.0 }
```

Calculates a pythagorean triple for the input parameter `hypotenuse_squared`, which must be a positive integer

Returns an empty json object if the input number is not a perfect square or if no triple could be found

If multiple triples are found, primitive triples are prioritized over non-primitives, and higher average is prioritized

### Example usage

25 = 3² + 4² = 5²\
average: (3 + 4 + 5) / 3.0 = 4.0

```bash
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=25"
{"a":3,"b":4,"c":5,"avg":4.0}
```

Input must be a positive integer

```bash
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=-25"
{"hypotenuse_squared":"must be a positive integer"}
```

Returns an empty object if a triple could not be calculated

```bash
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=1"
{}
```

169 = 5² + 12² = 13²\
average: (5 + 12 + 13) / 3.0 = 10.0

```bash
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=169"
{"a":5,"b":12,"c":13,"avg":10.0}
```

Lists calculated triples in reverse insertion order

```bash
$ curl "http://localhost:8080/api/triples"
[{"hypotSq":169,"a":5,"b":12,"c":13,"avg":10.0},{"hypotSq":25,"a":3,"b":4,"c":5,"avg":4.0}]
```

## Database Schema

```sql
CREATE TABLE IF NOT EXISTS triple_results (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    hypotenuse_squared INT UNSIGNED NOT NULL UNIQUE,
    leg_a INT UNSIGNED NULL,
    leg_b INT UNSIGNED NULL,
    hypotenuse INT UNSIGNED NULL,
    average DOUBLE NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```
