package bitplace.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.json.Json;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.log.Logger;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;
import bitplace.vo.EmailTest;
import bitplace.vo.Coolsms.Coolsms;
import bitplace.vo.Coolsms.SendResult;
import bitplace.vo.Coolsms.Set;

@Controller
@RequestMapping("ajax/login/{service}")
public class Login_Controller {

@Autowired
Bit_Rep repository;

	@ModelAttribute("data")
	public Object data(@PathVariable("service") String service,
			           HttpServletRequest request,
			           HttpSession session,
			           Bean bean
					   ){
			
		Object res = null;		
		switch(service){
	
		case "loginBtn" : 
			System.out.println("Login_Controller memNo: "+request.getParameter("email"));
			System.out.println("Login_Controller pwd : "+request.getParameter("pwd"));			
			bean.setEmail(request.getParameter("email"));
			bean.setPwd(request.getParameter("pwd"));
			res = repository.loginBtn(bean);
			
			bean = (Bean) res;
			System.out.println("Login_Controller bean : "+bean);
			
			if(bean!=null){
				System.out.println("Login_Controller result : "+bean.getMemno());		
				session.setAttribute("memNo", bean.getMemno());
			}else{
				System.out.println("Login_else_run");
				bean = new Bean();
				bean.setMemno(0);
				System.out.println("Login_else_run 0 given");
				res = bean;
				System.out.println("Login_else_run bean released");
			}

		break;
		
		case "idValidation" : 
			res = repository.checkIdValdation(request.getParameter("searchValue"));
		break;
		
		case "email_Reg" :
			res = repository.checkIdValdation(request.getParameter("searchValue"));			
		break;
		
		case "sendingValidation" :		
			Integer validadtionNumber = (int)(Math.random()*99999);
			
			if(validadtionNumber<10000)
				validadtionNumber+=10000;
			
			try
			{
			 EmailTest mail = new EmailTest("bitplacego2@gmail.com","123Snorlax");  
			 mail.sendMail("Bit_Place 인증번호입니다.", 
			        "Bit_Place에 가입해주셔서 감사합니다. 인증암호는 "+validadtionNumber+"입니다.",
			        "bitplacego2@gmail.com",  request.getParameter("emailCheck"));     
			 
			 
			}catch (Exception e) {
				
			}	
			res = validadtionNumber;
		break;
		
		case "registersubmit":
				res = repository.getRegisterSubmit(bean);
		break;

		case "registersubmit_withFiles":
			System.out.println("Login_________"+bean.getPhotoUpfile().getName());
			File file = new File("/Users/hongdavid/git/Bit_Place/src/main/webapp/img/userimages/"+bean.getPhotoUpfile().getName());
			
			try {
				byte[] byteArr = bean.getPhotoUpfile().getBytes();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(byteArr);
				fos.close();
				
				File file1 = new File("/Users/hongdavid/git/Bit_Place/src/main/webapp/img/userimages/"+bean.getPhotoUpfile().getName());
				File file2 = new File("/Users/hongdavid/git/Bit_Place/src/main/webapp/img/userimages/"+bean.getGit_id()+".png");
				file1.renameTo(file2);
				
				System.out.println("Login_________"+bean.getGit_id());

				bean.setPhoto(bean.getGit_id()+".png");
				System.out.println("Login_________"+bean.getPhoto());

				res = repository.getRegisterSubmit(bean);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		break;
		
		case "validation_to_github" :
			 String user = request.getParameter("git_id");
			 String password = request.getParameter("git_pwd");
			 Integer buffer = (int)(Math.random()*99999);
			 
			 try{
			 Github github = new RtGithub(user,password);
			 res = github.users().get(user).login();
			 Repo repo = github.repos().create(Json.createObjectBuilder()
								.add("name", user+buffer)
								.add("auto_init",true)
								.build());
			 github = null;
			 }catch(Exception e){}		 
		break;
		
		case "findname" : 
			String	findname;
			findname="%";
			findname+=request.getParameter("searchValue");
			findname+="%";
			res = repository.getFindName(findname);
		break;
		
		case "findBtn" :
			bean.setPhone(request.getParameter("findphone"));
			bean.setEmail(request.getParameter("findemail"));
			res = repository.getFindBtn(bean);		
			
			if(res!=null||res!="null"||res!=""){
				Coolsms coolsms = new Coolsms();
				Set set = new Set();
				set.setTo("010-3125-8607"); 
				set.setFrom(bean.getPhone()); 
				
				Integer tempPwd = (int)(Math.random()*99999);
				set.setText("BitPlace 임시번호는 "+tempPwd+"입니다."); 
				SendResult result = coolsms.send(set); 

				if (result.getErrorString() == null) {
					System.out.println("성공");			
				} else {
					System.out.println("실패");
					System.out.println(result.getErrorString()); 
				}		
			}	
		break;	
		
		}
		return res;
	}
	
	@RequestMapping
	String view(@PathVariable("service") String service){
		return "/main/login/"+service;
	}	
	
	
}
