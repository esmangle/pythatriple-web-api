spring boot backend + frontend example project, developed with VS Code on Debian Linux

Technologies used:
* Spring Boot 3.5.6
* Java 21
* MySQL database
* Maven build tool
* Bootstrap 5.3.3

### How to run

Download source code:
```bash
git https://github.com/esmangle/pythatriple-web-api.git
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

## Web App (Front-end)

tba

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

### Example usage

25 = 3² + 4² = 5²\
average: (3 + 4 + 5) / 3 = 4.0

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

100 = 6² + 8² = 10²\
average: (6 + 8 + 10) / 3 = 8.0

```bash
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=100"
{"a":6,"b":8,"c":10,"avg":8.0}
```

Lists calculated triples in reverse insertion order

```bash
$ curl "http://localhost:8080/api/triples"
[{"hypotSq":100,"a":6,"b":8,"c":10,"avg":8.0},{"hypotSq":25,"a":3,"b":4,"c":5,"avg":4.0}]
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
