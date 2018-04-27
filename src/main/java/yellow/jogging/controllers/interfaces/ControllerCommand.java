package yellow.jogging.controllers.interfaces;

import yellow.jogging.cloudinary.exceptions.CloudinaryInitializeException;
import yellow.jogging.cloudinary.exceptions.CloudinaryUploadException;
import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;

import java.util.Map;

public interface ControllerCommand {

    boolean call(Map<String,Object> answer) throws UnatharizedAccessException, SessionCreationException, CloudinaryInitializeException, CloudinaryUploadException;

}
