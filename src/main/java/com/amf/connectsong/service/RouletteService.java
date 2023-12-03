package com.amf.connectsong.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amf.connectsong.config.jwt.JwtUtils;
import com.amf.connectsong.dto.AlbumDTO;
import com.amf.connectsong.model.Album;
import com.amf.connectsong.model.Roulette;
import com.amf.connectsong.model.User;
import com.amf.connectsong.repository.RouletteRepository;
import com.amf.connectsong.repository.UserRepository;

@Service
public class RouletteService {

    @Autowired
    private RouletteRepository rouletteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<AlbumDTO> spinRoulette(String token) {

        if (token == null) {
            throw new RuntimeException("TOKEN_NOT_FOUND");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);

        Optional<User> currentUser = userRepository.findByUsername(username);

        if (!currentUser.isPresent()) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        Roulette roulette = rouletteRepository.findByUser(currentUser.get());

        if (roulette == null) {
            throw new RuntimeException("ROULETTE_NOT_FOUND");
        }

        Album[] rouletteAlbums = roulette.getAlbums().toArray(Album[]::new);

        Album selectAlbum = getRandomAlbum(rouletteAlbums);

        AlbumDTO albumDTO = new AlbumDTO(selectAlbum);
        Link selfLink = Link.of("http://localhost:8080/api/album/" + selectAlbum.getId());
        albumDTO.add(selfLink);

        Link artistsLink = Link.of("http://localhost:8080/api/album/" + selectAlbum.getId() + "/artists")
                .withRel("artists");
        Link reviewsLink = Link.of("http://localhost:8080/api/album/" + selectAlbum.getId() + "/reviews")
                .withRel("reviews");
        albumDTO.add(artistsLink);
        albumDTO.add(reviewsLink);

        return ResponseEntity.ok(albumDTO);
    }

    private Album getRandomAlbum(Album[] rouletteAlbums) {
        if (rouletteAlbums == null || rouletteAlbums.length == 0) {
            throw new RuntimeException("NO_ALBUMS_FOUND");
        }

        int randomIndex = new Random().nextInt(rouletteAlbums.length);
        return rouletteAlbums[randomIndex];
    }
}
