package com.csdaa.wsn.server.controller;

import com.csdaa.wsn.commons.entity.Message;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.commons.entity.Stu;
import com.csdaa.wsn.server.service.DepService;
import com.csdaa.wsn.server.utils.HttpUtil;
import com.csdaa.wsn.server.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/dep")
public class DepController {
    @Autowired
    DepService depService;
    @RequestMapping("/checkTexist")
    public byte[] checkIsTExist(HttpServletRequest request) throws IOException, ClassNotFoundException{

        return depService.checkIsTExist(request);
    }
    @RequestMapping("/metedata")
    public byte[] uploadData(HttpServletRequest request) throws IOException {

        return depService.uploadData(request);
    }

    @RequestMapping("/aaa")
    public byte[] test() throws IOException {
        byte[]bytes=SerializeUtil.doSerialize(new Stu("aaa",11,2));
        System.out.println(new String(bytes));

        return bytes;
    }
    @RequestMapping("/verify")
    public byte[] verify(HttpServletRequest request){
        return depService.verify(request);
    }
    @RequestMapping("/download")
    public byte[] download(HttpServletRequest request){
        return depService.download(request);
    }
    @RequestMapping("/delete")
    public byte[] delete(HttpServletRequest request){
        return depService.delete(request);
    }
//    @RequestMapping("/aaa")
//    public Stu test() throws IOException {
//       byte[]bytes=SerializeUtil.doSerialize(new Stu("aaa",11,2));
//        Stu stu = (Stu) SerializeUtil.doDeserialize(bytes);
//        System.out.println(stu.toString());
////        System.out.println(new String(bytes));
//
//        return new Stu("aaa",11,2);
//    }
}
