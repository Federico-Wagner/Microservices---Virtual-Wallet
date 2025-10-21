package main

import (
	"log"
	"notifications/internal/consumer"
	"notifications/internal/email"
	"notifications/internal/service"
	"notifications/pkg/config"
)

func main() {
	cfg := config.LoadConfig()

	mailer := email.NewMailer(cfg.SMTPHost, cfg.SMTPPort, cfg.SMTPUser, cfg.SMTPPass)
	notifService := service.NewNotificationService(mailer)

	rabbitConsumer, err := consumer.NewRabbitConsumer(cfg.RabbitURL, cfg.QueueName, notifService)
	if err != nil {
		log.Fatalf("❌ Error creating RabbitMQ consumer: %v", err)
	}

	log.Println("🚀 Notification Service started and waiting for messages...")
	if err := rabbitConsumer.Consume(); err != nil {
		log.Fatalf("❌ Error consuming messages: %v", err)
	}
}



// package cmd
//
// import (
//     "fmt"
//     "net/http"
// )
//
// func main() {
//     go func() {
//         http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
//             fmt.Fprintf(w, "OK")
//         })
//         http.ListenAndServe(":8080", nil)
//     }()
//
//     // Acá arranca el consumer de RabbitMQ
//     startRabbitConsumer()
// }
//
