package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	RabbitURL string
	QueueName string
	SMTPHost  string
	SMTPPort  string
	SMTPUser  string
	SMTPPass  string
}

func LoadConfig() *Config {
	_ = godotenv.Load() // carga .env si existe

	cfg := &Config{
		RabbitURL: getEnv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/"),
		QueueName: getEnv("RABBITMQ_QUEUE", "notifications_transaction"),
		SMTPHost:  getEnv("SMTP_HOST", "smtp.gmail.com"),
		SMTPPort:  getEnv("SMTP_PORT", "587"),
		SMTPUser:  getEnv("SMTP_USER", ""),
		SMTPPass:  getEnv("SMTP_PASS", ""),
	}

	log.Printf("✅ Config loaded: RabbitMQ=%s, Queue=%s", cfg.RabbitURL, cfg.QueueName)
	return cfg
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}
