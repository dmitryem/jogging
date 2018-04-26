package yellow.jogging.controllers.interfaces;

import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;

import java.util.Map;

public interface ControllerCommand {

    boolean call(Map<String,Object> answer) throws UnatharizedAccessException, SessionCreationException;

}
