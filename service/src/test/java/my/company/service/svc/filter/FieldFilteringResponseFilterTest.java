package my.company.service.svc.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.beans.IntrospectionException;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;
import com.google.common.collect.ImmutableList;
import static my.company.service.api.model.Constants.FIELD_FILTER;
import my.company.service.api.model.TransferObject;
import my.company.service.svc.MyServiceImpl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FieldFilteringResponseFilterTest {

    private FieldFilteringResponseFilter filter;

    private ObjectMapper mapper;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private ContainerResponseContext responseContext;

    @Mock
    private UriInfo uriInfo;

    @BeforeEach
    public void setup() throws IntrospectionException {
        initMocks(this);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        filter = new FieldFilteringResponseFilter();
    }

    @Test
    public void returnAllFields() throws IOException {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        when(uriInfo.getQueryParameters()).thenReturn(params);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        filter.filter(requestContext, responseContext);
        ObjectWriterModifier modifier = ObjectWriterInjector.get();
        assertThat(modifier, is(notNullValue()));
        assertThat(modifier, is(instanceOf(FieldFilteringResponseFilter.FieldObjectModifier.class)));
        FieldFilteringResponseFilter.FieldObjectModifier fieldModifier = (FieldFilteringResponseFilter.FieldObjectModifier) modifier;
        assertThat(fieldModifier.getFields(), is(notNullValue()));
        assertThat(fieldModifier.getFields().size(), is(equalTo(0)));
        ObjectWriter writer = fieldModifier.modify(null, null, MyServiceImpl.TRANSFER_OBJECT, mapper.writer(), null);
        FilterProvider filterProvider = writer.getConfig().getFilterProvider();
        assertThat(filterProvider, is(notNullValue()));
        assertThat(filterProvider.findPropertyFilter(FIELD_FILTER, null), is(instanceOf(SimpleBeanPropertyFilter.SerializeExceptFilter.class)));

        String json = writer.writeValueAsString(MyServiceImpl.TRANSFER_OBJECT);
        TransferObject restoredValue = mapper.readValue(json, TransferObject.class);
        assertThat(restoredValue, is(notNullValue()));
        assertThat(restoredValue.getCode().get(), is(equalTo(MyServiceImpl.CODE)));
        assertThat(restoredValue.getUrl().get(), is(equalTo(MyServiceImpl.URL)));
        assertThat(restoredValue.getDescription().get(), is(equalTo(MyServiceImpl.DESCRIPTION)));
        assertThat(restoredValue.getName().get(), is(equalTo(MyServiceImpl.NAME)));
        assertThat(restoredValue.getType().get(), is(equalTo(MyServiceImpl.TYPE)));
        assertThat(restoredValue.getFeatures().size(), is(equalTo(2)));
        assertThat(restoredValue.getFeatures().get(0), is(equalTo(MyServiceImpl.FEATURE_1)));
        assertThat(restoredValue.getFeatures().get(1), is(equalTo(MyServiceImpl.FEATURE_2)));
    }

    @Test
    public void returnSomeFields() throws IOException {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put(FieldFilteringResponseFilter.FIELDS, ImmutableList.of("code,type,name,description"));
        when(uriInfo.getQueryParameters()).thenReturn(params);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        filter.filter(requestContext, responseContext);
        ObjectWriterModifier modifier = ObjectWriterInjector.get();
        assertThat(modifier, is(notNullValue()));
        assertThat(modifier, is(instanceOf(FieldFilteringResponseFilter.FieldObjectModifier.class)));
        FieldFilteringResponseFilter.FieldObjectModifier fieldModifier = (FieldFilteringResponseFilter.FieldObjectModifier) modifier;
        assertThat(fieldModifier.getFields(), is(notNullValue()));
        assertThat(fieldModifier.getFields().size(), is(equalTo(4)));

        ObjectWriter writer = fieldModifier.modify(null, null, MyServiceImpl.TRANSFER_OBJECT, mapper.writer(), null);
        FilterProvider filterProvider = writer.getConfig().getFilterProvider();
        assertThat(filterProvider, is(notNullValue()));
        assertThat(filterProvider.findPropertyFilter(FIELD_FILTER, null), is(instanceOf(SimpleBeanPropertyFilter.FilterExceptFilter.class)));

        String json = writer.writeValueAsString(MyServiceImpl.TRANSFER_OBJECT);
        TransferObject restoredValue = mapper.readValue(json, TransferObject.class);
        assertThat(restoredValue, is(notNullValue()));
        assertThat(restoredValue.getCode().get(), is(equalTo(MyServiceImpl.CODE)));
        assertThat(restoredValue.getUrl().isPresent(), is(equalTo(false)));
        assertThat(restoredValue.getDescription().get(), is(equalTo(MyServiceImpl.DESCRIPTION)));
        assertThat(restoredValue.getName().get(), is(equalTo(MyServiceImpl.NAME)));
        assertThat(restoredValue.getDescription().get(), is(equalTo(MyServiceImpl.DESCRIPTION)));
        assertThat(restoredValue.getType().get(), is(equalTo(MyServiceImpl.TYPE)));
        assertThat(restoredValue.getFeatures().size(), is(equalTo(0)));
    }

    @Test
    public void returnSkipUnknownFields() throws IOException {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put(FieldFilteringResponseFilter.FIELDS, ImmutableList.of("codeFFF,name,description"));
        when(uriInfo.getQueryParameters()).thenReturn(params);
        when(uriInfo.toString()).thenReturn(MyServiceImpl.URI_INFO);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        filter.filter(requestContext, responseContext);
        ObjectWriterModifier modifier = ObjectWriterInjector.get();
        assertThat(modifier, is(notNullValue()));
        assertThat(modifier, is(instanceOf(FieldFilteringResponseFilter.FieldObjectModifier.class)));
        FieldFilteringResponseFilter.FieldObjectModifier fieldModifier = (FieldFilteringResponseFilter.FieldObjectModifier) modifier;
        assertThat(fieldModifier.getFields(), is(notNullValue()));
        assertThat(fieldModifier.getFields().size(), is(equalTo(3)));

        ObjectWriter writer = fieldModifier.modify(null, null, MyServiceImpl.TRANSFER_OBJECT, mapper.writer(), null);
        FilterProvider filterProvider = writer.getConfig().getFilterProvider();
        assertThat(filterProvider, is(notNullValue()));
        assertThat(filterProvider.findPropertyFilter(FIELD_FILTER, null), is(instanceOf(SimpleBeanPropertyFilter.FilterExceptFilter.class)));

        String json = writer.writeValueAsString(MyServiceImpl.TRANSFER_OBJECT);
        TransferObject restoredValue = mapper.readValue(json, TransferObject.class);
        assertThat(restoredValue, is(notNullValue()));
        assertThat(restoredValue, is(notNullValue()));
        assertThat(restoredValue.getCode().isPresent(), is(equalTo(false)));
        assertThat(restoredValue.getUrl().isPresent(), is(equalTo(false)));
        assertThat(restoredValue.getDescription().get(), is(equalTo(MyServiceImpl.DESCRIPTION)));
        assertThat(restoredValue.getName().get(), is(equalTo(MyServiceImpl.NAME)));
        assertThat(restoredValue.getType().isPresent(), is(equalTo(false)));
        assertThat(restoredValue.getFeatures().size(), is(equalTo(0)));
    }

}
