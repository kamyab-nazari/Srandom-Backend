package htw.berlin.webtech.srandom.web;

import htw.berlin.webtech.srandom.service.SongService;
import htw.berlin.webtech.srandom.web.SrandomRestController;
import htw.berlin.webtech.srandom.web.api.Song;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SrandomRestController.class)
class SrandomRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongService songService;

    @Test
    @DisplayName("should return found song from song service")
    void should_return_found_song_from_song_service() throws Exception {
        var song = List.of(
                new Song(1, "Heart Attack", "Noizy X Loredana",
                        2022, "https://www.youtube.com/watch?v=qhrdHl1LZH4", false, false),
                new Song(2, "Tequila", "Tayna",
                        2022, "https://www.youtube.com/watch?v=I4ZSvYLnOcQ", false, false)
        );
        doReturn(song).when(songService).findAll();

        mockMvc.perform(get("/api/v1/songs/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Heart Attack"))
                .andExpect(jsonPath("$.[0].author").value("Noizy X Loredana"))
                .andExpect(jsonPath("$.[0].releaseYear").value(2022))
                .andExpect(jsonPath("$.[0].songLink").value("https://www.youtube.com/watch?v=qhrdHl1LZH4"))
                .andExpect(jsonPath("$.[0].isOriginal").value(false))
                .andExpect(jsonPath("$.[0].isFavorite").value(false))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].title").value("Tequila"))
                .andExpect(jsonPath("$.[1].author").value("Tayna"))
                .andExpect(jsonPath("$.[1].releaseYear").value(2022))
                .andExpect(jsonPath("$.[1].songLink").value("https://www.youtube.com/watch?v=I4ZSvYLnOcQ"))
                .andExpect(jsonPath("$.[1].isOriginal").value(false))
                .andExpect(jsonPath("$.[1].isFavorite").value(false));
    }

    @Test
    @DisplayName("should return random songs from song service")
    void should_return_randomSongs_from_song_service() throws Exception {
        var randomSong = List.of(
                new Song(5, "All Night", "Melinda Ademi",
                        2022, "https://www.youtube.com/watch?v=Vfv9Qky427Q", false, true),
                new Song(13, "What's My Name", "Rihanna X Drake",
                        2010, "https://www.youtube.com/watch?v=U0CGsw6h60k", false, true)
        );
        doReturn(randomSong).when(songService).findAll();
    }
    @Test
    @DisplayName("should return found favourite songs from song service")
    void should_return_found_favouriteSongs_from_song_service() throws Exception {
        var song = List.of(
                new Song(5, "All Night", "Melinda Ademi",
                        2022, "https://www.youtube.com/watch?v=Vfv9Qky427Q", false, true),
                new Song(13, "What's My Name", "Rihanna X Drake",
                        2010, "https://www.youtube.com/watch?v=U0CGsw6h60k", false, true)
        );
        doReturn(song).when(songService).findAllIsFavoriteTrue();

        mockMvc.perform(get("/api/v1/songs/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].title").value("All Night"))
                .andExpect(jsonPath("$.[0].author").value("Melinda Ademi"))
                .andExpect(jsonPath("$.[0].releaseYear").value(2022))
                .andExpect(jsonPath("$.[0].songLink").value("https://www.youtube.com/watch?v=Vfv9Qky427Q"))
                .andExpect(jsonPath("$.[0].isOriginal").value(false))
                .andExpect(jsonPath("$.[0].isFavorite").value(true))
                .andExpect(jsonPath("$.[1].id").value(13))
                .andExpect(jsonPath("$.[1].title").value("What's My Name"))
                .andExpect(jsonPath("$.[1].author").value("Rihanna X Drake"))
                .andExpect(jsonPath("$.[1].releaseYear").value(2010))
                .andExpect(jsonPath("$.[1].songLink").value("https://www.youtube.com/watch?v=U0CGsw6h60k"))
                .andExpect(jsonPath("$.[1].isOriginal").value(false))
                .andExpect(jsonPath("$.[1].isFavorite").value(true));
    }


    @Test
    @DisplayName("should return 404 if song is not found")
    void should_return_404_if_song_is_not_found() throws Exception {
        // given
        doReturn(null).when(songService).findById(anyLong());

        // when
        mockMvc.perform(get("/api/v1/songs/123"))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 200 http status and Location header when creating a song")
    void should_return_200_http_status_and_location_header_when_creating_a_song() throws Exception {
        // given
        String songToCreateAsJson = "{\"title\": \"Tequila\", \"author\":\"Tayna\", \"releaseYear\":\"2022\", \"songLink\": \"0\", \"isOriginal\": \"false\", \"isFavorite\":\"false\"}";
        var song = new Song(123, null, null, 2022, null, false, false);
        doReturn(song).when(songService).create(any());

        // when
        mockMvc.perform(
                        post("/api/v1/songs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(songToCreateAsJson)
                )
                // then
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().equals(123));
    }

    @Test
    @DisplayName("should validate create song request")
    void should_validate_create_song_request() throws Exception {
        // given
        String songToCreateAsJson = "{\"title\": \"Tequila\", \"author\":\"\", \"releaseYear\":, \"songLink\":\"0\",\"isOriginal\":\"false\", \"isFavorite\":\"false\"}";

        // when
        mockMvc.perform(
                        post("/api/v1/songs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(songToCreateAsJson)
                )
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should validate favourite song request")
    void should_validate_favourite_song_request() throws Exception {
        // given
        String songToCreateAsJson = "{\"title\": \"All Night\", \"author\":\"\", \"releaseYear\":, \"songLink\":\"0\",\"isOriginal\":\"false\", \"isFavorite\":\"true\"}";

        // when
        mockMvc.perform(
                        post("/api/v1/songs/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(songToCreateAsJson)
                )
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should validate removed song from favourite list")
    void should_validate_removed_song_from_favourite_list() throws Exception {
        // given
        String songToCreateAsJson = "{\"title\": \"Say My Name\", \"author\":\"\", \"releaseYear\":, \"songLink\":\"0\",\"isOriginal\":\"false\", \"isFavorite\":\"false\"}";

        // when
        mockMvc.perform(
                        post("/api/v1/songs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(songToCreateAsJson)
                )
                // then
                .andExpect(status().isBadRequest());
    }
}


