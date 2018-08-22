package akkatest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akkatest.FileParser.Parse;

public class FileScanner extends AbstractLoggingActor {
    static Props props(ActorRef parser) {
        return Props.create(FileScanner.class, () -> new FileScanner(parser));
    }

    private final ActorRef parser;

    private FileScanner(ActorRef parser) {
        this.parser = parser;
    }

    static class Scan {
        private List<Path> filePaths;

        Scan(String directory) throws IOException {
            try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
                this.filePaths = paths
                    .filter(Files::isRegularFile)
                    .map(Path::normalize)
                    .collect(Collectors.toList());
            }
        }

        List<Path> getFilePaths() {
            return filePaths;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Scan.class, scan -> {
                if (scan.getFilePaths().size() > 0)
                    parser.tell(new Parse(scan.getFilePaths().get(0).toAbsolutePath().toString()), getSelf()); //just handle the first file in the directory to make it simpler
                else {
                    log().info("No any file in the directory. System will stop.");
                    context().system().terminate();
                }
            })
            .build();
    }
}
