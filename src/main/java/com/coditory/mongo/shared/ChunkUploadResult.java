package com.coditory.mongo.shared;

import lombok.Value;

@Value
public class ChunkUploadResult {
    int uploadedItems;
    long millis;
}
