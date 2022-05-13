package dev.kofe.controller;

/**
 *
 *  InfoController
 *
 *  GET:  /contacts
 *  POST: /sendMessage
 *  GET:  /delivery
 *
 */

import dev.kofe.model.ContactMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@Controller
public class InfoController {

    @Autowired private Environment env;

    @GetMapping("/contacts")
    public String showContactPage (@CookieValue(name = "email", required = false) String emailFromCookies,
                                   Model model) {
        ContactMessage message = new ContactMessage();
        model.addAttribute("message", message);
        if (emailFromCookies != null) message.setEmail(emailFromCookies);

        return "contacts";
    }

    // message (in form of email) sender controller
    @PostMapping("/sendMessage")
    public String sendMessage(@ModelAttribute("message") ContactMessage contactMessage, Model model) {

        String resultMessage = "";

        if (isEmailAddressValid(contactMessage.getEmail())) {

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setProtocol(env.getProperty("email_sender_protocol"));
            mailSender.setHost(env.getProperty("email_sender_host_server"));
            mailSender.setPort(Integer.parseInt(env.getProperty("email_sender_port")));
            mailSender.setUsername(env.getProperty("email_sender_username"));
            mailSender.setPassword(env.getProperty("email_sender_password"));

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.debug", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(env.getProperty("admin_email_for_messages_from_site"));
            message.setFrom(env.getProperty("email_sender_username"));
            message.setSubject("Message from the web-site of Dulces Duenas!");

            String text = "Message body:\n\n\n" +
                           contactMessage.getText() +
                          "\n\n=======================================\n" +
                          "Sent from: \n" +
                          "Name: " + contactMessage.getName() + "\n" +
                          "Username on site: " + contactMessage.getUserName() + "\n" +
                          "Email: " + contactMessage.getEmail() + "\n" +
                          "=======================================";

            message.setText(text);

            try {
                mailSender.send(message);
                resultMessage = "Thank you so much! Your message was sent!";
                contactMessage.clear();
            } catch (Exception e) {
                resultMessage = "Something was happened with the emailing";
                // @ToDo log
            }

        } else {
            resultMessage = "Please, be sure your email is correct :)";
        }

        model.addAttribute("message", contactMessage);
        model.addAttribute("resultMessage", resultMessage);
        return "contacts";
    }

    @GetMapping("/delivery")
    public String showDeliveryPage () {
        return "delivery";
    }

    private boolean isEmailAddressValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
