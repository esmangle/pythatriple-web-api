spring boot backend + frontend example project, developed with VS Code on Debian Linux

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

## Web API (Back-end)

Technologies used:
* Spring Boot 3.5.6
* Java 21
* MySQL database
* Maven build tool

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

```
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=25"
{"a":3,"b":4,"c":5,"avg":4.0}
```

Input must be a positive integer

```
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=-25"
{"hypotenuse_squared":"must be a positive integer"}
```

Returns an empty object if a triple could not be calculated

```
$ curl "http://localhost:8080/api/triples?hypotenuse_squared=1"
{}
```

Lists calculated triples in reverse insertion order

```
$ curl "http://localhost:8080/api/triples"
[{"hypotSq":100,"a":6,"b":8,"c":10,"avg":8.0},{"hypotSq":25,"a":3,"b":4,"c":5,"avg":4.0}]
```

## Web App (Front-end)
