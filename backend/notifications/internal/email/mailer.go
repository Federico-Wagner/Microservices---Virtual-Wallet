package email

import (
	"fmt"
	"net/smtp"
)

type Mailer struct {
	Host string
	Port string
	User string
	Pass string
}

func NewMailer(host, port, user, pass string) *Mailer {
	return &Mailer{host, port, user, pass}
}

func (m *Mailer) Send(to, subject, body string) error {
	addr := fmt.Sprintf("%s:%s", m.Host, m.Port)
	auth := smtp.PlainAuth("", m.User, m.Pass, m.Host)

	msg := []byte(fmt.Sprintf("Subject: %s\r\n\r\n%s", subject, body))
	err := smtp.SendMail(addr, auth, m.User, []string{to}, msg)
	if err != nil {
		return fmt.Errorf("failed to send mail: %v", err)
	}
	return nil
}
