package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.time.LocalDateTime;

public class MessageValidation {
    public static void validateNewMessage(Message message) throws ValidationException {
        if (message.getContent().isBlank()) {
            throw new ValidationException("Message content must not only consist of white space characters!");
        }
        if (message.getExpirationDate() != null && message.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Message expiration date must be in the future!");
        }
    }
}
