package bitplace.controller.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;

@Controller
@RequestMapping("{sub}/{service}")
public class Html_Controller{	

	@RequestMapping()
	public String view(@PathVariable("sub")String sub,
			@PathVariable("service")String service,
			HttpServletRequest request,
			HttpSession session
			){	
				
	try{
		//System.out.println("Html Controller MemNo - going to Session: "+request.getParameter("memNo"));
		//Integer memNo = Integer.parseInt(request.getParameter("memNo"));
	    //if(memNo!=null||memNo!=0){
		//session.setAttribute("memNo", memNo);
			Bean bean = new Bean();
			session.setAttribute("session_bean", bean);
	//	}	
	}catch(Exception e){}
	
		if(sub.equals("login")){			
			return "/login/"+service;
		}	
		return "/main/frame";	
	}
}
