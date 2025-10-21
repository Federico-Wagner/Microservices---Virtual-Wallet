package service

import (
	"encoding/json"
	"log"
	"notifications/internal/email"
)

type Notification struct {
	To      string `json:"to"`
	Subject string `json:"subject"`
	Message string `json:"message"`
}

type NotificationService struct {
	Mailer *email.Mailer
}

func NewNotificationService(mailer *email.Mailer) *NotificationService {
	return &NotificationService{Mailer: mailer}
}

func (s *NotificationService) ProcessMessage(msg []byte) {
	var notif Notification
	if err := json.Unmarshal(msg, &notif); err != nil {
		log.Printf("❌ Error parsing message: %v", err)
		return
	}

	log.Printf("📧 Sending email to %s...", notif.To)
	if err := s.Mailer.Send(notif.To, notif.Subject, notif.Message); err != nil {
		log.Printf("❌ Error sending email: %v", err)
	} else {
		log.Printf("✅ Email sent successfully to %s", notif.To)
	}
}
