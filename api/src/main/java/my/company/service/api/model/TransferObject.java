package my.company.service.api.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import static my.company.service.api.model.Constants.FIELD_FILTER;

@JsonFilter(FIELD_FILTER)
@Value.Immutable
@Value.Style(
    typeAbstract = "*",
    typeImmutable = "Immutable*", // prefix immutable concrete implementations with Immutable
    typeBuilder = "Builder", // name builder classes as Builder (nested within immutable concrete class)
    init = "with*", // Builder initialization methods will have 'with' prefix
    jacksonIntegration = true,
    defaultAsDefault = true, // java 8 default methods will be automatically turned into @Value.Default
    allParameters = false,
    visibility = Value.Style.ImplementationVisibility.PACKAGE, // hide the immutable implementation class
    builderVisibility = Value.Style.BuilderVisibility.PACKAGE, // hide the builder (and expose via nested class in your Object
    overshadowImplementation = true,
    optionalAcceptNullable = true
)
@JsonDeserialize(builder = TransferObject.Builder.class)
public abstract class TransferObject {

    public abstract Optional<String> getCode();

    public abstract Optional<String> getType();

    public abstract Optional<String> getName();

    public abstract Optional<String> getUrl();

    public abstract Optional<String> getDescription();

    @Value.Default
    public List<String> getFeatures() {
        return Collections.emptyList();
    }

    public static class Builder extends ImmutableTransferObject.Builder {}
}
