package my.company.service.svc.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Provider
@Consumes(MediaType.APPLICATION_JSON) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.APPLICATION_JSON)
public class JsonProvider extends JacksonJsonProvider {
    public JsonProvider() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setConfig(objectMapper.getSerializationConfig())
                        .setConfig(objectMapper.getDeserializationConfig())
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
                        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                        .registerModule(new JavaTimeModule())
                        .registerModule(new Jdk8Module())
                        .registerModule(new GuavaModule())
                        .findAndRegisterModules();
        setMapper(objectMapper);
    }
}
