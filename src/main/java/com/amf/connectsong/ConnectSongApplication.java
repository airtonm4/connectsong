package com.amf.connectsong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ConnectSong", version = "1", description = "API desenvolvida para a escolha de albuns de musica no Spotify"))
public class ConnectSongApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectSongApplication.class, args);
	}

}
