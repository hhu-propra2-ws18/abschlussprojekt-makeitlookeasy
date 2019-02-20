package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.ChatMessage;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.text.SimpleDateFormat;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChatController {
    private final UserService userService;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        User user = userService.findUserByPrincipal(principal);
        chatMessage.setSender(user.getUsername());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor,
                                Principal principal) {
        // Add username in web socket session
        User user = userService.findUserByPrincipal(principal);

        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        chatMessage.setSender(user.getUsername());
        return chatMessage;
    }

    @GetMapping("/chatBoard")
    public ModelAndView chatBoard(@Header("simpSessionId") String sessionId) {
        ModelAndView mav = new ModelAndView("/chatBoard");
        mav.addObject("sessionId", sessionId);
        return mav;
    }

    /* user chat */
    @MessageMapping("/chat.privateMessage")
    public void sendSpecific(@Payload ChatMessage msg, Principal principal) {
        User user = userService.findUserByPrincipal(principal);
        msg.setSender(user.getUsername());

        simpMessagingTemplate.convertAndSendToUser(
                msg.getReceiver(), "/user/queue/specific-user", msg);
    }

}

