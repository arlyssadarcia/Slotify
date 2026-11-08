package com.slotify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.slotify.model.Resource;
import com.slotify.repository.ResourceRepository;

// Spring manages this class as a service bean
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    // students only see bookable resources
    public List<Resource> listActive() {
        return resourceRepository.findByStatus("ACTIVE");
    }

    public List<Resource> listAll() {
        return resourceRepository.findAll();
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));
    }

    // soft-disable (not hard delete) - safer for history
    public void deactivate(Long id) {
        Resource resource = getById(id);
        resource.setStatus("INACTIVE");
        resourceRepository.save(resource);
    }
}
