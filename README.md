spring boot backend + frontend example project, developed with VS Code on Debian Linux

Technologies used:
* Spring Boot 3.5.6
* Java 21
* MySQL database
* Maven build tool
* Bootstrap 5.3.3

click here for normalized schema version: https://github.com/esmangle/pythatriple-web-api/tree/normalized-schema

click here for kotlin version: https://github.com/esmangle/pythatriple-web-api/tree/kotlin

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

## misc

This was made as a submission for a technical assessment for an entry-level junior programmer position with these instructions:

- Tech: Spring Boot, Bootstrap, MySQL
- Front-end: Table, Button, Textbox (number, required)
- Button sends GET request to REST API, request content is Textbox value, prepend response to Table
- Back-end: Calculate if value matches formula: value = a² + b² = c²
- Response is {a, b, c, avg} or empty object if no match
- Save response to database
- Bonus: Implementation of good design and features that are not in the basic requirements

Some of the bonus stuff I did:

back-end
- full unit tests (for service and controller)
- parameterized queries (no SQL injection) + transactional

front-end
- uses DocumentFragment to add elements to table (more programmatic, no raw HTML in JS code, prevents XSS attacks)
- loading state (animated spinner + button disabled) after api call (for better UX):
  - if response received in <150 ms: no loading state shown (feels more responsive, no UI flickering)
  - if response received within >150 ms and <450 ms: ensures loading state is shown for at least 300 ms (for no UI flickering)

The normalized-schema and kotlin branches were not part of my submission, those were just done afterwards for exploratory purposes

Tech assessment outcome: Was rejected, but I managed to receive some feedback after asking:

- API design ambiguity: Two endpoints (@GetMapping("triples") and @GetMapping(value = "triples", params = "hypotenuse_squared")) are mapped to the same path /api/triples. One returns a list while the other returns a single triple, which is inconsistent and confusing.
- Incorrect handling of input 1: Input 1 still satisfies the formula (0^2 + 1^2 = 1^2) as stated in the specification (a^2 + b^2 = c^2) , but the program incorrectly returns "Fail: No pythagorean triple found for 1." This shows that the current implementation fails to handle certain edge cases properly.
- Redundant DTOs: PythatripleResponse and PythatripleTableResponse are almost identical, differing only by the presence of hypotSq. The additional Triple record in the service adds another overlapping structure. The overlap introduces some redundancy and increases the number of data shapes to maintain. Having three similar data shapes increases duplication and increases the number of data shapes to maintain.
- Naming inconsistency: PythatripleRequest uses a snake\_case field (hypotenuse\_squared), while other DTOs (PythatripleResponse, PythatripleTableResponse) use camelCase (hypotSq). The difference in naming conventions reduces consistency across the API.
- Unnecessary "best triple" selection: The service performs extra work to determine a "best triple" among possible candidates. This involves additional looping, comparisons, and evaluation of primitiveness and averages. These operations add complexity and computation that are not required by the specification.

My thoughts on the feedback:

> API design ambiguity

That is right, I should've used separate paths for them.

> Incorrect handling of input 1

It didn't seem like (0, 1, 1) was a valid pythagorean triple, since my understanding was that positive integers were required. I was unaware that this is supposed to count.

> Redundant DTOs

It was to prevent the hypotSq property from appearing in the single responses since the specification implied to me that it shouldn't be included for that.

In retrospect, I could've made PythatripleTableResponse inherit from PythatripleResponse or just simply included the property in the response since it's harmless.

The Triple record in the service is a local record merely to be used by the comparator (it shouldn't be relevant or known outside of the calculateTriples method), since I thought it was cleaner than using multiple variables.

> Naming inconsistency

That was because I wanted the query parameter name to be "hypotenuse\_squared", but I should've been fine with just using "hypotSq" in the query parameter, since it would've been more consistent.

> Unnecessary "best triple" selection

The ranking of triples was mainly because it seemed to me like just returning an unspecified result (whatever the algorithm happens to find first) from multiple candidates felt a little like "undefined behaviour", since the function could output something different depending on implementation detail.

So defining which specific triple should be chosen ensures that the function outputs consistent values, regardless of how the algorithm is actually implemented.

Since the algorithm was optimized enough (~2 secs to calculate every possible input from 1^2 to 46340^2 on my machine), I felt it was fine to include as a "bonus" feature even if it's kinda unnecessary/overkill for a simple exercise.