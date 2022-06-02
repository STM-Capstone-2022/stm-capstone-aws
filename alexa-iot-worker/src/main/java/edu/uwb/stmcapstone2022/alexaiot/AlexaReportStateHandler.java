package edu.uwb.stmcapstone2022.alexaiot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class AlexaReportStateHandler {
    private final S3Client s3Client = S3Client.create();
    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private static final Gson gson = new GsonBuilder().create();

    @Builder
    @Getter
    @ToString
    private static class EnvData {
        private final double temp_0_c;
        private final double rh_pct;
        private final double temp_1_c;
        private final double baro_mbar;
        @SerializedName("fake door knock")
        private final float fake_door_knock;
    }

    public SkillResponse<Void> handleReportState(Directive<Void> request) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key("envData.json")
                .build();

        var objectResponse = s3Client.getObject(objectRequest);
        try (var reader = new InputStreamReader(objectResponse)) {
            EnvData envData = gson.fromJson(reader, EnvData.class);
            log.info("envData download successful: {}", envData);
            throw new UnsupportedOperationException("No idea how to handle StateReport /shrug");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
