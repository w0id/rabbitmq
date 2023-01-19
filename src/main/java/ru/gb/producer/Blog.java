package ru.gb.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class Blog {
    private final static String EXCHANGER_NAME = "subscribes_exchanger";

    public static void main(String[] args) throws Exception {
        String message = "";
        System.out.println("Введите сообщение:");
        Scanner scanner = new Scanner(System.in);
        String[] input = scanner.nextLine().split(" ", 2);
        String topic = input[0].trim();
        if (input.length == 2) {
            message = input[1].trim();
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()){
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);

            channel.basicPublish(EXCHANGER_NAME, topic, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
