# Revenium AI Metering - Kotlin Examples

Sample Kotlin applications demonstrating how to integrate [Revenium](https://revenium.io) AI metering with the [OpenAI Java SDK](https://github.com/openai/openai-java). Revenium's middleware transparently captures token usage, costs, timing, and business metadata from your AI API calls -- with zero changes to your application logic.

## Prerequisites

- JDK 21+
- An [OpenAI API key](https://platform.openai.com/api-keys)
- A [Revenium API key](https://revenium.io) (starts with `hak_`)

## Setup

```bash
export OPENAI_API_KEY="sk-..."
export REVENIUM_METERING_API_KEY="hak_..."
```

## Examples

### Basic Chat Completion

A minimal example that wraps the OpenAI client with Revenium metering and sends a chat completion request.

```bash
./gradlew run
```

**Source:** [`src/main/kotlin/Main.kt`](src/main/kotlin/Main.kt)

```kotlin
val openai = OpenAIOkHttpClient.fromEnv()

ReveniumOpenAIMiddleware.wrap(openai).use { client ->
    val params = ChatCompletionCreateParams.builder()
        .model(ChatModel.GPT_4O)
        .addUserMessage("Say 'Hello, World!' and tell me a fun fact.")
        .build()

    val completion = client.chatCompletions().create(params)
}
// Metering event sent to Revenium in the background
```

### Streaming Chat Completion

Demonstrates streaming support. Token usage and time-to-first-token are captured automatically when the stream closes.

```bash
./gradlew run -PmainClass=StreamingExampleKt
```

**Source:** [`src/main/kotlin/StreamingExample.kt`](src/main/kotlin/StreamingExample.kt)

```kotlin
client.chatCompletions().createStreaming(params).use { stream ->
    stream.stream().forEach { chunk ->
        chunk.choices().forEach { choice ->
            choice.delta().content().ifPresent { print(it) }
        }
    }
}
// Metering fires on stream close with token counts and time-to-first-token
```

### Business Metadata

Attach business context (trace IDs, subscriber info, task types, and more) to metering events for cost attribution, billing, and analytics in Revenium.

```bash
./gradlew run -PmainClass=MetadataExampleKt
```

**Source:** [`src/main/kotlin/MetadataExample.kt`](src/main/kotlin/MetadataExample.kt)

```kotlin
val metadata = UsageMetadata.builder()
    .traceId("session-abc-123")
    .taskType("customer-support")
    .organizationId("org-456")
    .subscriptionId("sub-789")
    .productId("product-pro")
    .agent("support-bot-v2")
    .responseQualityScore(0.95)
    .subscriber(Subscriber.builder()
        .id("user-001")
        .email("user@example.com")
        .credential(Credential.builder()
            .name("api-key")
            .value("key-xyz")
            .build())
        .build())
    .build()

val completion = client.chatCompletions().create(params, metadata)
```

## How It Works

Revenium's middleware wraps the OpenAI client and intercepts API calls transparently:

1. `ReveniumOpenAIMiddleware.wrap(client)` returns an instrumented client
2. All calls through the instrumented client are metered automatically
3. Metering runs asynchronously on a dedicated thread pool -- it never blocks your API calls
4. Failures in metering are logged and silently dropped -- your application is never affected

## Debug Logging

The middleware uses SLF4J. To enable debug logging, set the log level:

```bash
# Via system property
./gradlew run -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

# Or create src/main/resources/simplelogger.properties:
# org.slf4j.simpleLogger.defaultLogLevel=debug
```

## Learn More

- [Revenium AI Metering Middleware for OpenAI Java SDK](https://github.com/revenium/revenium-middleware-openai-java)
- [OpenAI Java SDK](https://github.com/openai/openai-java)
- [Revenium Documentation](https://revenium.io)
