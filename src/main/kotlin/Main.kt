import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import io.revenium.metering.openai.ReveniumOpenAIMiddleware

fun main() {
    val openai = OpenAIOkHttpClient.fromEnv()

    ReveniumOpenAIMiddleware.wrap(openai).use { client ->
        val params = ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4O)
            .maxCompletionTokens(2048)
            .addDeveloperMessage("You are a helpful assistant.")
            .addUserMessage("Say 'Hello, World!' and tell me a fun fact.")
            .build()

        val completion = client.chatCompletions().create(params)

        completion.choices().forEach { choice ->
            choice.message().content().ifPresent { content ->
                println(content)
            }
        }
    }
}
