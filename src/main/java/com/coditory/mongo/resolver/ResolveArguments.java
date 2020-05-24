package com.coditory.mongo.resolver;

import com.coditory.mongo.shared.Arguments;
import lombok.Value;

@Value
class ResolveArguments {
    static ResolveArguments extract(Arguments arguments) {
        return new ResolveArguments(
                arguments.getRequiredStringArgument("--mongo", "-m"),
                arguments.getRequiredStringArgument("--resolve", "-r")
        );
    }

    String mongoUrl;
    String resolve;
}
