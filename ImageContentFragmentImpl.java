package com.mysite.core.models.impl;

import java.util.HashMap;
import javax.annotation.PostConstruct;

import com.mysite.core.models.ImageContentFragment;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.DownloadResource;

import lombok.Getter;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ImageContentFragment.class}, resourceType = ImageContentFragmentImpl.RESOURCE_TYPE)
public class ImageContentFragmentImpl implements ImageContentFragment{

    public static final String RESOURCE_TYPE = "mysite/components/image";
    private static final String IMAGE = "/image";

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    protected Resource resource;

    @ValueMapValue(name = ContentFragment.PN_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
    private DAMContentElement imageElement;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fileReference;

    @Getter
    private Resource imageResource;

    @PostConstruct
    private void initModel() {
        if (StringUtils.isNotEmpty(fragmentPath) && null != imageElement && null != imageElement.getValue()) {
            createSyntheticResource(imageElement.getValue().toString(), resource.getPath() + IMAGE);
        }
    }

    private void createSyntheticResource(String imagePath, String path) {
        ValueMap properties = new ValueMapDecorator(new HashMap<>());
        properties.put(DownloadResource.PN_REFERENCE, imagePath);
        imageResource = new ValueMapResource(resourceResolver, path, ImageContentFragmentImpl.RESOURCE_TYPE, properties);
    }
}