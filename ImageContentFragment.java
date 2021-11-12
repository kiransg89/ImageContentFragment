package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code ImageContentFragment} Sling Model used for the {@code /apps/mysite/components/imagecontentfragment} component.
 */
@ConsumerType
public interface ImageContentFragment {

    /**
     * Getter for Image Synthetic Resource
     *
     * @return resource
     */
    default Resource getImageResource() {
        throw new UnsupportedOperationException();
    }
}
