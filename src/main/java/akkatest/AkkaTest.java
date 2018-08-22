package akkatest;

import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akkatest.FileScanner.Scan;

public class AkkaTest {
    public static void main(String args[]) {
        ActorSystem system = ActorSystem.create("akkaTest");
        try {
            final ActorRef aggregator = system.actorOf(Aggregator.props(), "aggregator");
            final ActorRef fileParser = system.actorOf(FileParser.props(aggregator), "fileParser");
            final ActorRef fileScanner = system.actorOf(FileScanner.props(fileParser), "fileScanner");

            System.out.println("Enter absolute directory path then press enter:");
            Scanner scanner = new Scanner(System.in);
            String directory = scanner.nextLine();

            fileScanner.tell(new Scan(directory), ActorRef.noSender());
            System.in.read(); //block actor system to prevent termination
        } catch (Exception e) {
            System.out.println("Catch exception:" + e.getMessage());
        } finally {
            system.terminate();
        }
    }
}
