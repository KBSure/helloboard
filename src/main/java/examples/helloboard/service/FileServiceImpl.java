package examples.helloboard.service;

import examples.helloboard.dao.FileDao;
import examples.helloboard.domain.FileDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    @Value("${file.save.dir}")
    String path;
    @Autowired
    FileDao fileDao;

    @Override
    public void fileUpload(MultipartHttpServletRequest mRequest) throws Exception{
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        Iterator<String> itor = fileMap.keySet().iterator();


        while(itor.hasNext()){
            String key = itor.next();
            MultipartFile mFile = fileMap.get(key);

            //파일 정보를 객체 담는다
            FileDetail fileDetail = new FileDetail();
            fileDetail.setSize(mFile.getSize());
            String fileName = mFile.getOriginalFilename();
            String ext = "";
            int extIdx = fileName.lastIndexOf(".");
            if(extIdx != -1){
                ext = fileName.substring(extIdx+1);
            }
            String mimeType = getMimeType(ext);
            fileDetail.setFormat(mimeType);
            fileDetail.setPath(path);

            //서버에 저장
            try(FileOutputStream fos = new FileOutputStream(fileName)){
                fos.write(mFile.getBytes());
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception();
            }

            //DB에 insert
            fileDao.insertFile(fileDetail);
        }
    }

    public static String getMimeType(String type){
        String mimeType = null;
        switch(type){
            case "txt":
                mimeType = "text/plain";
                break;
            case "doc":
                mimeType = "doc";
                break;
            case "gif":
                mimeType = "image/gif";
                break;
            case "jpeg":
            case "jpg":
                mimeType = "image/jpeg";
                break;
            case "zip":
                mimeType = "application/zip";
                break;
            default:
                mimeType = "application/octet";
                break;
        }
        return mimeType;
    }
}

