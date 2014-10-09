package bitplace.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;

@Controller
@RequestMapping("main/{sub}/{service}")
public class Frame_Controller{	
	
	String[] pagingRedirects = {"title_Paging","title_PagingRe"};
	//loggedPage the firstpage to enter when the user login
	boolean logged = false;
		
	@Autowired
	Bit_Rep repository;
	
	@ModelAttribute("data")
	public Object data(@PathVariable("service")String service,
				       @PathVariable("sub")String sub,
					   HttpServletRequest request,
					   HttpSession session,
					   Bean bean){
		
		Object res = null;		
		Integer no = 0;
		try{
		no = (Integer)session.getAttribute("memNo");
		res = repository.firstLogin(no);		
		bean = (Bean) res;
		session.setAttribute("FirstGroup", bean.getGroupno());
				
		}catch(Exception e){}	
		
		Integer groupNo; 		
		logged = Boolean.parseBoolean(request.getParameter("logged"));

		
		switch(service){
		case "title":
			try{
			if(logged == true){
			   groupNo = bean.getGroupno();
			}else{
			   groupNo=Integer.parseInt(request.getParameter("groupNo"));
			}
			
			bean.setObject1(repository.getTitles(groupNo));
			bean.setCreator(repository.getCreatorValue(groupNo));  
			
			System.out.println("title_output_groupNo : "+groupNo);
			bean.setTotalPage((Integer) repository.getGroupAdminTitleTotal(groupNo));
			System.out.println("title_output_totalPage : "+bean.getTotalPage());
			res=bean;
			}catch(Exception e){}
			break;
			
		case "title_Paging":
			try{
			
			/*
			 * when logged groupNo gets the data 
			 * from the repository.firstLogin(no)	
			 */
				
			if(logged == true){
			   groupNo = bean.getGroupno();
			   System.out.println("title_Paging_output : "+groupNo);

			}else{
			   groupNo=Integer.parseInt(request.getParameter("groupNo"));
			   System.out.println("title_Paging_output : "+groupNo);
			}
			 
				bean.setGroupno(groupNo);
				bean.setLastPage((Integer)repository.getTitlelastOrder(groupNo));
				bean.setStartPage(Integer.parseInt(request.getParameter("startPage")));				
				bean.setObject1(repository.getTitlesPaging(bean));
				res=bean;
				}catch(Exception e){}		
				break;
		
		case "title_PagingRe":
			try{
				if(logged == true){
				   groupNo = bean.getGroupno();
				}else{
				   groupNo=Integer.parseInt(request.getParameter("groupNo"));
				}
				
				bean.setGroupno(groupNo);
			    bean.setFirstPage((Integer)repository.getTitlefirstOrder(groupNo));
				bean.setPrevPage(Integer.parseInt(request.getParameter("prevPage")));
				bean.setObject1(repository.getTitlesPagingRe(bean));
				res=bean;
				}catch(Exception e){}		
				break;			
			
		case "sub":
			 try{
			if(logged == true){
			  groupNo = bean.getGroupno();
			}else{
			  groupNo=Integer.parseInt(request.getParameter("groupNo"));
			}
			bean.setGroupno(groupNo);
			bean.setStartPage((Integer)repository.getSubsMax(groupNo));
			bean.setLastPage((Integer)repository.getSubsMin(groupNo));
			bean.setTotalPage((Integer)repository.getSubsTotal(groupNo));
			bean.setObject1(repository.getSubs(bean));		
			res = bean;
			
			 }catch(Exception e){}
			break;
		
		case "subPaging" :
			if(logged == true){
				  groupNo = bean.getGroupno();
				}else{
				  groupNo=Integer.parseInt(request.getParameter("groupNo"));
				}
			bean.setGroupno(groupNo);
			bean.setLastPage(Integer.parseInt(request.getParameter("nextPage")));
			System.out.println("subPaging_output : "+bean.getGroupno()+" "+bean.getLastPage());
			res = repository.getSubPaging(bean);
		break;		
		
		case "prevsubPaging" :
			if(logged == true){
				  groupNo = bean.getGroupno();
				}else{
				  groupNo=Integer.parseInt(request.getParameter("groupNo"));
				}
			bean.setGroupno(groupNo);
			bean.setFirstPage(Integer.parseInt(request.getParameter("prevPage")));
			
			System.out.println("prevsubPaging_output : "+bean.getGroupno()+" "+bean.getFirstPage());
			res = repository.getprevSubPaging(bean);		
		break;
			
		case "submenGroups" : 
			try{		
				res = repository.getGroups(no);
			}catch(Exception e){}
		break;	
		
		case "selectedGroup" :
			try{
			 if(logged == true){
			   groupNo = bean.getGroupno();
			 }else{
			   groupNo=Integer.parseInt(request.getParameter("groupNo"));
			 }			
			   res = repository.selectedGroup(groupNo);
			}catch(Exception e){}
		break;	
		
		case "selectedGroup_post" :
			try{
				 if(logged == true){
				   groupNo = bean.getGroupno();
				 }else{
				   groupNo=Integer.parseInt(request.getParameter("groupNo"));
				 }			
				   res = groupNo;
				}catch(Exception e){}	
		break;
		
		
		
		}		
		return res;
	}
	
	@RequestMapping
	public String view(@PathVariable("sub") String sub, 
			           @PathVariable("service") String service){
		
		for(String redirect : pagingRedirects){
			if(redirect.equals(service)){
				return "/main/frame/title_Paging";	
			}
		}
		
		if(service.equals("subPaging")||service.equals("prevsubPaging")){
			    return "/main/frame/subPaging";
		}
		
		return "/main/"+sub+"/"+service;
	}	
}
