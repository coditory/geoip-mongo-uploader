package com.coditory.mongo;

import com.coditory.mongo.resolver.ResolveCommand;
import com.coditory.mongo.shared.Arguments;
import com.coditory.mongo.uploader.UploadCommand;
import lombok.AllArgsConstructor;

import static com.coditory.mongo.shared.Logger.stderr;
import static com.coditory.mongo.shared.Logger.stdout;

@AllArgsConstructor
public class Application {
    public static void main(String[] args) {
        try {
            Arguments arguments = Arguments.parse(args);
            new Application(arguments)
                    .run();
        } catch (Throwable e) {
            e.printStackTrace();
            error("Error: %s", e);
        }
    }

    private final Arguments arguments;

    void run() {
        if (arguments.isEmpty() || arguments.contains("--help", "-h")) {
            printHelp();
        } else if (arguments.contains("--resolve", "-r")) {
            resolve();
        } else {
            uploadData();
        }
    }

    void printHelp() {
        stdout("MongoDB GeoIp Uploader");
        stdout("======================");
        stdout("Uploads GeoIp CSV data to MongoDB");
        stdout("Repository: https://github.com/coditory/mongo-geoip-uploader");
        stdout("");
        stdout("Usage:");
        stdout("");
        stdout("Run the uploader:");
        stdout("  java -jar geoip-mongo-uploader \\");
        stdout("      -m mongodb://localhost:27017/my-database \\");
        stdout("      -d ./geoip");
        stdout("");
        stdout("Resolve an address:");
        stdout("  java -jar geoip-mongo-uploader \\");
        stdout("      -m mongodb://localhost:27017/my-database \\");
        stdout("      -r 5.133.248.238");
        stdout("");
        stdout("Parameters:");
        stdout("  --mongo [-m]       - mongo connection string");
        stdout("  --dir [-d]         - geoip data directory");
        stdout("");
        stdout("Optional parameters:");
        stdout("  --help [-h]        - print manual");
        stdout("");
    }

    private void resolve() {
        ResolveCommand.execute(arguments);
    }

    private void uploadData() {
        UploadCommand.execute(arguments);
    }

    private static void error(String message, Object... args) {
        stderr(String.format(message, args));
        System.exit(1);
    }
}
