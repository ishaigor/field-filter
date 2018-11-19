package my.company.service.svc.filter;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static my.company.service.api.model.Constants.FIELD_FILTER;

@Singleton
/**
 * Allows to tailor the list of fields based on the list of fields specified in the request
 */
public class FieldFilteringResponseFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldFilteringResponseFilter.class);

    @VisibleForTesting
    static final String FIELDS = "fields";
    private final Map<Class, Set<String>> declaredFields = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                    throws IOException {

        MultivaluedMap<String, String> queryParams = requestContext.getUriInfo().getQueryParameters();
        // parse out the fields
        Set<String> fields = splitParams(queryParams.getFirst(FIELDS));

        // add the modifier
        FieldObjectModifier modifier = new FieldObjectModifier(fields, declaredFields, requestContext.getUriInfo().toString());
        ObjectWriterInjector.set(modifier);
    }

    private  Set<String> splitParams(String paramValue) {
        final Iterable<String> values = Splitter.on(",")
            .trimResults()
            .omitEmptyStrings()
            .split(Strings.nullToEmpty(paramValue));
        return Sets.newHashSet(values);
    }

    @VisibleForTesting
    static class FieldObjectModifier extends ObjectWriterModifier {

        @VisibleForTesting
        Set<String> getFields() {
            return fields;
        }

        private final Map<Class, Set<String>> declaredFields;
        private final Set<String> fields;
        private final String uri;

        /**
         * @param fields requested fields
         * @param declaredFields all declared fields in the system
         * @param uri the uri of the request
         */
        private FieldObjectModifier(Set<String> fields, Map<Class, Set<String>> declaredFields, String uri) {
            this.fields = fields;
            this.declaredFields = declaredFields;
            this.uri = uri;
        }

        @Override
        public ObjectWriter modify(EndpointConfigBase<?> endpoint, MultivaluedMap<String, Object> responseHeaders, Object valueToWrite,
                        ObjectWriter objectWriter, JsonGenerator jsonGenerator) {
            SimpleBeanPropertyFilter filter = null;
            if (valueToWrite != null && fields != null && !fields.isEmpty()) {

                Set<String> availableFields = getClassInfo(valueToWrite.getClass());

                if (CollectionUtils.isNotEmpty(availableFields)) {
                    // filter out unknown fields
                    Set<String> knownFields = fields
                        .stream()
                        .filter(field -> availableFields.contains(field))
                        .collect(Collectors.toSet());

                    if (fields.size() != knownFields.size()) {
                        fields.removeAll(knownFields);
                        LOGGER.error("Unknown fields {} requested for URI {}", fields, uri);
                    }

                    filter = new SimpleBeanPropertyFilter.FilterExceptFilter(knownFields);
                } else {
                    filter = getAllowAllFilter();
                }
            } else {
                filter = getAllowAllFilter();
            }
            FilterProvider provider = new SimpleFilterProvider().addFilter(FIELD_FILTER, filter);
            return objectWriter.with(provider);
        }

        private SimpleBeanPropertyFilter getAllowAllFilter() {
            return SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>());
        }


        private Set<String> getClassInfo(Class<?> filteredClass) {
            return declaredFields.computeIfAbsent(filteredClass, newKey -> computeClassInfo(newKey));
        }

        private Set<String> computeClassInfo(Class filteredClass) {
            BeanInfo info;
            try {
                info = Introspector.getBeanInfo(filteredClass);
            } catch (IntrospectionException e) {
                return Collections.emptySet();
            }
            PropertyDescriptor[] props = info.getPropertyDescriptors();

            return ImmutableSet.copyOf(
                Arrays.stream(props)
                    .map(prop -> prop.getName()).collect(Collectors.toSet()));
        }
    }
}
