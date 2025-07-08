# Mutants API

This project implements a Java Spring Boot REST API that determines whether a human is a mutant based on their DNA sequence and provides statistics on DNA verification requests.

📋 **Prerequisites**

Java 17 or higher <br/>
Maven 3.6+ <br/>
(Optional) AWS CLI v2 and EB CLI configured for deployment on Elastic Beanstalk

🏗️ **Build**

From the project root directory, run:

```bash
./mvnw clean package -DskipTests
```

After completion, the artifact target/mutants-0.0.1-SNAPSHOT.jar will be generated.

**🚀 Run Locally**

The service starts by default on port 5000:

```bash
java -jar target/mutants-0.0.1-SNAPSHOT.jar
```

**Available Endpoints**

POST /mutant <br/>
Request Body JSON:
```json
{ "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"] }
```

Responses:

200 OK → DNA indicates a mutant <br/>
403 Forbidden → DNA indicates a human

GET /stats

Response 200 OK with JSON:
```json
{
"count_mutant_dna": 10,
"count_human_dna": 90,
"ratio": 0.11
}
```

✅ Examples with curl

### Check mutant DNA

```bash
echo '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}' \
| curl -i -X POST http://localhost:5000/mutant \
-H "Content-Type: application/json"
```

### Get statistics
```bash
curl -i http://localhost:5000/stats
```