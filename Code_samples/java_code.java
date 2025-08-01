import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class java_code {
    public static void main(String[] args) throws IOException {

        String API_KEY = "<your-ibm-api-key>";

        String deploymentUrl = "https://us-south.ml.cloud.ibm.com/ml/v4/deployments/<deployment-id>/ai_service_stream?version=2021-05-01";

        HttpURLConnection tokenConnection = null;
        HttpURLConnection scoringConnection = null;
        BufferedReader tokenBuffer = null;
        BufferedReader scoringBuffer = null;

        try {
            URL tokenUrl = new URL("https://iam.cloud.ibm.com/identity/token");
            tokenConnection = (HttpURLConnection) tokenUrl.openConnection();
            tokenConnection.setDoOutput(true);
            tokenConnection.setRequestMethod("POST");
            tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            tokenConnection.setRequestProperty("Accept", "application/json");

            String body = "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + API_KEY;
            OutputStreamWriter out = new OutputStreamWriter(tokenConnection.getOutputStream());
            out.write(body);
            out.flush();
            out.close();

            tokenBuffer = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
            StringBuilder tokenResponse = new StringBuilder();
            String line;
            while ((line = tokenBuffer.readLine()) != null) {
                tokenResponse.append(line);
            }

            String iam_token = "Bearer " + tokenResponse.toString().split("\"access_token\":\"")[1].split("\"")[0];

            URL scoringUrl = new URL(deploymentUrl);
            scoringConnection = (HttpURLConnection) scoringUrl.openConnection();
            scoringConnection.setDoOutput(true);
            scoringConnection.setRequestMethod("POST");
            scoringConnection.setRequestProperty("Accept", "application/json");
            scoringConnection.setRequestProperty("Authorization", iam_token);
            scoringConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String payload = "{ \"messages\": [ { \"content\": \"What is UPI fraud?\", \"role\": \"user\" } ] }";

            OutputStreamWriter writer = new OutputStreamWriter(scoringConnection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.flush();
            writer.close();

            InputStream inputStream = scoringConnection.getResponseCode() == 200
                    ? scoringConnection.getInputStream()
                    : scoringConnection.getErrorStream();

            scoringBuffer = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String lineScoring;
            while ((lineScoring = scoringBuffer.readLine()) != null) {
                response.append(lineScoring);
            }

            System.out.println("\n✅ AI Response:");
            System.out.println(response.toString());

        } catch (Exception e) {
            System.err.println("❌ Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (tokenConnection != null)
                tokenConnection.disconnect();
            if (scoringConnection != null)
                scoringConnection.disconnect();
            if (tokenBuffer != null)
                tokenBuffer.close();
            if (scoringBuffer != null)
                scoringBuffer.close();
        }
    }
}
