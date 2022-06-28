package com.csdaa.wsn.client.controller;

import com.csdaa.wsn.client.service.FileService;
import com.csdaa.wsn.client.utils.SHAutil;
import com.csdaa.wsn.commons.entity.FileCallBack;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.commons.entity.Stu;
import com.csdaa.wsn.client.service.NettyClientService;
import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
public class DepController {
    @Autowired
    NettyClientService nettyClientService;
    @Autowired
    FileService fileService;
    @RequestMapping("/aa")
    public String test(HttpServletRequest request) throws IOException, ClassNotFoundException {
        int contentLength = request.getContentLength();
        ByteArrayOutputStream os=new ByteArrayOutputStream(contentLength);
        InputStream inputStream = request.getInputStream();
        byte []buf=new byte[1024];
        int n=0;
        Stu obj=null;
        while ((n=inputStream.read(buf))!=-1){
            System.out.println("1");
            os.write(buf,0,n);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream (os.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj =(Stu) ois.readObject();
        ois.close();
        bis.close();
        os.close();
        System.out.println(obj.toString());
        System.out.println(obj.name.length());
        return "heheheheh";
    }
//    @RequestMapping("/bbb")
//    public String testAsc(){
//        ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/dep/aaa", "123".getBytes(), HttpMethod.POST, "2");
//        System.out.println(responseMsg);
//        return "111";
//    }
    @RequestMapping("/upload")
    public FileCallBack upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return fileService.upload(file);
    }
    @RequestMapping("/download")
    public FileCallBack download(String t,String filename,String sk) throws Exception {
        return fileService.download(t,filename,sk);
    }
    @RequestMapping("/delete")
    public FileCallBack delete(String t,String localName,String upid){
        return fileService.delete(t,localName,upid);
    }
}
