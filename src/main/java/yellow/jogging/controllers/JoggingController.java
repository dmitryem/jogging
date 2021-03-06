package yellow.jogging.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yellow.jogging.beans.Image;
import yellow.jogging.beans.Jogging;
import yellow.jogging.beans.Statistic;
import yellow.jogging.cloudinary.CloudinaryUploader;
import yellow.jogging.cloudinary.exceptions.CloudinaryInitializeException;
import yellow.jogging.cloudinary.exceptions.CloudinaryUploadException;
import yellow.jogging.db.dao.JoggingDao;
import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api/authenticated/jogging")
public class JoggingController extends Controller {

    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${custom.folder.path}")
    private String imagePath;

    @Autowired
    private JoggingDao joggingDao;


    @GetMapping
    public ResponseEntity getJogging(@RequestParam(value = "id", required = false) Integer id,
                                     @RequestParam(value = "joggingOffset", required = false, defaultValue = "0") int offset,
                                     @RequestParam(value = "joggingPerPage", required = false, defaultValue = "50") int perPage) {

        return businessLogic(answer -> {
            boolean hasError = false;
            if (id == null) {
                List<Jogging> joggingList = joggingDao.getJoggingList(offset,perPage);
                answer.put("list", joggingList);
                answer.put("totalNumRecords", joggingDao.getJoggingCount());
            } else {
                Jogging jogging = joggingDao.getJogging(id);
                if (jogging == null) {
                    hasError = true;
                    answer.put("errorMessage", String.format("Can't find jogging for id %s", id));
                } else {
                    answer.put("jogging", jogging);
                }
            }
            return hasError;
        }, null);
    }

    @PostMapping
    public ResponseEntity createJogging(@RequestBody Jogging jogging) {
        return businessLogic(answer -> {
            boolean hasError = false;
            List<String> errors = validateJogging(jogging);
            if (errors.size() == 0) {
                joggingDao.createJogging(jogging);
                answer.put("jogging", jogging);
            } else {
                hasError = true;
                answer.put("errors", errors);
            }
            return hasError;
        }, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateJogging(@RequestPart(name = "jogging") Jogging jogging) {
        return businessLogic(answer -> {
            boolean hasError = false;
            List<String> errors = validateJogging(jogging);
            if (errors.size() == 0) {
                boolean success = joggingDao.updateJogging(jogging);
                if (success) {
                    answer.put("jogging", jogging);
                } else {
                    hasError = true;
                    answer.put("errorMessage", "Can't update jogging. Check input data.");
                }

            } else {
                hasError = true;
                answer.put("errors", errors);
            }
            return hasError;
        }, HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    public ResponseEntity deleteJogging(@RequestParam(value = "id") int id) {
        return businessLogic(answer -> {
            boolean success = joggingDao.deleteJogging(id);
            if (!success) {
                answer.put("errorMessage", String.format("Can't find jogging for id %s to delete", id));
            }
            return !success;
        }, HttpStatus.NO_CONTENT);


    }

    @GetMapping("/statistic")
    public ResponseEntity getJoggingStatistic(@RequestParam(value = "weeksOffset", required = false, defaultValue = "0") int offset,
                                              @RequestParam(value = "weeksPerPage", required = false, defaultValue = "50") int perPage) {
        return businessLogic(
                answer -> {
                    boolean hasError = false;
                    List<Statistic> statistics = joggingDao.getJoggingListForPeriod(offset, perPage);
                    if (statistics != null) {
                        for (Statistic statistic : statistics) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setFirstDayOfWeek(Calendar.MONDAY);
                            calendar.set(Calendar.WEEK_OF_YEAR, statistic.getWeekNumber() + 1);
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                            String begin = dt.format(calendar.getTime());
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                            String end = dt.format(calendar.getTime());
                            statistic.setWeekRange("( " + begin + " / " + end + " )");
                        }
                        answer.put("statistic", statistics);
                        answer.put("totalNumWeeks", joggingDao.getWeeksCount());

                    } else {
                        hasError = true;
                        answer.put("errorMessage","Can't get statistic");
                    }
                    return hasError;

                }, null);
    }


    @PostMapping("/images")
    public ResponseEntity uploadImages(@RequestParam(value = "id") int id, @RequestParam("files") MultipartFile[] files){
        return businessLogic(answer -> {
            boolean hasError = false;
            if(files.length != 0){
                if(null != joggingDao.getJogging(id)){
                    CloudinaryUploader uploader = CloudinaryUploader.getInstance();
                    if(uploader != null){
                        List<Image> images = uploader.uploadImage(id,files);
                        if(images != null){
                            boolean updated = joggingDao.addImagesToJogging(id,images);
                            hasError = !updated;
                        }else {
                            throw new CloudinaryUploadException("Can't upload images");
                        }
                    }else{
                        throw new CloudinaryInitializeException("Can't initialize cloudinary");
                    }
                }else{
                    hasError = true;
                    answer.put("errorMessage",String.format("Can't find jogging for id %s", id));
                }

            }else{
                hasError = true;
                answer.put("errorMessage","Nothing to upload");
            }
            return hasError;
        },HttpStatus.CREATED);
    }

    private List<String> validateJogging(Jogging jogging) {
        List<String> errorMessages = new ArrayList<>();
        if (jogging != null) {
            if (jogging.getDate() == null) {
                errorMessages.add("Date should be present");
            }
            if (jogging.getDistance() <= 0) {
                errorMessages.add("Distance should be positive");
            }
            if (jogging.getDuration() <= 0) {
                errorMessages.add("Duration should be positive");
            }
        } else {
            errorMessages.add("Jogging can't be empty");
        }
        return errorMessages;
    }


}
