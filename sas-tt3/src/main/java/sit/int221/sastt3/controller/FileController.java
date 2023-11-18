package sit.int221.sastt3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.sastt3.DTO.FileName;
import sit.int221.sastt3.DTO.MultiPartAndEmail;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Email;
import sit.int221.sastt3.entities.Files;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.FileRepo;
import sit.int221.sastt3.services.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {
    private final FileService fileService;
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    @Autowired
    private AnnouncementRepo announcementRepo ;
    @Autowired
    private FileRepo fileRepo;

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileService.loadFileAsResource(filename);
        MediaType mediaType =fileService.determineMediaType(filename);

        // Create a ResponseEntity and set the content type
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(file);
    }
    private static final int maximumFileUpload = 5;
    private static final long  maximumFileSize = 20*1024*1024; //20MB

    @PostMapping("")
    public ResponseEntity<String> fileUpload(@ModelAttribute MultiPartAndEmail multiPartAndEmail) {
        List<MultipartFile> files = multiPartAndEmail.getFile();
        String title = multiPartAndEmail.getTitle();
        Announcement announcement = announcementRepo.findByTitle(title);
        List<String> filenames = new ArrayList<>();
        try {
            if (files.size() > maximumFileUpload) {
                return ResponseEntity.badRequest()
                        .body("The announcement can have at most 5 attachments.");
            }

            for (MultipartFile file : files) {
                System.out.println("sss");
                if (file.getSize() > maximumFileSize) {
                    return ResponseEntity.badRequest()
                            .body("Error: File size exceeds the limit (20 MB): " + file.getOriginalFilename());
                }
                String uploadedFilename = fileService.store(file);
                filenames.add(uploadedFilename);
                    fileService.addFileToDB(filenames,announcement.getId());


            }

//            fileService.addFileToDB(filenames,announcement.getId());
            return ResponseEntity.ok("You successfully uploaded " + files.size() + " file(s)! \n Contain: " + filenames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during file upload: " + e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<String> fileUpdate(@ModelAttribute MultiPartAndEmail multiPartAndEmail) {
        List<MultipartFile> files = multiPartAndEmail.getFile();
        String title = multiPartAndEmail.getTitle();
        Announcement announcement = announcementRepo.findByTitle(title);

        try {
            if (files.size() > maximumFileUpload) {
                return ResponseEntity.badRequest().body("The announcement can have at most 5 attachments.");
            }

            List<Files> existingFiles = fileRepo.findByIdAnnounce(announcement.getId());

            List<String> newFilenames = new ArrayList<>();
            List<String> existingFilenames = existingFiles.stream().map(Files::getFileName).collect(Collectors.toList());

            for (MultipartFile file : files) {
                if (file.getSize() > maximumFileSize) {
                    return ResponseEntity.badRequest().body("Error: File size exceeds the limit (20 MB): " + file.getOriginalFilename());
                }

                String uploadedFilename = fileService.store(file);
                newFilenames.add(uploadedFilename);

                if (!existingFilenames.contains(file.getOriginalFilename())) {
                    fileService.addFileToDB(Collections.singletonList(uploadedFilename), announcement.getId());
                }
            }

            // Delete old files that are not in the new file list
            for (Files existingFile : existingFiles) {
                if (!newFilenames.contains(existingFile.getFileName())) {
                    fileService.deleteFile(existingFile.getFileName());
                    fileService.deleteFileDB(existingFile.getIdFiles());
                }
            }

            return ResponseEntity.ok("You successfully uploaded " + files.size() + " file(s)!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during file upload: " + e.getMessage());
        }
    }
//    public ResponseEntity<String> fileUpdate(@ModelAttribute MultiPartAndEmail multiPartAndEmail) {
//        List<MultipartFile> files = multiPartAndEmail.getFile();
//        String title = multiPartAndEmail.getTitle();
//        Announcement announcement = announcementRepo.findByTitle(title);
//        List<Files> nameFiles =fileRepo.findByIdAnnounce(announcement.getId());
//
//        List<String> filenames = new ArrayList<>();
//
//        try {
//            if (files.size() > maximumFileUpload) {
//                return ResponseEntity.badRequest()
//                        .body("The announcement can have at most 5 attachments.");
//            }
//
//            for (MultipartFile file : files) {
//                System.out.println("sss");
//                if (file.getSize() > maximumFileSize) {
//                    return ResponseEntity.badRequest()
//                            .body("Error: File size exceeds the limit (20 MB): " + file.getOriginalFilename());
//                }
//                else {
//                    for (Files name : nameFiles){
//                        if(!name.getFileName().equals(file.getOriginalFilename())){
//                            System.out.println("abc");
//                            System.out.println("cds");
//                            String uploadedFilename = fileService.store(file);
//                            filenames.add(uploadedFilename);
//                            fileService.addFileToDB(filenames, announcement.getId());
//                            fileService.deleteFile(name.getFileName());
//                            fileService.deleteFileDB(name.getIdFiles());
//                        }
//                    }
////                String uploadedFilename = fileService.store(file);
////                filenames.add(uploadedFilename);
//                }
//            }
//
////                fileService.addFileToDB(filenames,announcement.getId());
//            return ResponseEntity.ok("You successfully uploaded " + files.size() + " file(s)! \n Contain: " + filenames);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error occurred during file upload: " + e.getMessage());
//        }
//    }

    @DeleteMapping ("/{fileName:.+}")
    public String removeFile(@PathVariable String fileName){
        return fileService.deleteFile(fileName);
    }

    @DeleteMapping ("/fileName/{id}")
    public void removeFileDB(@PathVariable Integer id){
        fileService.deleteFileDB(id);
    }
@DeleteMapping ("/fileStep/{id}")
public void removeStep(@PathVariable Integer id, @RequestBody FileName fileName){
         fileService.deleteFileByStep(id,fileName.getFileName());
}

    @GetMapping ("/fileName/{id}")
    public List <Files> nameFiles (@PathVariable Integer id){
        return fileService.getDataByIdAnnounce(id);
    }
}
