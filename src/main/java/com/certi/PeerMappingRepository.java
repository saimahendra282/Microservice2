package com.certi;

import com.certi.PeerMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PeerMappingRepository extends MongoRepository<PeerMapping, String> {

	List<PeerMapping> findByPeerEmail(String peerEmail);
    List<PeerMapping> findAll(); // This is provided by JpaRepository by default, so no changes are needed.
    void deleteByPeerEmail(String peerEmail);
//    List<PeerMapping> findByEmail(String email); 
    
}