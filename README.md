# Github Kafka Connect

[![Build](https://github.com/rayyildiz/github-kafka-connector/actions/workflows/build.yaml/badge.svg)](https://github.com/rayyildiz/github-kafka-connector/actions/workflows/build.yaml)

Kafka Connect source connector that streams GitHub repository issues into Kafka.

It fetches issues from the GitHub REST API for a given `owner/repository` and produces records to a target Kafka topic using Kafka Connect schemas (works with Avro or JSON converters depending on your Connect configuration).

## Features
- Source connector for GitHub issues (polls the GitHub Issues API)
- Offset tracking per repository so repeated runs only continue from the last processed item
- Optional GitHub authentication to raise API rate limits
- Structured record schemas for keys and values
- Configurable batch size with validation (1–250)

## Requirements
- Java 8+ (11 recommended)
- Kafka and Kafka Connect (tested with Confluent Platform 7.9.x images)
- Optional: Schema Registry if you use Avro converters
- sbt 1.x to build from source

Project build details:
- Scala: `2.13.18`
- Kafka Connect API: `org.apache.kafka:connect-api:4.1.1`

## Installation
1. Build the connector JAR:
   - With sbt installed, run:
     ```bash
     sbt clean package
     ```
   - The artifact will be under `target/scala-2.13/`.
2. Install into Kafka Connect:
   - Copy the connector JAR (and its required runtime dependencies not already present on your Connect classpath) into a folder listed in your `CONNECT_PLUGIN_PATH`.
   - For Confluent’s Docker image, you can mount a host directory into `/etc/kafka-connect/jars` and include that path in `CONNECT_PLUGIN_PATH`.
3. Restart Kafka Connect so it discovers the plugin.

Note: The Confluent Kafka Connect images already include many Kafka dependencies. You still need to provide this connector JAR and its runtime libraries (e.g., STTP and Spray JSON). Keeping all plugin JARs in a single directory is a common practice.

## Configuration
Connector class:
- `com.rayyildiz.connect.GithubSourceConnector`

Supported properties (with defaults where applicable):
- `topic` (string, required): Target Kafka topic to produce issues to.
- `github.owner` (string, required): GitHub owner/org name.
- `github.repository` (string, required): GitHub repository name.
- `github.batch` (int, default `100`): Batch size; must be between 1 and 250.
- `github.auth.username` (string, default empty): GitHub username for basic auth or token user; optional.
- `github.auth.password` (password, default empty): GitHub password or personal access token; optional.

Typical minimal configuration requires `topic`, `github.owner`, and `github.repository`.

## How it works
- The connector polls `https://api.github.com/repos/{owner}/{repo}/issues`.
- Each issue is converted to a Kafka Connect `SourceRecord` and produced to your `topic`.
- Offsets are tracked per `owner/repository` via Kafka Connect’s offset storage.

## Record schema
The connector produces structured records with Kafka Connect schemas.

Key (struct):
- `owner` (string)
- `repository` (string)
- `issue_id` (int)

Value (struct): includes common GitHub Issue fields, for example:
- `id` (int)
- `node_id` (string)
- `title` (string)
- `state` (string)
- `created_at` (timestamp as epoch millis)
- `updated_at` (optional timestamp as epoch millis)
- `body` (optional string)
- `pull_request` (struct, optional): `{ url, diff_url }`
- `user` (struct): `{ id, login, site_admin }`
- `labels` (array, optional): items `{ id, name, color }`

Note: The exact schema types are defined in `src/main/scala/com/rayyildiz/connect/GithubSchema.scala` and may evolve across versions. Ensure your chosen converter (e.g., AvroConverter or JsonConverter) matches your downstream expectations.

## Running with Kafka Connect

Example REST request to create the connector:
```bash
curl -s -X POST http://localhost:8083/connectors \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "github-issues",
  "config": {
    "connector.class": "com.rayyildiz.connect.GithubSourceConnector",
    "tasks.max": "1",
    "topic": "github.issues",
    "github.owner": "rayyildiz",
    "github.repository": "github-kafka-connector",
    "github.batch": "100",
    "github.auth.username": "",
    "github.auth.password": ""
  }
}' | jq .
```

Consume from the topic (example):
```bash
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic github.issues \
  --from-beginning
```

### Using Docker Compose (example)
This repo includes a sample `docker-compose.yml` that starts Zookeeper, Kafka, Schema Registry, and Kafka Connect images. To use this with your locally built connector:
1. Build the connector JAR: `sbt clean package`
2. Mount the directory with your JARs into the Connect container and ensure `CONNECT_PLUGIN_PATH` includes that directory (e.g., `/etc/kafka-connect/jars`).
3. Start the stack: `docker compose up -d`
4. Register the connector via the REST example above.

Important: Adjust ports and mounts in `docker-compose.yml` to match your environment. Ensure the Kafka bootstrap address you use to consume matches the advertised listeners in the compose file.

## Authentication and rate limits
GitHub’s unauthenticated API access is rate-limited. Provide `github.auth.username` and `github.auth.password` (or a personal access token) to increase limits. See GitHub’s API docs for current rate limits and token requirements.

## Development
- Format code: `sbt scalafmt`
- Run tests: `sbt test`
- Build JAR: `sbt package`

## Versioning
- Connector version is defined in code. Current library versions:
  - Scala: 2.13.18
  - Kafka Connect API: 4.1.1

## License
MIT — see the [LICENSE](LICENSE) file for details.


