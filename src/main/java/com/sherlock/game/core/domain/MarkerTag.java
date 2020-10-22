package com.sherlock.game.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(MarkerTag.CLASS_NAME)
@Document(collection = MarkerTag.COLLECTION_NAME)
public class MarkerTag {

    public static final String CLASS_NAME = "MarkerTag";
    public static final String COLLECTION_NAME = "tags";

    @Id
    private UUID id;
    private String description;
}
