package akkatest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akkatest.Aggregator.EndOfFile;
import akkatest.Aggregator.Line;
import akkatest.Aggregator.StartOfFile;

public class FileParser extends AbstractLoggingActor {
    static Props props(ActorRef aggregator) {
        return Props.create(FileParser.class, () -> new FileParser(aggregator));
    }

    private final ActorRef aggregator;

    private FileParser(ActorRef aggregator) {
        this.aggregator = aggregator;
    }

    static class Parse {
        private String path;

        Parse(String filePath) {
            this.path = filePath;
        }

        private String getPath() {
            return path;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Parse.class, parse -> {
                aggregator.tell(new StartOfFile(), getSelf());
                try (Stream<String> stream = Files.lines(Paths.get(parse.getPath()))) {
                    stream.forEach(line -> aggregator.tell(new Line(line), getSelf()));
                }
                aggregator.tell(new EndOfFile(), getSelf());
            })
            .build();
    }
}
