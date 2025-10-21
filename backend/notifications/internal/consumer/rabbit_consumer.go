package consumer

import (
	"log"
	"notifications/internal/service"

	"github.com/rabbitmq/amqp091-go"
)

type RabbitConsumer struct {
	conn    *amqp091.Connection
	channel *amqp091.Channel
	queue   amqp091.Queue
	service *service.NotificationService
}

func NewRabbitConsumer(rabbitURL, queueName string, svc *service.NotificationService) (*RabbitConsumer, error) {
	conn, err := amqp091.Dial(rabbitURL)
	if err != nil {
		return nil, err
	}

	ch, err := conn.Channel()
	if err != nil {
		return nil, err
	}

	q, err := ch.QueueDeclare(
		queueName,
		true,  // durable
		false, // auto-delete
		false, // exclusive
		false, // no-wait
		nil,
	)
	if err != nil {
		return nil, err
	}

	return &RabbitConsumer{
		conn:    conn,
		channel: ch,
		queue:   q,
		service: svc,
	}, nil
}

func (r *RabbitConsumer) Consume() error {
	msgs, err := r.channel.Consume(
		r.queue.Name,
		"",
		true,  // auto-ack
		false, // exclusive
		false, // no-local
		false, // no-wait
		nil,
	)
	if err != nil {
		return err
	}

	log.Printf("📡 Listening for messages on queue: %s", r.queue.Name)

	forever := make(chan bool)
	go func() {
		for d := range msgs {
			r.service.ProcessMessage(d.Body)
		}
	}()
	<-forever
	return nil
}
