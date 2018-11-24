package my.company.service.svc;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import my.company.service.api.MyService;
import my.company.service.api.model.TransferObject;
import static my.company.service.svc.filter.FieldFilteringResponseFilter.getFieldsFromRequest;
import org.apache.commons.collections4.CollectionUtils;

@Path(MyService.ROOT_PATH)
public class MyServiceImpl implements MyService {
    public static final String CODE = "CODE";
    public static final String TYPE = "TYPE";
    public static final String DESCRIPTION = "Description";
    public static final String NAME = "Name";
    public static final String URI_INFO = "/test?fields=codeFFF,name,description";
    public static final String URL = "https://my.company.com";
    public static final String FEATURE_1 = "Feature1";
    public static final String FEATURE_2 = "Feature2";
    public static final List<String> FEATURES = ImmutableList.of(FEATURE_1, FEATURE_2);
    public static TransferObject TRANSFER_OBJECT = new TransferObject.Builder()
        .withCode(CODE)
        .withType(TYPE)
        .withUrl(URL)
        .withDescription(DESCRIPTION)
        .withName(NAME)
        .withFeatures(FEATURES)
        .build();

    @Context
    UriInfo uriInfo;

    @Override
    public TransferObject getTransferObject() {
        Set<String> fields = getFieldsFromRequest(uriInfo);
        if (CollectionUtils.isEmpty(fields) || fields.contains("features")) {
            // TODO do heavy lifting to load the features
        }
        return TRANSFER_OBJECT;
    }
}
