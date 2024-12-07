package com.certi;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
	  List<Certificate> findByEmail(String email);

	    // Optionally, you can add custom query methods if needed:
	    // Example: Find certificates by name and email
	    List<Certificate> findByNameAndEmail(String name, String email);
    // Custom query methods if needed
    List<Certificate> findAll();
    
    // Find a certificate by ID, returning an Optional for safe handling of missing results
    Optional<Certificate> findById(String id);
}
