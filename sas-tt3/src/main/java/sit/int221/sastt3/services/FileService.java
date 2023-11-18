package sit.int221.sastt3.services;

import io.minio.*;
import io.minio.errors.*;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.sastt3.entities.Files;
import sit.int221.sastt3.exception.ItemNotFoundException;
import sit.int221.sastt3.repositories.FileRepo;
import sit.int221.sastt3.utils.FileStorageProperties;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class FileService {
    private final MinioClient minioClient;

    @Autowired
    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
    @Value("${minio.bucket-name}")
    private String bucketName;
    @Autowired
    private FileRepo fileRepo;

    public List<Files> getAllData (){
        return fileRepo.findAll();
    }

    public List<Files> getDataByIdAnnounce (Integer id){
        return fileRepo.findByIdAnnounce(id);
    }

    public Files getDataById (Integer id){
        return fileRepo.findById(id).orElseThrow(()-> new ItemNotFoundException("Category id " + id + " does not exist"));
    }
    public String store(MultipartFile file) throws IOException {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename()) // Using the original filename from MultipartFile
                            .stream(file.getInputStream(), file.getSize(), -1) // Uploading content from MultipartFile
                            .contentType(file.getContentType()) // Setting content type
                            .build());

        }
        catch (NoSuchAlgorithmException  | InvalidKeyException | MinioException e) {
            throw new RuntimeException(e);
        }
        return file.getOriginalFilename();
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            return new InputStreamResource(
                    minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fileName)
                                    .build()));
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ServerException | InsufficientDataException | ErrorResponseException | InternalException | XmlParserException | InvalidResponseException  e) {
            throw new RuntimeException("Could not load file: " + e.getClass());
        }
    }
    public String deleteFile(String fileName) {
        System.out.println("kdd");
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error occurred: " + e);
        }
        return "File deleted successfully";
    }
    public MediaType determineMediaType(String filename){
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String[] filenameParts = filename.split("\\.");
        String fileExtension = null;
        if (filenameParts.length > 1) {
            fileExtension = filenameParts[filenameParts.length-1].toLowerCase();
        }
        if (fileExtension!=null){
            if (fileExtension.equals("png")){
                mediaType =MediaType.IMAGE_PNG;
            } else if (fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
                mediaType =MediaType.IMAGE_JPEG;
            }else if (fileExtension.equals("gif") ) {
                mediaType =MediaType.IMAGE_GIF;
            }else if (fileExtension.equals("pdf") ) {
                mediaType =MediaType.APPLICATION_PDF;
            }else if (fileExtension.equals("docx") || fileExtension.equals("doc")) {
                mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            }else if (fileExtension.equals("xls") || fileExtension.equals("xlsx")) {
                mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }else if (fileExtension.equals("pptx")) {
                mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            }else if (fileExtension.equals("mp4") ) {
                mediaType =MediaType.valueOf("video/mp4");
            }else if (fileExtension.equals("mp3") ) {
                mediaType =MediaType.valueOf("audio/mp3");
            }else if (fileExtension.equals("txt") ) {
                mediaType =MediaType.TEXT_PLAIN;
            }else if (fileExtension.equals("html") ) {
                mediaType =MediaType.TEXT_HTML;
            }else if (fileExtension.equals("xml") ) {
                mediaType =MediaType.TEXT_XML;
            }else if (fileExtension.equals("zip") ) {
                mediaType =MediaType.valueOf("application/zip");
            }else mediaType = MediaType.MULTIPART_FORM_DATA;
        }
        return mediaType;
    }


public String addFileToDB (List<String> fileName, Integer idAnnounce){
    Files newFile = new Files();
    List<Files> existingFiles = fileRepo.findByIdAnnounce(idAnnounce);

    for (String name : fileName) {
        boolean exists = false;
        if(!existingFiles.isEmpty()) {
            for (Files existingFile : existingFiles) {
                if (name.equals(existingFile.getFileName())) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            newFile.setIdAnnounce(idAnnounce);
            newFile.setFileName(name);
            fileRepo.saveAndFlush(newFile);
        }
    }
        return "Success Save";
}
public void deleteFileDB (Integer id) {
    System.out.println("nnn");
        Files data = getDataById(id);
         fileRepo.delete(data);
}

    //old
//    private final Path fileStorageLocation;
//    @Autowired
//    public FileService(FileStorageProperties fileStorageProperties) {
//        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
//                .toAbsolutePath().normalize();
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception ex) {
//            throw new RuntimeException(
//                    "Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }

//    public String store(MultipartFile file) {
//        //Upload file
//// Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        try {
//// Check if the file's name contains invalid characters
//            if (fileName.contains("..")) {
//                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//// Copy file to the target location (Replacing existing file with the same name)
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//            return fileName;
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
//        }
//    }

//    @Autowired
//    private MinioClient minioClient;
//
//    @Value("${minio.bucket-name}")
//    private String bucketName;
//

//    public Resource loadFileAsResource(String fileName) {
//        //download file
//        try {
//            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists()) {
//                return resource;
//            } else {
//                throw new RuntimeException("File not found " + fileName);
//            }
//        } catch (MalformedURLException ex) {
//            throw new RuntimeException("File operation error: " + fileName, ex);
//        }
//    }


}
