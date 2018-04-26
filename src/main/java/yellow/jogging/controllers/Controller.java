package yellow.jogging.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import yellow.jogging.controllers.interfaces.ControllerCommand;
import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;

import java.util.HashMap;
import java.util.Map;

public abstract class Controller {

    ResponseEntity businessLogic(ControllerCommand command, HttpStatus status){
        Map<String, Object> answer = new HashMap<>();
        ResponseEntity response;
        try {
            boolean hasError = command.call(answer);
            if (hasError) {
                response = ResponseEntity.badRequest().body(answer);
            } else {
                if(status != null){
                    response = ResponseEntity.status(status).body(answer);
                }
                else{
                    response = ResponseEntity.ok(answer);
                }

            }
        } catch (UnatharizedAccessException e) {
            response = error(answer,"Can't authorize user",HttpStatus.UNAUTHORIZED );

        } catch (SessionCreationException e) {
            response = error(answer,"Error with database session connection. Try again later.",
                    HttpStatus.INTERNAL_SERVER_ERROR );
        }
        return response;
    }

    private ResponseEntity error(Map<String, Object> answer, String errorMessage, HttpStatus status){
        answer.put("errorMessage",errorMessage);
        return ResponseEntity.status(status).body(answer);
    }

}
