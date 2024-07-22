package online.mitja.noElytraNetherite;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DiscordWebhook {

    private static final OkHttpClient client = new OkHttpClient();
    private final String webhookUrl;

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public void sendMessage(String username, String content, String avatarUrl) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String json = "{"
                + "\"username\": \"" + username + "\","
                + "\"content\": \"" + content + "\","
                + (avatarUrl != null ? "\"avatar_url\": \"" + avatarUrl + "\"" : "")
                + "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                response.close();
            }
        });
    }
}