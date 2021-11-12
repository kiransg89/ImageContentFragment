package com.mysite.core.models.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class}, resourceType = ImageDelegate.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageDelegate implements Image {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDelegate.class);
    public static final String RESOURCE_TYPE = "mysite/components/content/image";
    public static final String CONTENT_DAM_PATH = "/content/dam";
    public static final String HTTPS = "https://";
    public static final String PN_DISPLAY_SIZES = "sizes";
    public static final String WIDTH = "{.width}";

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    protected Resource resource;

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegationExclusion.class)
    private Image image;

    @Override
    public String getSrc() {
        return prepareSuffix(image.getSrc());
    }

    @Override
    public String getSrcset() {
        int[] widthsArray = image.getWidths();
        String srcUritemplate = image.getSrcUriTemplate();
        String[] srcsetArray = new String[widthsArray.length];
        if (widthsArray.length > 0 && srcUritemplate != null) {
            String srcUriTemplateDecoded = "";
            try {
                srcUriTemplateDecoded = prepareSuffix(URLDecoder.decode(srcUritemplate, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Character Decoding failed for {}", resource.getPath());
            }
            if (srcUriTemplateDecoded.contains(WIDTH)) {
                for (int i = 0; i < widthsArray.length; i++) {
                    if (srcUriTemplateDecoded.contains("="+WIDTH)) {
                        srcsetArray[i] = srcUriTemplateDecoded.replace(WIDTH, String.format("%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    } else {
                        srcsetArray[i] = srcUriTemplateDecoded.replace(WIDTH, String.format(".%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    }
                }
                return StringUtils.join(srcsetArray, ',');
            }
        }
        return null;
    }

    private String prepareSuffix(String imageSrc) {
        if(StringUtils.isNotEmpty(imageSrc) && !StringUtils.containsIgnoreCase(imageSrc, CONTENT_DAM_PATH)) {
            int endIndex = imageSrc.lastIndexOf(SlingPostConstants.DEFAULT_CREATE_SUFFIX);
            String intermittenResult = imageSrc.substring(0, endIndex);
            endIndex = intermittenResult.lastIndexOf(SlingPostConstants.DEFAULT_CREATE_SUFFIX);
            return intermittenResult.substring(0, endIndex)+image.getFileReference();
        }
        return imageSrc;
    }

    private interface DelegationExclusion {
        String getSrc();
        String getSrcset();
    }
}