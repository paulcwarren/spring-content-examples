package com.emc.spring.content.examples;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource(path="claims")
public interface ClaimFormStore extends ContentStore<ClaimForm, String>, Renderable<ClaimForm> {

}
