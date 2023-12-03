package com.amf.connectsong.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.amf.connectsong.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
   
   

    Artist findByNome(String nome);

    Artist findById(long id);
    
}
