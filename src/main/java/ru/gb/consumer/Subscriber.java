package ru.gb.consumer;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Subscriber {
    private final static String EXCHANGER_NAME = "subscribes_exchanger";

    public static void main(String[] args) throws Exception {
        String topic = "";
        Scanner scanner = new Scanner(System.in);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);
        String queue = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queue);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(Thread.currentThread().getName() + " [x] Received '" + message + "'");
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
        });

        while(true)
        {
            System.out.println("Введите команду:");
            if (scanner.hasNext())
            {
                String[] input = scanner.nextLine().split(" ", 2);
                String command = input[0].trim();
                if (input.length == 2) {
                    topic = input[1].trim();
                }
                if (command.equals("subscribe")) {
                    System.out.println(" [*] Подписан на тему " + topic);
                    channel.queueBind(queue, EXCHANGER_NAME, topic);
                    System.out.println(" [*] Waiting for messages");
                } else if (command.equals("unsubscribe")) {
                    channel.queueUnbind(queue, EXCHANGER_NAME, topic);
                    System.out.println(" [*] Отписан от рассылки на тему " + topic);
                } else if (command.equalsIgnoreCase("Q"))
                {
                    break;
                } else {
                    System.out.println("Команда нераспознана");
                }
            }
        }
        System.exit(0);
    }
}
