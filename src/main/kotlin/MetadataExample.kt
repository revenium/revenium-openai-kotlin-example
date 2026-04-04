import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import io.revenium.metering.openai.ReveniumOpenAIMiddleware
import io.revenium.metering.openai.model.Credential
import io.revenium.metering.openai.model.Subscriber
import io.revenium.metering.openai.model.UsageMetadata

fun main() {
    val openai = OpenAIOkHttpClient.fromEnv()

    ReveniumOpenAIMiddleware.wrap(openai).use { client ->
        val params = ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4O)
            .maxCompletionTokens(2048)
            .addDeveloperMessage("You are a helpful assistant.")
            .addUserMessage("Say 'Hello, World!' and tell me a fun fact.")
            .build()

        val metadata = UsageMetadata.builder()
            .traceId("session-abc-123")
            .taskType("customer-support")
            .organizationId("org-456")
            .subscriptionId("sub-789")
            .productId("product-pro")
            .agent("support-bot-v2")
            .responseQualityScore(0.95)
            .subscriber(
                Subscriber.builder()
                    .id("user-001")
                    .email("user@example.com")
                    .credential(
                        Credential.builder()
                            .name("api-key")
                            .value("key-xyz")
                            .build()
                    )
                    .build()
            )
            .build()

        val completion = client.chatCompletions().create(params, metadata)

        completion.choices().forEach { choice ->
            choice.message().content().ifPresent { content ->
                println(content)
            }
        }
    }
}
