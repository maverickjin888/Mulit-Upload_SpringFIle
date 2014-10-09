package bitplace.controller.json;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;


@Controller
@RequestMapping("/update")
public class Update_Controller {
	static Logger logger = Logger.getLogger(Update_Controller.class);
	@Autowired
  ServletContext servletContext;
	
	@Autowired
	Bit_Rep rep;
	
	@RequestMapping(value = "/infoUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String totalUpdate( Model model, MultipartHttpServletRequest request, 
			Bean bean, HttpSession session) throws Exception {

		Integer memno = (Integer)session.getAttribute("memNo");
		
		
		bean.setMemno(memno);

		if(!bean.getPwd().equals("")){
			rep.pwdUpdate(bean);
		}
		if(!bean.getEmail().equals("")){
			rep.emailUpdate(bean);
		}
		if(!bean.getPhone().equals("")){
			rep.phoneUpdate(bean);
		}

		rep.alarmUpdate(bean);


		Iterator<String> itr =  request.getFileNames();
		if(itr.hasNext()) {
			MultipartFile mpf = request.getFile(itr.next());
			System.out.println(mpf.getOriginalFilename() +" uploaded!");

			if(mpf.getOriginalFilename() != ""){
				
				String folderPath = "/javaide/workspace/Bit_Place/src/main/webapp/img/userimages/";				
				File uploadFile = new File(folderPath + bean.getGit_id()+".png");
				System.out.println("________"+uploadFile);

				mpf.transferTo(uploadFile);
				
				//File file2 = new File(uploadDir + "/" + bean.getGit_id() + "2.jpg");
				//boolean rename = uploadFile.renameTo(file2);
				System.out.println("________"+uploadFile);
				//System.out.println(file2);
				
			//	System.out.println(rename);
			
				
				
				bean.setPhoto(mpf.getOriginalFilename());

				rep.photoUpdate(bean);

				
			}else{
				System.out.println("github:"+bean.getPhoto());
				if(bean.getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
					rep.photoUpdate(bean);
				}
			}
			
		}
		return new Gson().toJson(bean);
	}
	
	
}
