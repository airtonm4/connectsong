package com.amf.connectsong.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amf.connectsong.config.jwt.JwtUtils;
import com.amf.connectsong.model.Album;
import com.amf.connectsong.model.Artist;
import com.amf.connectsong.model.Roulette;
import com.amf.connectsong.model.Track;
import com.amf.connectsong.model.User;
import com.amf.connectsong.repository.RouletteRepository;
import com.amf.connectsong.repository.UserRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.SavedAlbum;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

@Service
public class SpotifyService {

    Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    @Autowired
    private RouletteRepository rouletteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    public SpotifyService() {
    }

    public ResponseEntity<?> getUserAlbums(SpotifyApi spotifyApi, String token) {
        try {
            final Paging<SavedAlbum> savedAlbumPaging = spotifyApi.getCurrentUsersSavedAlbums().build().execute();

            SavedAlbum[] savedAlbums = savedAlbumPaging.getItems();

            String username = jwtUtils.getUserNameFromJwtToken(token.replace("Bearer ", ""));

            Optional<User> currentUser = userRepository.findByUsername(username);

            if (!currentUser.isPresent()) {
                throw new RuntimeException("USER_NOT_FOUND");
            }

            Roulette roulette = new Roulette();

            Set<Album> albums = new HashSet<Album>();

            for (SavedAlbum savedAlbum : savedAlbums) {
                se.michaelthelin.spotify.model_objects.specification.Album gettedAlbum = savedAlbum.getAlbum();
                Album album = new Album(
                        gettedAlbum.getName(),
                        gettedAlbum.getHref(),
                        gettedAlbum.getTracks().getTotal(),
                        gettedAlbum.getReleaseDate(),
                        roulette,
                        gettedAlbum.getPopularity());

                Set<Track> tracks = new HashSet<Track>();
                for (TrackSimplified albumTrack : gettedAlbum.getTracks().getItems()) {
                    Track track = new Track(
                            albumTrack.getName(),
                            album,
                            albumTrack.getHref(),
                            albumTrack.getDurationMs());
                    tracks.add(track);
                }

                album.setTracks(tracks);

                Set<Artist> artists = new HashSet<Artist>();
                for (ArtistSimplified albumArtist : gettedAlbum.getArtists()) {
                    Artist artist = new Artist(
                            albumArtist.getName(),
                            album);
                    artists.add(artist);
                }

                album.setArtists(artists);

                albums.add(album);
            }

            roulette.setAlbums(albums);
            roulette.setUser(currentUser.get());

            rouletteRepository.save(roulette);

            return ResponseEntity.ok("Albums saved successfully!");

        } catch (ParseException | SpotifyWebApiException | IOException e) {
            logger.info("Error: " + e.getMessage());
            logger.info("StackTrace: " + e.getStackTrace());
            return ResponseEntity.badRequest().body(e.getStackTrace());
        }
    }
}
