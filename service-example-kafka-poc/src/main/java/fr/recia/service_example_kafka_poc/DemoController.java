package fr.recia.service_example_kafka_poc;

import fr.recia.event_rest_client_kafka_poc.HttpNotificationClient;
import fr.recia.model_kafka_poc.model.Channel;
import fr.recia.model_kafka_poc.model.Priority;
import fr.recia.model_kafka_poc.model.TargetType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class DemoController {

    private final HttpNotificationClient notificationClient;

    public DemoController(HttpNotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/send")
    public String sendNotification(@RequestParam String target, @RequestParam String id, @RequestParam String title, @RequestParam String message, @RequestParam String link) {
        if(target.equals("user")){
            System.out.println("Sent to user");
            notificationClient.sendNotification(title, message, link, id, List.of(Channel.WEB, Channel.PUSH, Channel.MAIL), Priority.NORMAL, TargetType.USER);
        } else {
            System.out.println("Sent to group");
            notificationClient.sendNotification(title, message, link, id, List.of(Channel.WEB, Channel.PUSH, Channel.MAIL), Priority.NORMAL, TargetType.GROUP);
        }
        return "redirect:/?sent=true";
    }
}
