package com.coditory.mongo.uploader;

import com.coditory.mongo.shared.Arguments;
import lombok.Value;

@Value
class UploadArguments {
    static UploadArguments extract(Arguments arguments) {
        return new UploadArguments(
                arguments.getRequiredStringArgument("--mongo", "-m"),
                arguments.getRequiredStringArgument("--dir", "-d")
        );
    }

    String mongoUrl;
    String dir;
}
