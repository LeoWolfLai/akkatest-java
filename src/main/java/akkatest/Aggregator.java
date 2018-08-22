package akkatest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class Aggregator extends AbstractLoggingActor {
    static Props props() {
        return Props.create(Aggregator.class, Aggregator::new);
    }

    private int wordCount;

    private Aggregator() {
    }

    static class StartOfFile {
        StartOfFile() {
        }
    }

    static class Line {
        private String line;

        Line(String line) {
            this.line = line;
        }
    }

    static class EndOfFile {
        EndOfFile() {
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(StartOfFile.class, this::initialWork)
            .match(Line.class, this::processLine)
            .match(EndOfFile.class, this::printWordsCount)
            .build();
    }

    private void initialWork(StartOfFile msg) {
        this.wordCount = 0;
    }

    private void processLine(Line msg) {
        String str = msg.line;
        List<String> splitWord = Stream.of(str.split(" ")).collect(Collectors.toList());
        this.wordCount += splitWord.size();
    }

    private void printWordsCount(EndOfFile msg) {
        log().info("word count in file is : " + this.wordCount);
        log().info("Press Enter to stop the system.");
    }
}
