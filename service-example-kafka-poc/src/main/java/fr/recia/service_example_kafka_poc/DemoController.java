package fr.recia.service_example_kafka_poc;

import fr.recia.event_rest_client_kafka_poc.HttpNotificationClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String sendNotification(@RequestParam String target, @RequestParam String id, @RequestParam String title, @RequestParam String message) {
        if(target.equals("user")){
            System.out.println("Sent to user");
            notificationClient.sendNormalPriorityToUser(title, message, id);
        } else {
            System.out.println("Sent to group");
            notificationClient.sendNormalPriorityToGroup(title, message, id);
        }
        return "redirect:/?sent=true";
    }
}
