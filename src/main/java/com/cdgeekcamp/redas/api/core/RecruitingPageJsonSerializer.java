package com.cdgeekcamp.redas.api.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

// https://www.baeldung.com/spring-boot-jsoncomponent
@JsonComponent
public class RecruitingPageJsonSerializer extends JsonSerializer<RecruitingPage> {
    @Override
    public void serialize(RecruitingPage user, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("uuid", user.getUuid());
        jsonGenerator.writeStringField("advantage", user.getAdvantage());
        jsonGenerator.writeStringField("company_main_page", user.getCompany_main_page());
        jsonGenerator.writeStringField("company_name", user.getCompany_name());
        jsonGenerator.writeStringField("company_nature", user.getCompany_nature());
        jsonGenerator.writeStringField("edu", user.getEdu());
        jsonGenerator.writeStringField("exp", user.getExp());
        jsonGenerator.writeStringField("hr_name", user.getHr_name());
        jsonGenerator.writeStringField("hr_position", user.getHr_position());
        jsonGenerator.writeStringField("location", user.getLocation());
        jsonGenerator.writeStringField("money", user.getMoney());
        jsonGenerator.writeStringField("pos_desc", user.getPos_desc());
        jsonGenerator.writeStringField("position", user.getPosition());
        jsonGenerator.writeStringField("scale", user.getScale());
        jsonGenerator.writeStringField("stage", user.getStage());
        jsonGenerator.writeArrayFieldStart("tag_list");
        for (String tag : user.getTag_list()) {
            jsonGenerator.writeString(tag);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();

        jsonGenerator.close();
    }
}
