package de.propra2.ausleiherino24.features.chat;

import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChatController {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(final UserService userService,
            final SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * sends a chat message.
     *
     * @param principal user that sends the message
     * @param chatMessage message that is send
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(final @Payload ChatMessage chatMessage,
            final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);
        chatMessage.setSender(user.getUsername());
        return chatMessage;
    }

    /**
     * adds user to chat.
     *
     * @param principal user which should be added to chat
     * @param headerAccessor SimpleMessageHeaderAccessor
     * @param chatMessage chat message
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(final @Payload ChatMessage chatMessage,
            final SimpMessageHeaderAccessor headerAccessor,
            final Principal principal) {
        // Add username in web socket session
        final User user = userService.findUserByPrincipal(principal);

        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        chatMessage.setSender(user.getUsername());
        return chatMessage;
    }

    /**
     * shows chatboard.
     *
     * @param sessionId id of the session
     */
    @GetMapping("/chatBoard")
    public ModelAndView chatBoard(final @Header("simpSessionId") String sessionId) {
        final ModelAndView mav = new ModelAndView("/chatBoard");
        mav.addObject("sessionId", sessionId);
        return mav;
    }

    /**
     * user chat.
     *
     * @param principal sender of the message
     * @param msg message that should be send
     */
    @MessageMapping("/chat.privateMessage")
    public void sendSpecific(final @Payload ChatMessage msg, final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);
        msg.setSender(user.getUsername());

        simpMessagingTemplate.convertAndSendToUser(
                msg.getReceiver(), "/user/queue/specific-user", msg);
    }

}

