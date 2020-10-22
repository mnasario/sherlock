package com.sherlock.game.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(Marker.CLASS_NAME)
@Document(collection = Marker.COLLECTION_NAME)
public class Marker {

    public static final String CLASS_NAME = "Marker";
    public static final String COLLECTION_NAME = "markers";

    @Id
    private UUID id;
    private String description;
    private Double latitude;
    private Double longitude;
    private Collection<MarkerTag> tags;
}
