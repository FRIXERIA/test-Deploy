package sit.int221.sastt3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.sastt3.services.FileService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
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
    public ResponseEntity<String> fileUpload(@RequestParam("file") List<MultipartFile> files) {
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
            }

            return ResponseEntity.ok("You successfully uploaded " + files.size() + " file(s)! \n Contain: " + filenames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during file upload: " + e.getMessage());
        }
    }


    @DeleteMapping ("/{fileName:.+}")
    public String removeFile(@PathVariable String fileName){
        return fileService.deleteFile(fileName);
    }
}
